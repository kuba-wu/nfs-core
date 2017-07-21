var SimRunModel = function(params){
	
	var self = this;
	
	self.firstRun = true;
	self.loadDataId = params.view+"_loadData";
	
	var comparer = function(newValue, oldValue) {
	    return false;
	};
	
	self.submissionFlag = ko.observable(false).publishOn(params.view+"_submissionFlag", true);
	self.runTime = ko.observable(100).publishOn(params.view+"_runTime", false, comparer);
	self.runTime.extend({ notify: 'always' });
	self.timeScale = ko.observable(10).publishOn(params.view+"_timeScale", false, comparer);
	self.runTime.extend({notify: 'always'});
		
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
	
	ko.postbox.subscribe(params.view+"_graph", function(graph) {
		self.setSimulationRunNeeded();
	});
}

$(document).ready(function() {
	ko.components.register('sim-run', {
	    template: {require: 'text!components/shared/sim-run/template.html'},
	    viewModel: SimRunModel
	});
});