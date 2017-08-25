var SimRunModel = function(params){
	
	var self = this;
	
	self.loadDataId = params.view+"_loadData";
		
	self.simRun = ko.observable().publishOn(params.view+"_simRun", true);
	self.runTime = ko.observable(200).syncWith(params.view+"_runTime", true, true);
	self.timeScale = ko.observable(10).syncWith(params.view+"_timeScale", true, true);
		
	self.submit = function() {
		self.simRun({
			environment : {
				runTime : self.runTime(),
				timeScale : self.timeScale()
			},
			timeStamp : new Date()
		});
		self.setNoSimulationRun();
	};

	self.setSimulationRunNeeded = function() {
		$('#'+self.loadDataId).removeClass('btn-primary');
		$('#'+self.loadDataId).addClass('btn-danger');
	};
	
	self.setNoSimulationRun = function() {
		$('#'+self.loadDataId).addClass('btn-primary');
		$('#'+self.loadDataId).removeClass('btn-danger');
	};	
	
	ko.postbox.subscribe(params.view+"_system", function(system) {
		self.setSimulationRunNeeded();
	});
}

$(document).ready(function() {
	ko.components.register('sim-run', {
	    template: {require: 'text!components/shared/sim-run/template.html'},
	    viewModel: SimRunModel
	});
});