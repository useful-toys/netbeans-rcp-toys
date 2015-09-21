/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.core;

import java.beans.PropertyChangeSupport;
import java.util.Objects;
import org.usefultoys.rcp.platform.dialog.api.DialogState;

/**
 *
 * @author Daniel Felix Ferber
 */
public class DialogStateImpl extends PropertyChangeSupport implements DialogState {

    private final String defaultMessage;

    private Message message;

    public DialogStateImpl(Object sourceBean, String defaultMessage) {
        super(sourceBean);
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    protected final void setMessage(Message newMessage) {
        if (Objects.equals(this.message, newMessage)) {
            return;
        }
        final Message oldMEssage = this.message;
        this.message = newMessage;
        firePropertyChange(PROP_MESSAGE, oldMEssage, newMessage);
    }

    @Override
    public final Message getMessage() {
        return message;
    }

    @Override
    public final boolean isOkAllowed() {
        return message != null && message.okAllowed;
    }

    @Override
    public final void changeToBlockingErrorState(String message) {
        setMessage(new ErrorMessage(false, message));
    }

    @Override
    public final void changeToErrorState(String message) {
        setMessage(new ErrorMessage(true, message));
    }

    @Override
    public final void changeToWarnState(String message) {
        setMessage(new WarnMessage(true, message));
    }

    @Override
    public final void changeToInfoState(String message) {
        setMessage(new InfoMessage(true, message));
    }

    @Override
    public final void changeToDefaultState() {
        setMessage(new Message(true, defaultMessage));
    }
}
