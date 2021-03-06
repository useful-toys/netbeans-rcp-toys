package org.usefultoys.rcp.security.ui;

import org.usefultoys.rcp.platform.dialog.api.DialogConvention;
import java.awt.Component;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.usefultoys.rcp.platform.dialog.core.DialogConventionDefault;

/**
 *
 */
@NbBundle.Messages({
    "CredentialPanel_Message_Default=Enter login and password.",
    "CredentialPanel_Message_LoginRequired=Login must not be empty.",
    "CredentialPanel_Message_PasswordRequired=Password must not be empty."
})
public class CredentialPanel extends javax.swing.JPanel implements DialogConvention.Support<CredentialPanel.Inbound, CredentialPanel.Outbound> {

    public static final String PREF_PREVISOUS_LOGIN = "login";
    private final Descriptor descriptor;
    private final DialogConventionImpl dialogConvention;

    /**
     * Describes how to build the panel.
     */
    public static class Descriptor {

        public String defaultMessage = Bundle.UserPasswordPanel_Message_Default();
        public boolean suggestPrevisouLogin = true;
    }

    /**
     * Contains values to populate fields shown on the panel.
     */
    public static class Inbound {

        public String login;
    }

    /**
     * Contains values from fields shown on the panel.
     */
    public static class Outbound {

        public int tries = 0;
        public String login;
        public char[] password;
    }

    private class FieldDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            dialogConvention.scheduleUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            dialogConvention.scheduleUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            dialogConvention.scheduleUpdate();
        }
    }

    /**
     * Creates new form CredenciaisPanel
     */
    public CredentialPanel(Descriptor descriptor) {
        this.descriptor = descriptor;
        initComponents();
        final FieldDocumentListener fieldDocumentListener = new FieldDocumentListener();
        this.passwordField.getDocument().addDocumentListener(fieldDocumentListener);
        this.loginField.getDocument().addDocumentListener(fieldDocumentListener);
        this.dialogConvention = new DialogConventionImpl(this, descriptor.defaultMessage);
    }

    private class DialogConventionImpl extends DialogConventionDefault<Inbound, Outbound> {

        public DialogConventionImpl(Component source, String defaultMessage) {
            super(source, defaultMessage);
        }

        @Override
        public Inbound createInbound() {
            return new Inbound();
        }

        @Override
        public Outbound createOutbound() {
            return new Outbound();
        }

        @Override
        protected void loadInboundDefaults(Inbound inbound) {
            if (inbound.login == null) {
                inbound.login = NbPreferences.forModule(CredentialPanel.class).get(PREF_PREVISOUS_LOGIN, "");
            }
        }

        @Override
        protected void saveOutboundDefaults(Outbound outbound) {
            if (outbound.login != null && ! outbound.login.isEmpty()) {
                NbPreferences.forModule(CredentialPanel.class).put(PREF_PREVISOUS_LOGIN, outbound.login);
            }
        }
        

        @Override
        protected void convertInboundToFields(Inbound inbound) {
            loginField.setText(inbound.login);
            passwordField.setText("");
        }

        @Override
        protected String convertFieldToOutbound(Outbound outbound) throws IllegalStateException {
            outbound.login = loginField.getText().trim();
            outbound.password = passwordField.getPassword();
            return null;
        }

        @Override
        protected String executePosValidation(Outbound outbound) throws IllegalStateException {
            String message = null;
            if (outbound.login.length() == 0) {
                throw new IllegalStateException(Bundle.CredentialPanel_Message_LoginRequired());
            }
            if (outbound.password.length == 0) {
                throw new IllegalStateException(Bundle.CredentialPanel_Message_PasswordRequired());
            }
            return message;
        }

          
    }

    @Override
    public DialogConvention getDialogConvention() {
        return dialogConvention;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginLabel = new javax.swing.JLabel();
        loginField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();

        org.openide.awt.Mnemonics.setLocalizedText(loginLabel, org.openide.util.NbBundle.getMessage(CredentialPanel.class, "CredentialPanel.loginLabel.text")); // NOI18N

        loginField.setText(org.openide.util.NbBundle.getMessage(CredentialPanel.class, "CredentialPanel.loginField.text")); // NOI18N
        loginField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                loginFieldPropertyChange(evt);
            }
        });
        loginField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                loginFieldKeyTyped(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(CredentialPanel.class, "CredentialPanel.passwordLabel.text")); // NOI18N

        passwordField.setText(org.openide.util.NbBundle.getMessage(CredentialPanel.class, "CredentialPanel.passwordField.text")); // NOI18N
        passwordField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fieldPropertyChange(evt);
            }
        });
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                passwordFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loginLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(loginField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(passwordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginLabel)
                    .addComponent(loginField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loginFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_loginFieldPropertyChange
    }//GEN-LAST:event_loginFieldPropertyChange

    private void fieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fieldPropertyChange
    }//GEN-LAST:event_fieldPropertyChange

    private void loginFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_loginFieldKeyTyped
    }//GEN-LAST:event_loginFieldKeyTyped

    private void passwordFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldKeyTyped
    }//GEN-LAST:event_passwordFieldKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField loginField;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    // End of variables declaration//GEN-END:variables
}
