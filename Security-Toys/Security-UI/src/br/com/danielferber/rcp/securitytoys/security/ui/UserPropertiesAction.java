/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.ui;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = UserPropertiesAction.CATEGORY,
        id = UserPropertiesAction.ID
)
@ActionRegistration(
        displayName = "#UserPropertiesAction_Caption"
)
@ActionReference(path = "Menu/Help", position = 1300)
@Messages({"UserPropertiesAction_Caption=User properties",
    "UserPropertiesAction_UserInfoDialogTitle=User information",
    "UserPropertiesAction_Message_NoAuthenticatedUser=No authenticated user"})
public final class UserPropertiesAction implements ActionListener {

    public static final String CATEGORY = "Help";
    public static final String ID = "br.com.danielferber.rcp.securitytoys.ui.UserPropertiesAction";
    
    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runShowUserPropertiesImpl();
            }

        });
    }

    private void runShowUserPropertiesImpl() {
        final AuthenticatedUser authenticatedUser = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (authenticatedUser == null) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPropertiesAction_Message_NoAuthenticatedUser(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        final UserPropertiesPanel.Descriptor descriptor = new UserPropertiesPanel.Descriptor();
        descriptor.editableProperties = false;
        descriptor.editablePassword = SecurityService.getDefault().getPasswordService().canChangePassword(authenticatedUser.getLogin());
        final UserPropertiesPanel panel = new UserPropertiesPanel(descriptor, null);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(panel, Bundle.UserPropertiesAction_UserInfoDialogTitle());
        dialogDescriptor.setClosingOptions(null);
        dialogDescriptor.setModal(true);
        dialogDescriptor.setLeaf(true);
        dialogDescriptor.setOptionType(NotifyDescriptor.DEFAULT_OPTION);
        panel.setNotificationLine(dialogDescriptor.createNotificationLineSupport());
        panel.toField(new UserPropertiesPanel.Inbound(authenticatedUser));
        panel.executeValidation();
        DialogDisplayer.getDefault().notify(dialogDescriptor);
    }
    
    public static void runAction(Object source) {
        Action action=org.openide.awt.Actions.forID(CATEGORY, ID);
        action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null));
    }

}
