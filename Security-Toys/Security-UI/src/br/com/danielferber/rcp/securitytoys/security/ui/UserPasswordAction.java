/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.ui;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.PasswordException;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.SwingUtilities;
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
    "UserPasswordAction_UserInfoDialogTitle=Change password",
    "UserPasswordAction_Message_NoAuthenticatedUser=No authenticated user",
    "UserPasswordAction_Message_IncorrectCredentials=Current password is wrong",
    "UserPasswordAction_Message_PasswordTooWeak=Password is too weak",
    "UserPasswordAction_Message_PasswordChangeNotAllowed=Password change not allowed",
    "UserPasswordAction_Message_PasswordChangeSuccessful=Password successfully changed"})
public final class UserPasswordAction implements ActionListener {

    public static final String CATEGORY = "Help";
    public static final String ID = "br.com.danielferber.rcp.securitytoys.ui.UserPasswordAction";

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runChangePasswordImpl();
            }
        });
    }

    private void runChangePasswordImpl() {
        final AuthenticatedUser authenticatedUser = SecurityService.Lookup.getDefault().getCurrentAuthenticatedUser();
        if (authenticatedUser == null) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_NoAuthenticatedUser(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        if (!SecurityService.Lookup.getDefault().getPasswordService().canChangePassword(authenticatedUser.getLogin())) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        final UserPasswordPanel.Descriptor descriptor = new UserPasswordPanel.Descriptor();
        final UserPasswordPanel panel = new UserPasswordPanel(descriptor, null);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(panel, Bundle.UserPropertiesPanel_ChangeUserPasswordDialogTitle());
        dialogDescriptor.setClosingOptions(null);
        dialogDescriptor.setModal(true);
        dialogDescriptor.setLeaf(true);
        dialogDescriptor.setOptionType(NotifyDescriptor.DEFAULT_OPTION);
        final NotificationLineSupport notificationLine = dialogDescriptor.createNotificationLineSupport();
        panel.setNotificationLine(notificationLine);
        panel.toField();
        dialogDescriptor.setButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                    try {
                        final UserPasswordPanel.Outbound outbound = new UserPasswordPanel.Outbound();
                        panel.fromField(outbound);
                        dialogDescriptor.setClosingOptions(null);
                        SecurityService.Lookup.getDefault().getPasswordService().changePassword(authenticatedUser.getLogin(), outbound.oldPassword, outbound.newPassword);
                        final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_PasswordChangeSuccessful(), NotifyDescriptor.INFORMATION_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                        dialogDescriptor.setClosingOptions(null);
                    } catch (PasswordException.IncorrectCredentials e) {
                        notificationLine.setErrorMessage(Bundle.UserPasswordAction_Message_IncorrectCredentials());
                        dialogDescriptor.setClosingOptions(new Object[]{});
                    } catch (PasswordException.NotAllowed e) {
                        notificationLine.setErrorMessage(Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed());
                        dialogDescriptor.setClosingOptions(new Object[]{});
                    } catch (PasswordException.TooWeak e) {
                        notificationLine.setErrorMessage(Bundle.UserPasswordAction_Message_PasswordTooWeak());
                        dialogDescriptor.setClosingOptions(new Object[]{});
                    } catch (PasswordException | IllegalStateException e) {
                        notificationLine.setErrorMessage(e.getMessage());
                        dialogDescriptor.setClosingOptions(new Object[]{});
                    }
                }
            }
        });

        DialogDisplayer.getDefault().notify(dialogDescriptor);
    }

    public static void runAction(Object source) {
        Action action = org.openide.awt.Actions.forID(CATEGORY, ID);
        action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null));
    }

}
