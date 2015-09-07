/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.ui;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationException;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationProcessException;
import br.com.danielferber.rcp.securitytoys.security.api.PasswordException;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import static br.com.danielferber.rcp.securitytoys.security.core.AuthenticationProcessServiceDefault.LOGGER;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

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
    public static final String ID = "br.com.danielferber.rcp.securitytoys.ui.UserPasswordAction";

    @Override
    public void actionPerformed(ActionEvent event) {
        final AuthenticatedUser authenticatedUser = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (authenticatedUser == null) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_NoAuthenticatedUser(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        if (!SecurityService.getDefault().getPasswordService().canChangePassword(authenticatedUser.getLogin())) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
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
                final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_Success(), NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
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
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_Canceled(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    public static void runAction(Object source) {
        Action action = org.openide.awt.Actions.forID(CATEGORY, ID);
        action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null));
    }

}
