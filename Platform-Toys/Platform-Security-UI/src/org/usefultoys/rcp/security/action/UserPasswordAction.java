/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.usefultoys.rcp.platform.dialog.api.NetbeansDialogConvention;
import org.usefultoys.rcp.platform.dialog.notification.MessageUtil;
import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.PasswordException;
import org.usefultoys.rcp.security.api.SecurityService;
import org.usefultoys.rcp.security.ui.UserPasswordPanel;

@ActionID(
        category = UserPasswordAction.CATEGORY,
        id = UserPasswordAction.ID
)
@ActionRegistration(
        displayName = "#UserPasswordAction_Caption"
)
@ActionReference(path = "Menu/Help", position = 1310)
@Messages({"UserPasswordAction_Caption=Change password",
    "UserPasswordAction_Dialog_Title=Change password",
    "UserPasswordAction_Message_Working=Changing password...",
    "UserPasswordAction_Message_NoAuthenticatedUser=No authenticated user",
    "UserPasswordAction_Message_PasswordChangeNotAllowed=Password change not allowed",
    "UserPasswordAction_Message_IncorrectCredentials=Current password is wrong",
    "UserPasswordAction_Message_PasswordTooWeak=Password is too weak",
    "UserPasswordAction_Message_Success=Password successfully changed",
    "UserPasswordAction_Message_Canceled=Password not changed"
})
public final class UserPasswordAction implements ActionListener {

    public static final String CATEGORY = "Help";
    public static final String ID = "org.usefultoys.rcp.security.UserPasswordAction";

    @Override
    public void actionPerformed(ActionEvent event) {
        final AuthenticatedUser authenticatedUser = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (authenticatedUser == null) {
            MessageUtil.info(Bundle.UserPasswordAction_Caption(), Bundle.UserPasswordAction_Message_NoAuthenticatedUser());
            return;
        }

        if (!SecurityService.getDefault().getPasswordService().canChangePassword(authenticatedUser.getLogin())) {
            MessageUtil.info(Bundle.UserPasswordAction_Caption(), Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed());
            return;
        }

        final UserPasswordPanel.Descriptor descriptor = new UserPasswordPanel.Descriptor();
        final UserPasswordPanel panel = new UserPasswordPanel(descriptor, null);
        final NetbeansDialogConvention<UserPasswordPanel.Inbound, UserPasswordPanel.Outbound> nbc
                = NetbeansDialogConvention.create(panel, Bundle.UserPasswordAction_Dialog_Title());
        final UserPasswordPanel.Outbound result = nbc.editAndProcess(() -> {
            final UserPasswordPanel.Outbound outbound = nbc.getOutbound();
            nbc.getDialogState().changeToInfoState(Bundle.UserPasswordAction_Message_Working());
            try {
                SecurityService.getDefault().getPasswordService().changePassword(authenticatedUser.getLogin(), outbound.oldPassword, outbound.newPassword);
                MessageUtil.info(Bundle.UserPasswordAction_Caption(), Bundle.UserPasswordAction_Message_Success());
                return outbound;
            } catch (PasswordException.IncorrectCredentials e) {
                throw new IllegalStateException(Bundle.UserPasswordAction_Message_IncorrectCredentials());
            } catch (PasswordException.NotAllowed e) {
                throw new IllegalStateException(Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed());
            } catch (PasswordException.TooWeak e) {
                throw new IllegalStateException(Bundle.UserPasswordAction_Message_PasswordTooWeak());
            } catch (PasswordException e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
        if (result == null) {
            MessageUtil.info(Bundle.UserPasswordAction_Caption(), Bundle.UserPasswordAction_Message_Canceled());
        }
    }

    public static void runAction(Object source) {
        Action action = org.openide.awt.Actions.forID(CATEGORY, ID);
        action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null));
    }

}
