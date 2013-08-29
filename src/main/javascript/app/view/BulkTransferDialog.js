Ext.define('Spm.view.BulkTransferDialog', {
    extend: 'Spm.view.StandardDialog',
    alias: 'widget.bulkTransferDialog',

    height: 250,
    width: 450,
    iconCls: 'icon-transfer',
    title: 'Bulk Transfer',

    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            content: {
                id: 'bulk-transfer-view',
                xtype: 'dataview',
                cls: 'bulk-transfer-view',

                tpl: [
                    '<div id="transfer-queues-group">',
                    '   <tpl for=".">',
                    '      <div id="transfer-{name}" class="queue x-view-item">{name}</div>',
                    '   </tpl>',
                    '</div>'
                ],
                itemSelector: 'div.queue',
                overItemCls: 'x-item-over',
                store: 'AllQueues',
                trackOver: true,
                autoScroll: true,
                margin: 5,
                border: 1,
                style: {
                    borderColor: '#bcb1b0',
                    borderStyle: 'solid'
                }
            }
        });

        me.callParent(arguments);
    }
});