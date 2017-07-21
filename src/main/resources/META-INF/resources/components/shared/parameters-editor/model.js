var ParametersEditorModel = function(params){

	var self = this;
	
	self.containerId = params.view+"_parametersEditor";
	
	self.setParamsButtonId = params.view+"_setParamsButton";
	
	self.system = ko.mapping.fromJS({
		system : {
			id: "",
			name: "",
			description : "",
			components: [],
			receptors: [],
			inits: []
		}
	});
	self.formatLabel = function(threshold) {
		
		return (typeof threshold.signal !== "function" || threshold.signal() == null 
				? (threshold.product()) 
				: (threshold.signal()+" / "+threshold.product()));
	};
		
	self.toModel = function(systemObject) {
		
		return {
			system : {
				id: systemObject.id,
				name : systemObject.name,
				description : systemObject.description,
				components : systemObject.components,
				receptors : systemObject.receptors,
				inits : [systemObject.init]
			}
		};
	};
	
	self.fromModel = function() {
		var unmapped = ko.mapping.toJS(self.system).system;
		var init = (unmapped.inits.length == 0 ? {} : unmapped.inits[0]);
		return JSON.stringify({
			id: unmapped.id,
			name: unmapped.name,
			description: unmapped.description,
			components : unmapped.components,
			receptors : unmapped.receptors,
			init : init
		});
	};
	
	ko.postbox.subscribe(params.view+"_system", function(system) {
		self.set(system);
	});
	
	self.set = function(systemAsString) {
		
		var systemObject = JSON.parse(systemAsString);
		var system = self.toModel(systemObject);
		ko.mapping.fromJS(system, self.system);
		self.subscribeToModel(self.setEditorDirtyState);
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
		self.setEditorCleanState();
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
		for (var i=0, n=self.system.system.inits().length; i < n; i++) {
			var init = self.system.system.inits()[i];
			var keys = Object.keys(init);
			for (var j=0, m=keys.length; j < m; j++) {
				init[keys[j]].subscribe(callback);
			}
		}
	};
	
	self.systemWithParams = ko.observable().publishOn(params.view+"_system", true);
	self.hasStructuralChange = ko.observable().publishOn(params.view+"_hasStructuralChange", true);
	
	self.setSystemParams = function() {
		
		var system = self.fromModel();
		self.hasStructuralChange(false);
		self.systemWithParams(system);
	};
	
	self.setEditorDirtyState = function() {
		$('#'+self.setParamsButtonId).removeClass('btn-primary');
		$('#'+self.setParamsButtonId).addClass('btn-danger');
	};
	
	self.setEditorCleanState = function() {
		$('#'+self.setParamsButtonId).addClass('btn-primary');
		$('#'+self.setParamsButtonId).removeClass('btn-danger');
	};
};

ko.components.register('parameters-editor', {
    template: { require: 'text!components/shared/parameters-editor/template.html' },
    viewModel: ParametersEditorModel
});