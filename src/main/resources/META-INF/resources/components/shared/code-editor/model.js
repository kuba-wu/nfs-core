var CodeEditorModel = function(params) {
	
	var self = this;
	
	self.containerId = params.view+"_codeEditor";
	self.submitSimulationDefButtonId = params.view+"_submitSimulationDefButton"
	
	self.editor = {};
	self.system = ko.observable().publishOn(params.view+"_system", true);
	self.hasStructuralChange = ko.observable().publishOn(params.view+"_hasStructuralChange", true);
	
	ko.postbox.subscribe(params.view+"_system", function(system) {
		self.set(system);
	});
		
	self.init = function() {
		$('#'+self.containerId+'[data-editor]').each(function () {
			
            var textarea = $(this);
            var editDiv = $('<div>', {
                position: 'absolute',
                'class': textarea.attr('class')
            }).insertBefore(textarea);
 
            self.editor = ace.edit(editDiv[0]);
            var editor = self.editor;
            editor.setFontSize(11);
            editor.renderer.setShowGutter(false);
            editor.renderer.setShowInvisibles(false);
            editor.getSession().setTabSize(2);
            editor.getSession().setValue(textarea.val());
            editor.getSession().setMode("ace/mode/" + textarea.data('editor'));
            editor.getSession().setUseWrapMode(true);
            editor.setOptions({maxLines: 24, minLines: 24});
            editor.on("change", self.setEditorDirtyState);
		});
	};
	
	self.set = function(systemAsString) {
		self.editor.getSession().setValue(vkbeautify.json(systemAsString, 2));
		self.setEditorCleanState();
	};
	
	self.format = function() {
		self.editor.getSession().setValue(vkbeautify.json(self.getSimulationDef(), 2));
	};
	
	self.setEditorDirtyState = function() {
		$('#'+self.submitSimulationDefButtonId).removeClass('btn-primary');
		$('#'+self.submitSimulationDefButtonId).addClass('btn-danger');
	};
	
	self.setEditorCleanState = function() {
		$('#'+self.submitSimulationDefButtonId).addClass('btn-primary');
		$('#'+self.submitSimulationDefButtonId).removeClass('btn-danger');
	};
	
	self.submitSimulationDef = function() {
		console.debug("CODE: hanged - publishing changed system.");
		self.hasStructuralChange(true);
		self.system(self.getSimulationDef());
	};
	
	self.getSimulationDef = function() {
		return self.editor.getSession().getValue();
	}
};

$(document).ready(function() {
	ko.components.register('code-editor', {
	    template: {require: 'text!components/shared/code-editor/template.html'},
	    viewModel: CodeEditorModel
	});
});