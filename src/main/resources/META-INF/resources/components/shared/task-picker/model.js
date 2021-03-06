var TaskPickerModel= function(params){

	var self = this;

	self.system = ko.observable().publishOn(params.view+"_system", true);
	self.task = ko.observable().publishOn(params.view+"_task", true);
	self.tasks = ko.observableArray();
	
	ko.postbox.subscribe("global"+"_tasks-changed", function(ts) {
		self.loadAllTasks();
	});
	
	self.loadAllTasks = function() {
		$.get("api/task/public", function(tasks) {
			self.tasks(tasks);
		});
	};
	
	self.loadSelectedTask = function(task) {
		
		console.debug("TASK: selected, publishing system.");
		
		self.task(task);
		var systemString = JSON.stringify(task.system);
		self.system({system: systemString, hasStructuralChange : true});
	};
	
	self.init = function() {
		self.loadAllTasks();
	};
};

ko.components.register('task-picker', {
    template: { require: 'text!components/shared/task-picker/template.html' },
    viewModel: TaskPickerModel
});