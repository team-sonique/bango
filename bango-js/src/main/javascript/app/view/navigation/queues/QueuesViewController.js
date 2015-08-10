Ext.define('Spm.view.navigation.queues.QueuesViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.myQueues',

    listen: {
        controller: {
            'superman': {
                authenticated: 'onAuthenticated'
            }
        },
        component: {
            '#queues-view': {
                select: 'onAgentQueueSelect'
            }
        }
    },

    onAgentQueueSelect: function(dataViewModel, selectedQueue) {
        this.fireEvent('agentQueueSelected', selectedQueue);
    },

    onAuthenticated: function(authenticatedAgent) {
        var hasAssignedQueues = authenticatedAgent.hasPrivilege('HasAssignedQueues');
        this.getView().setVisible(hasAssignedQueues);

        if (hasAssignedQueues) {
            var queueStore = this.getStore('agentQueues');
            queueStore.loadRawData(authenticatedAgent.getData());
        }
    }

});