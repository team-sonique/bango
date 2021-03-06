Ext.define('Spm.view.dashboard.agent.AgentDashboardTab', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.agentDashboard',

    requires: [
        'Ext.grid.feature.Grouping',
        'Spm.view.component.autorefresh.AutoRefreshToolbar',
        'Spm.view.dashboard.agent.AgentDashboardViewController',
        'Spm.view.dashboard.agent.AgentDashboardViewModel'
    ],

    viewModel: {type: 'agentDashboard'},
    controller: 'agentDashboard',

    listeners: {
        activate: 'loadStore'
    },

    bind: {
        store: '{agents}'
    },

    title: 'Agent Dashboard',
    iconCls: 'icon-agent-dashboard',
    closable: false,

    dockedItems: [
        {
            xtype: 'autorefreshtoolbar',
            border: 0,
            bind: {
                store: '{agents}'
            }
        }
    ],

    columns: {
        defaults: {
            menuDisabled: true
        },
        items: [
            {text: 'Username', dataIndex: 'agentCode', width: 250},
            {text: 'Status', dataIndex: 'agentAvailability', align: 'center', renderer: 'agentStatusRenderer'},
            {text: '# Assigned Items', dataIndex: 'assignedWorkItemCount', align: 'right'},
            {
                text: 'Duration (Minutes)',
                dataIndex: 'availabilityChangeDate',
                align: 'right',
                renderer: 'availabilityDurationRenderer'
            }
        ]
    },
    features: [{
        ftype: 'grouping',
        enableGroupingMenu: false,
        groupHeaderTpl: 'Team: {name} ({children.length:plural("Agent")})'
    }]
});
