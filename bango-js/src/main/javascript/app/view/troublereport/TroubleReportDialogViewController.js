Ext.define('Spm.view.troublereport.TroubleReportDialogViewController', {
    extend: 'Spm.component.StandardDialogViewController',
    alias: 'controller.troubleReportDialog',

    listen: {
        controller: {
            'requestAppointmentDialog': {
                updateAppointmentReference: 'onUpdateAppointmentReference'
            }
        }
    },

    onShow: function () {
        var serviceType = this.getViewModel().get('troubleReportTemplate.serviceType');
        var serviceId = this.getViewModel().get('troubleReportTemplate.serviceId');
        this.getViewModel().getStore('testProducts').load({
            params: {
                serviceType: serviceType.code
            }
        });
        this.getViewModel().getStore('symptoms').load({
            params: {
                serviceType: serviceType.code
            }
        });
        this.getViewModel().getStore('lineTest').load({
            params: {
                serviceId: serviceId
            }
        });

        var additionalNotesField = this.getView().lookupReference('additionalNotes');
        var maxInputLength = ('RoiOffnetVoice' === serviceType.code
                             || 'RoiRuralOffnetBroadband' === serviceType.code
                             || 'RoiUrbanOffnetBroadband' === serviceType.code
                             || 'RoiFttc' === serviceType.code) ? 150 : 2000;

        additionalNotesField.validator = Ext.bind(this.maxNotesLengthValidator, this, [additionalNotesField, maxInputLength]);
        this.forceAdditionalNotesMaxLength(additionalNotesField, maxInputLength);
    },

    forceAdditionalNotesMaxLength: function(additionalNotesField, maxInputLength) {
        additionalNotesField.inputEl.set({ maxLength: maxInputLength });
    },

    maxNotesLengthValidator: function(additionalNotesField, maxInputLength) {
        return additionalNotesField.getValue() && additionalNotesField.getValue().length >= maxInputLength ? "The max length for notes is "+maxInputLength : true;
    },

    onUpdateAppointmentReference: function (appointmentReference) {
        this.getViewModel().set('troubleReportTemplate.appointmentReference', appointmentReference);
    },

    onValidityChange: function (form, isValid) {
        this.lookupReference('acceptButton').setDisabled(!isValid);
    },

    onExpand: function () {
        this.getViewModel().getStore('lineTest').reload();
    },

    onAccept: function () {
        if (this.lookupReference('troubleReportForm').isValid()) {
            var troubleReportTemplate = this.getViewModel().get('troubleReportTemplate');

            troubleReportTemplate.copy(null).save({
                    scope: this,
                    failure: function () {
                        console.log('fail');
                    },
                    success: function () {
                        this.getView().close();
                    }
                }
            );
        }
    },

    onRequestAppointment: function () {
        Ext.create('Spm.view.troublereport.requestappointment.RequestAppointmentDialog', {
            viewModel: {
                data: {
                    serviceProblemId: this.getViewModel().get('troubleReportTemplate.serviceProblemId'),
                    serviceType: this.getViewModel().get('troubleReportTemplate.serviceType')
                }
            }
        }).show();
    }

});