var ParametersEditorModel = function(params){

	var self = this;
	
	self.containerId = params.view+"_parametersEditor";
	
	self.createSystem = function(id, parentId, name, description, components, receptors, init) {
		return {
			id: id,
			parentId: parentId,
			name: name,
			description : description,
			components: components,
			receptors: receptors,
			init: init,
			equals : function(other) {
				var ide = (this.id == other.id);
				var parentIde = (this.parentId == other.parentId);
				var componentse = (this.components.length == other.components.length);
				if (componentse) {
					for (var i=0, n=this.components.length; i < n; i++) {
						var nfis = this.components[i];
						var nfisOther = other.components[i];
						componentse = componentse && (nfis.effector.production == nfisOther.effector.production)
						componentse = componentse && (nfis.effector.outflow == nfisOther.effector.outflow)
						componentse = componentse && (nfis.receptor.delay == nfisOther.receptor.delay)
						
						componentse = componentse && (nfis.receptor.thresholds.length == nfisOther.receptor.thresholds.length);
						if (componentse) {
							for (var j = 0, m = nfis.receptor.thresholds.length; j < m; j++) {
								componentse = componentse && (nfis.receptor.thresholds[j].value == nfisOther.receptor.thresholds[j].value);
							}
						}
					}
				}
				var receptorse = (this.receptors.length == other.receptors.length);
				if (receptorse) {
					for (var i=0, n=this.receptors.length; i < n; i++) {
						var receptor = this.receptors[i];
						var receptorOther = other.receptors[i];
						receptorse = receptorse && (receptor.delay == receptorOther.delay);
						
						receptorse = receptorse && (receptor.thresholds.length == receptorOther.thresholds.length);
						if (receptorse) {
							for (var j = 0, m = receptor.thresholds.length; j < m; j++) {
								receptorse = receptorse && (receptor.thresholds[j].value == receptorOther.thresholds[j].value);
							}
						}
					}
				}
				var initse = (this.init.length == other.init.length);
				if (initse) {
					for (var i=0, n=this.init.length; i < n; i++) {
						var init = this.init[i];
						var initOther = other.init[i];
						var keys = Object.keys(init);
						var keysOther = Object.keys(initOther);
						
						initse = initse && (keys.length == keysOther.length);
						if (initse) {
							for (var j=0, m=keys.length; j < m; j++) {
								initse = initse && (init[keys[j]] == initOther(keys[j]));
							}
						}
					}
				}
				
				return (ide && parentIde && componentse && receptorse && initse);
			}
		};
	}; 
	
	self.system = ko.mapping.fromJS({
		system : self.createSystem("", null, "", "", [], [], [])
	});
	
	self.originalSystem = null;
	
	self.systemWithParams = ko.observable().publishOn(params.view+"_system", true);
	self.hasStructuralChange = ko.observable().syncWith(params.view+"_hasStructuralChange", true);
	
	self.formatLabel = function(threshold) {
		
		return (typeof threshold.signal !== "function" || threshold.signal() == null 
				? (threshold.product()) 
				: (threshold.signal()+" / "+threshold.product()));
	};
		
	self.toModel = function(systemObject) {
		
		return {
			system : self.createSystem(systemObject.id, systemObject.parentId, systemObject.name, systemObject.description, systemObject.components, systemObject.receptors,  [systemObject.init])
		};
	};
	
	self.fromModel = function() {
		var unmapped = ko.mapping.toJS(self.system).system;
		var init = (unmapped.init.length == 0 ? {} : unmapped.init[0]);
		return self.createSystem(unmapped.id, unmapped.parentId, unmapped.name, unmapped.description, unmapped.components, unmapped.receptors, init);
	};
	
	ko.postbox.subscribe(params.view+"_system", function(system) {
		
		console.debug("PARAMS: got new system. Strucutral changes? "+self.hasStructuralChange());
		self.set(system);
	});
	
	self.set = function(systemAsString) {
		
		var systemObject = JSON.parse(systemAsString);
		self.originalSystem = systemObject;
		var system = self.toModel(systemObject);

		ko.mapping.fromJS(system, self.system);
		self.subscribeToModel(self.setSystemParams);
		$('input.property').TouchSpin({
			verticalbuttons: true,
			decimals: 0,
			step: 1
		});
		$('input.double-type-property').TouchSpin({
			verticalbuttons: true,
			decimals: 2,
			step: 0.01
		});
	};
	
	self.subscribeToModel = function(callback) {
		for (var i=0, n=self.system.system.components().length; i < n; i++) {
			var nfis = self.system.system.components()[i];
			nfis.effector.production.subscribe(callback);
			nfis.effector.outflow.subscribe(callback); 
			nfis.receptor.delay.subscribe(callback);
			for (var j = 0, m = nfis.receptor.thresholds().length; j < m; j++) {
				var threshold = nfis.receptor.thresholds()[j];
				threshold.value.subscribe(callback);
			}
		}
		for (var i=0, n=self.system.system.receptors().length; i < n; i++) {
			var receptor = self.system.system.receptors()[i];
			receptor.delay.subscribe(callback);
			for (var j = 0, m = receptor.thresholds().length; j < m; j++) {
				var threshold = receptor.thresholds()[j];
				threshold.value.subscribe(callback);
			}
		}
		for (var i=0, n=self.system.system.init().length; i < n; i++) {
			var init = self.system.system.init()[i];
			var keys = Object.keys(init);
			for (var j=0, m=keys.length; j < m; j++) {
				init[keys[j]].subscribe(callback);
			}
		}
	};
		
	self.setSystemParams = function() {
		
		var system = self.fromModel();

		if (system.equals(self.originalSystem)) {
			console.debug("PARAMS: No system differences upon property set");
		} else {
			console.debug("PARAMS: System changed upon property set, propagating.");
			self.hasStructuralChange(false);
			self.systemWithParams(JSON.stringify(system));
		}
	};
};

ko.components.register('parameters-editor', {
    template: { require: 'text!components/shared/parameters-editor/template.html' },
    viewModel: ParametersEditorModel
});