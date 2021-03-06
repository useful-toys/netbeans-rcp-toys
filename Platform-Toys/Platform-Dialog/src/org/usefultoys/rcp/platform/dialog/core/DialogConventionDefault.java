/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.core;

import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.usefultoys.rcp.platform.dialog.api.DialogConvention;
import org.usefultoys.rcp.platform.dialog.api.DialogState;

public abstract class DialogConventionDefault<Inbound, Outbound>
        implements DialogConvention<Inbound, Outbound> {

    private final DialogState dialogState;
    private int changeCounter = 0;
    private boolean editable = false;

    public DialogConventionDefault(Component sourceBean, String defaultMessage) {
        dialogState = new DialogStateImpl(sourceBean, defaultMessage);
    }

    @Override
    public DialogState getDialogState() {
        return dialogState;
    }

    @Override
    public void scheduleUpdate() {
        if (changeCounter != 0) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            updateAll();
        });

    }

    @Override
    public final void setEditable(boolean editable) {
        this.editable = editable;
    }

    protected final boolean isEditable() {
        return editable;
    }

    public void startChange() {
        changeCounter++;
    }

    public void stopChange() {
        changeCounter--;
        if (changeCounter == 0) {
            updateAll();
        }
    }

    @Override
    public abstract Outbound createOutbound();

    @Override
    public Inbound createInbound() {
        return null;
    }

    protected void convertInboundToFields(Inbound inbound) {
        /* Nothing by default. */
    }

    protected String convertFieldToOutbound(Outbound outbound) throws IllegalStateException {
        /* Nothing by default. */
        return null;
    }

    protected void loadInboundDefaults(Inbound inbound) {
        /* Nothing by default. */
    }

    protected void saveOutboundDefaults(Outbound outbound) {
        /* Nothing by default. */
    }

    protected void loadFieldDefaults() {
        /* Nothing by default. */
    }

    protected void saveFieldDefaults() {
        /* Nothing by default. */
    }

    @Override
    public final void toFields() {
        startChange();
        loadFieldDefaults();
        stopChange();
    }

    @Override
    public final void toFields(Inbound inbound) {
        startChange();
        loadFieldDefaults();
        loadInboundDefaults(inbound);
        convertInboundToFields(inbound);
        stopChange();
    }

    @Override
    public final void fromFields(Outbound outbound) throws IllegalStateException {
        executePreValidation();
        convertFieldToOutbound(outbound);
        executePosValidation(outbound);
        saveOutboundDefaults(outbound);
        saveFieldDefaults();
    }

    protected String executePreValidation() throws IllegalStateException {
        /* Nothing by default. */
        return null;
    }

    protected String executePosValidation(Outbound outbound) throws IllegalStateException {
        /* Nothing by default. */
        return null;
    }

    protected final void updateValidation() {
        if (changeCounter != 0) {
            return;
        }
        try {
            final Outbound outbound = createOutbound();
            final String messages[] = new String[]{
                executePreValidation(),
                convertFieldToOutbound(outbound),
                executePosValidation(outbound)
            };
            boolean isWarning = false;
            for (String message : messages) {
                if (message != null) {
                    getDialogState().changeToWarnState(message);
                    isWarning = true;
                    break;
                }
            }
            if (!isWarning) {
                getDialogState().changeToDefaultState();
            }
        } catch (IllegalStateException e) {
            getDialogState().changeToBlockingErrorState(e.getMessage());
        }
    }

    protected void updateEditable() {
        /* Nothing by default. */
    }

    protected void updateVisible() {
        /* Nothing by default. */
    }

    protected void updateEnabled() {
        /* Nothing by default. */
    }

    protected void updateValues() {
        /* Nothing by default. */
    }

    @Override
    public void updateAll() {
        updateValidation();
        updateEditable();
        updateVisible();
        updateEnabled();
        updateValues();
    }
    
    private final DocumentListener fieldDocumentListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            scheduleUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            scheduleUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            scheduleUpdate();
        }
    };

    public final DocumentListener getDefaultDocumentListener() {
        return fieldDocumentListener;
    }
}
