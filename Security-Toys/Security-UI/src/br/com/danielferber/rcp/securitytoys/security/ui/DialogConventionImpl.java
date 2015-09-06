/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.ui;

import java.awt.Component;
import javax.swing.SwingUtilities;

public abstract class DialogConventionImpl<Inbound, Outbound>
        implements DialogConvention<Inbound, Outbound> {

    private final DialogState dialogState;
    private int changeCounter = 0;

    public DialogConventionImpl(Component sourceBean, String defaultMessage) {
        dialogState = new DialogStateImpl(sourceBean, defaultMessage);
    }

    @Override
    public DialogState getDialogState() {
        return dialogState;
    }

    @Override
    public void scheduleValidation() {
        if (changeCounter != 0) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            executeValidation();
        });

    }

    public void startChange() {
        changeCounter++;
    }

    public void stopChange() {
        changeCounter--;
        if (changeCounter == 0) {
            executeValidation();
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

    @Override
    public final void executeValidation() {
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
            getDialogState().changeToErrorState(e.getMessage());
        }
    }
}
