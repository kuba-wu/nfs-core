var TaskHeaderModel = function(params) {
	
	var self = this;
	self.task = ko.observable().subscribeTo(params.view+"_task", false);
};

$(document).ready(function() {
	ko.components.register('task-header', {
	    template : {require: 'text!components/shared/task-header/template.html'},
	    viewModel : TaskHeaderModel
	});
});