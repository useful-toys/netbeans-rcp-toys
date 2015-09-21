/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.api;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;
import javax.swing.JPanel;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;

/**
 *
 * @author Daniel Felix Ferber
 */
public class NetbeansDialogConvention<Inbound, Outbound> {

    public static <Inbound, Outbound, T extends JPanel & DialogConvention.Support<Inbound, Outbound>> NetbeansDialogConvention<Inbound, Outbound> create(T panel, String title) {
        final NetbeansDialogConvention<Inbound, Outbound> ndc = new NetbeansDialogConvention<>(panel, panel.getDialogConvention(), title);
        return ndc;
    }

    private final JPanel panel;
    private final DialogConvention<Inbound, Outbound> dialogConvention;
    private final DialogDescriptor dialogDescriptor;
    private final NotificationLineSupport notificationLine;
    private final Inbound inbound;
    private final Outbound outbound;
    private Exception exception;

    private NetbeansDialogConvention(JPanel panel, DialogConvention<Inbound, Outbound> dialogConvention, String title) {
        this.panel = panel;
        this.dialogConvention = dialogConvention;
        this.dialogDescriptor = new DialogDescriptor(panel, title);
        this.dialogDescriptor.setClosingOptions(null);
        this.dialogDescriptor.setModal(true);
        this.dialogDescriptor.setLeaf(true);
        this.notificationLine = this.dialogDescriptor.createNotificationLineSupport();
        this.inbound = dialogConvention.createInbound();
        this.outbound = dialogConvention.createOutbound();
    }

    protected class DialogConventionPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (DialogState.PROP_MESSAGE.equals(evt.getPropertyName())) {
                updateDialogDescriptorValidity();
                updateDialogDescriptorMessage();
            }
        }
    }

    public Inbound getInbound() {
        return inbound;
    }

    public Outbound getOutbound() {
        return outbound;
    }

    public DialogState getDialogState() {
        return dialogConvention.getDialogState();
    }

    protected void updateDialogDescriptorMessage() {
        final DialogState.Message message = dialogConvention.getDialogState().getMessage();
        if (message instanceof DialogState.ErrorMessage) {
            notificationLine.setErrorMessage(message.text);
        } else if (message instanceof DialogState.WarnMessage) {
            notificationLine.setWarningMessage(message.text);
        } else {
            notificationLine.setInformationMessage(message.text);
        }
    }

    protected void updateDialogDescriptorValidity() {
        dialogDescriptor.setValid(dialogConvention.getDialogState().isOkAllowed());
    }

    protected void updateDialogDescriptorDefaultMessage() {
        notificationLine.setInformationMessage(dialogConvention.getDialogState().getDefaultMessage());
    }

    public void show() {
        dialogDescriptor.setOptions(new Object[] {DialogDescriptor.OK_OPTION});
        if (inbound != null) {
            dialogConvention.toFields(inbound);
        }
        this.updateDialogDescriptorDefaultMessage();
        DialogDisplayer.getDefault().notify(dialogDescriptor);
    }

    public Outbound edit() {
        dialogDescriptor.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);
        dialogDescriptor.setButtonListener((ActionEvent ev) -> {
            if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                try {
                    dialogConvention.fromFields(outbound);
                    dialogDescriptor.setClosingOptions(null);
                } catch (IllegalStateException e) {
                    dialogConvention.getDialogState().changeToBlockingErrorState(e.getMessage());
                    dialogDescriptor.setClosingOptions(new Object[]{});
                }
            } else {
                dialogDescriptor.setClosingOptions(null);
            }
        });
        if (inbound != null) {
            dialogConvention.toFields(inbound);
        } else {
            dialogConvention.executeValidation();
        }
        this.updateDialogDescriptorValidity();
        this.updateDialogDescriptorDefaultMessage();
        this.dialogConvention.getDialogState().addPropertyChangeListener(new DialogConventionPropertyChangeListener());
        DialogDisplayer.getDefault().notify(dialogDescriptor);
        this.dialogConvention.getDialogState().removePropertyChangeListener(new DialogConventionPropertyChangeListener());
        return outbound;

    }

    private class EditAndProcessContext {

        boolean preventClose;
        Object result = null;
        Exception exception = null;
    }

    public <T> T editAndProcess(final Callable<T> callable) {
        final EditAndProcessContext context = new EditAndProcessContext();
        dialogDescriptor.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);
        dialogDescriptor.setButtonListener((ActionEvent ev) -> {
            if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                try {
                    dialogConvention.fromFields(outbound);
                    dialogDescriptor.setClosingOptions(null);
                } catch (IllegalStateException e) {
                    dialogConvention.getDialogState().changeToBlockingErrorState(e.getMessage());
                    dialogDescriptor.setClosingOptions(new Object[]{});
                    return;
                }
                context.exception = null;
                context.result = null;
                context.preventClose = false;
                ProgressUtils.showProgressDialogAndRun(() -> {
                    try {
                        context.result = callable.call();
                    } catch (PreventClose e) {
                        context.preventClose = true;
                    } catch (Exception e) {
                        context.exception = e;
                    }
                }, "Wait...");
                if (context.preventClose || !dialogConvention.getDialogState().isOkAllowed()) {
                    dialogDescriptor.setClosingOptions(new Object[]{});
                    return;
                }
                dialogDescriptor.setClosingOptions(null);
                return;
            }
            dialogDescriptor.setClosingOptions(null);
        });
        if (inbound != null) {
            dialogConvention.toFields(inbound);
        } else {
            dialogConvention.executeValidation();
        }
        this.updateDialogDescriptorValidity();
        this.updateDialogDescriptorDefaultMessage();
        this.dialogConvention.getDialogState().addPropertyChangeListener(new DialogConventionPropertyChangeListener());
        DialogDisplayer.getDefault().notify(dialogDescriptor);
        this.dialogConvention.getDialogState().removePropertyChangeListener(new DialogConventionPropertyChangeListener());
        this.exception = context.exception;
        return (T) context.result;
    }

    public static class PreventClose extends RuntimeException {

        public PreventClose() {
        }

        public PreventClose(String message) {
            super(message);
        }
    }
}
