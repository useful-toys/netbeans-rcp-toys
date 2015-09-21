/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.ui;

import org.usefultoys.rcp.platform.dialog.api.NetbeansDialogConvention;
import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.SecurityService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.usefultoys.rcp.platform.dialog.notification.MessageUtil;

@ActionID(
        category = UserPropertiesAction.CATEGORY,
        id = UserPropertiesAction.ID
)
@ActionRegistration(
        displayName = "#UserPropertiesAction_Caption"
)
@ActionReference(path = "Menu/Help", position = 1300)
@Messages({"UserPropertiesAction_Caption=User information",
    "UserPropertiesAction_Dialog_Title=User information",
    "UserPropertiesAction_Message_NoAuthenticatedUser=No authenticated user"})
public final class UserPropertiesAction implements ActionListener {

    public static final String CATEGORY = "Help";
    public static final String ID = "org.usefultoys.rcp.security.UserPropertiesAction";

    @Override
    public void actionPerformed(ActionEvent e) {
        final AuthenticatedUser authenticatedUser = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (authenticatedUser == null) {
            MessageUtil.info(Bundle.UserPropertiesAction_Caption(), Bundle.UserPasswordAction_Message_NoAuthenticatedUser());
            return;
        }

        final UserPropertiesPanel.Descriptor descriptor = new UserPropertiesPanel.Descriptor();
        // API does not yet support user information editing.
        descriptor.editableProperties = false;
        descriptor.editablePassword = SecurityService.getDefault().getPasswordService().canChangePassword(authenticatedUser.getLogin());
        final UserPropertiesPanel panel = new UserPropertiesPanel(descriptor);
        final NetbeansDialogConvention<UserPropertiesPanel.Inbound, UserPropertiesPanel.Outbound> nbc
                = NetbeansDialogConvention.create(panel, Bundle.UserPropertiesAction_Dialog_Title());
        nbc.getInbound().fromObject(authenticatedUser);
        nbc.show();
    }

    public static void runAction(Object source) {
        Action action = org.openide.awt.Actions.forID(CATEGORY, ID);
        action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null));
    }
}
