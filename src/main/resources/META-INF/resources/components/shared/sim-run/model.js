var SimRunModel = function(params){
	
	var self = this;
	
	self.firstRun = true;
	self.loadDataId = params.view+"_loadData";
	
	self.submissionFlag = ko.observable(false).publishOn(params.view+"_submissionFlag", true);
	self.runTime = ko.observable(200).syncWith(params.view+"_runTime", true, true);
	self.timeScale = ko.observable(10).syncWith(params.view+"_timeScale", true, true);
		
	self.submit = function() {
		if (self.firstRun) {
			self.runTime(self.runTime());
			self.timeScale(self.timeScale());
			self.firstRun = false;
		}
		self.submissionFlag(!self.submissionFlag());
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