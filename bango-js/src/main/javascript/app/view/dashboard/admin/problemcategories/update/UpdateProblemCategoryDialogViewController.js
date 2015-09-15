Ext.define('Spm.view.dashboard.admin.problemcategories.update.UpdateProblemCategoryDialogViewController', {
    extend: 'Spm.component.StandardDialogViewController',
    alias: 'controller.updateProblemCategoryDialog',

    onAccept: function () {
        this.closeView();
    },

    onCancel: function () {
        this.callParent();
    },

    loadStore: function() {
        this.getViewModel().getStore('queues').load();
    }

});
