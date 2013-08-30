Ext.define('Spm.view.application.AppContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.appContainer',

    requires: [
        'Ext.tab.Panel',
        'Ext.layout.container.Border',
        'Spm.view.navigation.NavigationPanel',
        'Spm.view.application.HeaderView'
    ],

    hidden: true,
    itemId: 'appContainer',
    layout: {
        type: 'border'
    },

    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'tabpanel',
                    region: 'center',
                    id: 'tab-panel',
                    items: [ {
                        xtype: 'panel',
                        title: 'My Items'
                    }
                    ]
                },
                {
                    xtype: 'navigationPanel',
                    region: 'west'
                },
                {
                    xtype: 'headerView',
                    region: 'north'
                }
            ]
        });

        me.callParent(arguments);
    }

});