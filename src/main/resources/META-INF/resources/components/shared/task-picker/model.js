var TaskPickerModel= function(params){

	var self = this;

	self.system = ko.observable().publishOn(params.view+"_system", true);
	self.task = ko.observable().publishOn(params.view+"_task", true);
	self.hasStructuralChange = ko.observable().publishOn(params.view+"_hasStructuralChange", true);
	self.tasks = ko.observableArray();
	
	ko.postbox.subscribe("global"+"_tasks-changed", function(ts) {
		self.loadAllTasks();
	});
	
	self.loadAllTasks = function() {
		$.get("/nfs/api/task/public", function(tasks) {
			self.tasks(tasks);
		});
	};
	
	self.loadSelectedTask = function(task) {
		self.task(task);
		self.system(JSON.stringify(task.system));
		self.hasStructuralChange(true);
	};
	
	self.init = function() {
		self.loadAllTasks();
	};
};

ko.components.register('task-picker', {
    template: { require: 'text!components/anonymous/task-picker/template.html' },
    viewModel: TaskPickerModel
});