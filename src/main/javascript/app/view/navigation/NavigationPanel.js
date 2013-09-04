Ext.define('Spm.view.navigation.NavigationPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.navigationPanel',

    requires: [
        'Spm.view.navigation.AgentStatusPanel',
        'Spm.view.navigation.MyQueuesPanel',
        'Spm.view.navigation.SearchPanel'
    ],

    height: 730,
    width: 197,
    layout: {
        align: 'stretch',
        type: 'vbox'
    },

    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'agentStatusPanel'
                },
                {
                    xtype: 'myQueuesPanel',
                    collapsible:true
                },
                {
                    xtype: 'searchPanel',
                    collapsible:true
                }
            ]
        });

        me.callParent(arguments);
    }

});