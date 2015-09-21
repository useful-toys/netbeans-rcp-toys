/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.api;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface DialogState {

    interface Support<Inbound, Outbound> {

        DialogConvention<Inbound, Outbound> getDialogState();
    }

    String getDefaultMessage();

    class Message {

        public final boolean okAllowed;
        public final String text;

        public Message(boolean okAllowed, String text) {
            this.okAllowed = okAllowed;
            this.text = text;
        }
    }

    class ErrorMessage extends Message {

        public ErrorMessage(boolean okAllowed, String text) {
            super(okAllowed, text);
        }
    }

    class WarnMessage extends Message {

        public WarnMessage(boolean okAllowed, String text) {
            super(okAllowed, text);
        }
    }

    class InfoMessage extends Message {

        public InfoMessage(boolean okAllowed, String text) {
            super(okAllowed, text);
        }
    }

    String PROP_MESSAGE = "message";

    Message getMessage();

    boolean isOkAllowed();

    void changeToDefaultState();

    void changeToBlockingErrorState(String message);

    void changeToErrorState(String message);

    void changeToInfoState(String message);

    void changeToWarnState(String message);

    PropertyChangeListener[] getPropertyChangeListeners();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

}
