Ext.define('Spm.view.SearchPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.searchPanel',

    cls: 'search-panel',
    title: 'Search',
    layout: 'vbox',
    collapsible: true,
    margin: 3,

    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'radiogroup',
                    columns: 1,
                    margin: 3,
                    items: [
                        {
                            boxLabel: 'Service Problem ID',
                            name: 'searchType',
                            inputValue: 'serviceProblemId'
                        },
                        {
                            boxLabel: 'Service ID',
                            name: 'searchType',
                            inputValue: 'serviceId'
                        },
                        {
                            boxLabel: 'Directory Number',
                            name: 'searchType',
                            inputValue: 'directoryNumber'
                        },
                        {
                            boxLabel: 'MSP ID',
                            name: 'searchType',
                            inputValue: 'mspId'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    margin: 3,
                    width: '100%',
                    defaults: {
                        width: '100%'
                    },
                    items: [
                        {
                            xtype: 'textfield'
                        },
                        {
                            xtype: 'button',
                            text: 'Search'
                        }
                    ]
                }


            ]});

        me.callParent(arguments);
    }
});