/*
 * File: app/view/AppContainer.js
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

Ext.define('Spm.view.AppContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.appContainer',

    requires: [
        'Spm.view.TabPanel',
        'Spm.view.NavigationPanel',
        'Spm.view.HeaderView'
    ],

    itemId: 'appContainer',
    layout: {
        type: 'border'
    },

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'tabPanel',
                    region: 'center'
                },
                {
                    xtype: 'navigationPanel',
                    region: 'west'
                },
                {
                    xtype: 'headerView',
                    height: 60,
                    region: 'north'
                }
            ]
        });

        me.callParent(arguments);
    }

});