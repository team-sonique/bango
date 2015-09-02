Ext.define('Spm.view.admindashboard.queues.update.UpdateQueueDialogViewModel', {
    extend: 'Spm.component.StandardDialogViewModel',
    alias: 'viewmodel.updateQueueDialog',

    data: {
        queue: null
    },

    stores: {
        queueDomains: {
            fields: ['name'],
            data: [
                ['SNS'],
                ['ThirdParty'],
                ['CST'],
                ['Project'],
                ['ROI'],
                ['BTIreland']
            ]
        }
    },

    formulas: {
        acceptButtonDefaultDisabled: {
            bind: {
                bindTo: '{queue.name}'
            },
            get: function (name) {
                return !name;
            }
        }
    },

    queue: function () {
        return this.get('queue');
    }

});