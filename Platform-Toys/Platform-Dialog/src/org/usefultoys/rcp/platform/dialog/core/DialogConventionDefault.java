/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.core;

import java.awt.Component;
import javax.swing.SwingUtilities;
import org.usefultoys.rcp.platform.dialog.api.DialogConvention;
import org.usefultoys.rcp.platform.dialog.api.DialogState;

public abstract class DialogConventionDefault<Inbound, Outbound>
        implements DialogConvention<Inbound, Outbound> {

    private final DialogState dialogState;
    private int changeCounter = 0;

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
    public abstract Inbound createInbound();

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

    @Override
    public final void toFields(Inbound inbound) {
        startChange();
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
}
