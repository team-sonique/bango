Ext.define('Spm.view.troublereport.requestappointment.RequestAppointmentDialog', {
    extend: 'Spm.component.StandardDialog',
    alias: 'widget.requestAppointmentDialog',

    viewModel: {type: 'requestAppointmentDialog'},
    controller: 'requestAppointmentDialog',

    title: 'Request Appointment',
    iconCls: 'icon-create-trouble-report',

    height: 422,
    width: 396,

    items: [
        {
            xtype: 'form',
            bodyPadding: 10,
            listeners: {
                validitychange: 'onValidityChange'
            },
            reference: 'requestAppointmentForm',
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Repair Type',
                                    bind: {
                                        value: '{requestAppointment.type}'
                                    },
                                    margin: '5 0 0 0',
                                    labelWidth: 160,
                                    reference: 'repairType',
                                    displayField: 'repairType',
                                    valueField: 'repairType',
                                    store: {
                                        fields: ['repairType'],
                                        data: [
                                            ['Standard Repair'],
                                            ['SFI Repair']
                                        ]
                                    },
                                    allowBlank: false,
                                    typeAhead: true,
                                    forceSelection: true
                                },
                                {
                                    xtype: 'datefield',
                                    fieldLabel: 'Appointment Start Date',
                                    bind: {
                                        value: '{requestAppointment.date}'
                                    },
                                    allowBlank: false,
                                    format: 'd/m/Y',
                                    formatText: null,
                                    minValue: new Date(),
                                    margin: '5 0 0 0',
                                    labelWidth: 160
                                }
                            ]
                        },
                        {
                            xtype: 'fieldcontainer',
                            layout: {
                                type: 'vbox',
                                align: 'right'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    text: 'Fetch Appointments',
                                    margin: '15 0 0 0',
                                    handler: 'onFetchAppointments'
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            reference: 'appointment',
                            layout: {
                                type: 'card'
                            },
                            items: [
                                {
                                    xtype: 'container',
                                    reference: 'no-appointment-panel',
                                    layout: {
                                        type: 'vbox',
                                        align: 'stretch'
                                    },
                                    height: 87,
                                    items: [
                                        {
                                            xtype: 'label',
                                            cls: 'no-work-item-text',
                                            text: 'Please enter earliest date and fetch appointments'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'gridpanel',
                                    reference: 'available-appointment-grid',
                                    disableSelection: true,
                                    bind: {
                                        store: '{availableAppointments}'
                                    },
                                    viewConfig: {
                                        listeners: {
                                            itemupdate: function (record, index, node) {
                                                console.log(record);
                                                console.log(node);
                                            },
                                            click: function(){
                                                console.log('click..');
                                            }
                                        }
                                    },
                                    height: 220,
                                    columns: [
                                        {
                                            text: 'Appointment Date',
                                            xtype: 'datecolumn',
                                            flex: 0.6,
                                            dataIndex: 'appointmentDate'
                                        },
                                        {
                                            text: 'AM',
                                            xtype: 'widgetcolumn',
                                            flex: 0.2,
                                            dataIndex: 'amTimeslotAvailable',
                                            widget: {
                                                xtype: 'radio',
                                                //listeners: {
                                                //    change: function (rb, nv, ov) {
                                                ////        console.log('am ' + 'new ' + nv + ' old ' + ov);
                                                ////        //console.log(rb);
                                                //        //debugger;
                                                //        if (rb.getWidgetRecord()) {
                                                //            console.log('am');
                                                //        //    console.log(rb.getWidgetRecord());
                                                //            rb.setDisabled(!rb.getWidgetRecord().get('amTimeslotAvailable'));
                                                //        }
                                                ////        //rb.setDisabled(!rb.getData().get('pmTimeslotAvailable'));
                                                //    }
                                                //}
                                            }
                                        },
                                        {
                                            text: 'PM',
                                            flex: 0.2,
                                            xtype: 'widgetcolumn',
                                            dataIndex: 'pmTimeslotAvailable',
                                            widget: {
                                                xtype: 'radio',
                                                //listeners: {
                                                //    change: function (rb, nv, ov) {
                                                ////        console.log('pm ' + 'new ' + nv + ' old ' + ov);
                                                ////        //        //console.log(rb);
                                                //        if (rb.getWidgetRecord()) {
                                                //            console.log('pm');
                                                //        //    console.log(rb.getWidgetRecord());
                                                //            rb.setDisabled(!rb.getWidgetRecord().get('pmTimeslotAvailable'));
                                                //        }
                                                ////        //        //rb.setDisabled(!nv);
                                                //    }
                                                //}
                                            }
                                        }
                                    ]
                                }
                            ]
                        }]
                }
            ]

        }]
});