/*
 * File: app/view/MyQueuesPanel.js
 *
 * This file was generated by Sencha Architect version 2.2.2.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('Spm.view.MyQueuesPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.myQueuesPanel',

    cls: 'my-queues-panel',
    layout: {
        type: 'fit'
    },
    animCollapse: false,
    collapsible: true,
    title: 'My Queues',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'dataview',
                    prepareData: function(data, recordIndex, record) {
                        console.log(record.getData(true));
                        console.log(data);
                        return data;
                    },
                    id: 'queue-chooser-view',
                    itemTpl: Ext.create('Ext.XTemplate', 
                        '<tpl for=".">',
                        '	<div class="queue-wrap" id="queue-{id}">',
                        '		{name}',
                        '	</div>',
                        '</tpl>',
                        {
                            foo: function(stuff) {
                                console.log(stuff);
                            }
                        }
                    ),
                    overItemCls: 'x-item-selected',
                    store: 'AuthenticatedAgent',
                    trackOver: true
                }
            ]
        });

        me.callParent(arguments);
    }

});