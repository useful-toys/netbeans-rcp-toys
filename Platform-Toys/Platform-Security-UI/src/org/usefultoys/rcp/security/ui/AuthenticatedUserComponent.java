/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.ui;

import org.usefultoys.rcp.security.action.UserPropertiesAction;
import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.SecurityService;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Daniel Felix Ferber
 */
@NbBundle.Messages({
    "AuthenticatedUserComponent_Label_Anonimous=Anonimous"
})
public class AuthenticatedUserComponent extends JLabel {

    public AuthenticatedUserComponent() {
        super(Bundle.AuthenticatedUserComponent_Label_Anonimous());
        this.setText(retrieveUserName());
        this.addMouseListener(createMouseListener());

    }

    protected MouseAdapter createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    UserPropertiesAction.runAction(this);
                }
            }
        };
    }

    public void update() {
        final String userName = retrieveUserName();
        SwingUtilities.invokeLater(() -> {
            AuthenticatedUserComponent.this.setText(userName);
        });
    }

    protected String retrieveUserName() {
        final AuthenticatedUser user = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (user == null) {
            return Bundle.AuthenticatedUserComponent_Label_Anonimous();
        } else {
            return user.getName();
        }
    }
}
