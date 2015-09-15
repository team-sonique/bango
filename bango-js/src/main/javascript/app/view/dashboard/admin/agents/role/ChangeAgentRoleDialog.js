Ext.define('Spm.view.dashboard.admin.agents.role.ChangeAgentRoleDialog', {
    extend: 'Spm.component.StandardDialog',
    alias: 'widget.changeAgentRoleDialog',

    requires: [
        'Spm.view.dashboard.admin.agents.role.ChangeAgentRoleDialogViewController',
        'Spm.view.dashboard.admin.agents.role.ChangeAgentRoleDialogViewModel'
    ],

    viewModel: 'changeAgentRoleDialog',
    controller: 'changeAgentRoleDialog',

    bind: {
        title: 'Change Role For {agent.displayName}'
    },
    iconCls: 'icon-admin-agent-change-role',
    defaultFocus: 'roleComboBox',
    width: 400,
    height: 130,

    items: [{
        xtype: 'fieldcontainer',
        padding: 10,

        defaults: {
            xtype: 'combobox',
            labelWidth: 120,
            typeAhead: true,
            queryMode: 'local',
            listeners: {
                specialkey: 'submitOnEnter'
            }
        },
        items: [
            {
                xtype: 'combobox',
                name: 'Role',
                itemId: 'roleComboBox',
                reference: 'roleComboBox',
                fieldLabel: 'Role',
                valueField: 'name',
                displayField: 'description',
                bind: {
                    store: '{roles}'
                },
                listeners: {
                    select: 'onRoleSelected'
                }
            },
            {
                xtype: 'combobox',
                name: 'Team',
                reference: 'teamComboBox',
                fieldLabel: 'Team',
                valueField: 'id',
                displayField: 'name',
                bind: {
                    store: '{teams}'
                }
            }
        ]
    }]

});