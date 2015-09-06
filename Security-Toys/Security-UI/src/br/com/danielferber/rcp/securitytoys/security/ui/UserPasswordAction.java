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
    "UserPasswordAction_UserInfoDialogTitle=Change password",
    "UserPasswordAction_Message_NoAuthenticatedUser=No authenticated user",
    "UserPasswordAction_Message_IncorrectCredentials=Current password is wrong",
    "UserPasswordAction_Message_PasswordTooWeak=Password is too weak",
    "UserPasswordAction_Message_PasswordChangeNotAllowed=Password change not allowed",
    "UserPasswordAction_Message_PasswordChangeSuccessful=Password successfully changed",
    "UserPasswordAction_Message_WaitingServer=Please wait..."})
public final class UserPasswordAction implements ActionListener {

    public static final String CATEGORY = "Help";
    public static final String ID = "br.com.danielferber.rcp.securitytoys.ui.UserPasswordAction";

    @Override
    public void actionPerformed(ActionEvent e) {
        runDialog();
    }

    protected void runDialog() {
        final AuthenticatedUser authenticatedUser = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (authenticatedUser == null) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_NoAuthenticatedUser(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        if (!SecurityService.getDefault().getPasswordService().canChangePassword(authenticatedUser.getLogin())) {
            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        
        final UserPasswordPanel.Descriptor descriptor = new UserPasswordPanel.Descriptor();
        final UserPasswordPanel panel = new UserPasswordPanel(descriptor, null);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(panel, Bundle.UserPropertiesPanel_ChangeUserPasswordDialogTitle());
        dialogDescriptor.setClosingOptions(null);
        dialogDescriptor.setModal(true);
        dialogDescriptor.setLeaf(true);
        dialogDescriptor.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        final NotificationLineSupport notificationLine = dialogDescriptor.createNotificationLineSupport();
        panel.setNotificationLine(notificationLine);
        final UserPasswordPanel.Inbound inbound = new UserPasswordPanel.Inbound();
        panel.toField(inbound);

        final UserPasswordPanel.Outbound outbound = new UserPasswordPanel.Outbound();
        dialogDescriptor.setButtonListener((ActionEvent ev) -> {
            if (ev.getSource() == DialogDescriptor.OK_OPTION) {
                try {
                    panel.fromField(outbound);
                    notificationLine.setInformationMessage(Bundle.UserPasswordAction_Message_WaitingServer());
                    ProgressUtils.showProgressDialogAndRun(() -> {
                        try {
                            SecurityService.getDefault().getPasswordService().changePassword(authenticatedUser.getLogin(), outbound.oldPassword, outbound.newPassword);
                            final NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.UserPasswordAction_Message_PasswordChangeSuccessful(), NotifyDescriptor.INFORMATION_MESSAGE);
                            DialogDisplayer.getDefault().notify(nd);
                        } catch (PasswordException.IncorrectCredentials e) {
                            throw new IllegalStateException(Bundle.UserPasswordAction_Message_IncorrectCredentials());
                        } catch (PasswordException.NotAllowed e) {
                            throw new IllegalStateException(Bundle.UserPasswordAction_Message_PasswordChangeNotAllowed());
                        } catch (PasswordException.TooWeak e) {
                            throw new IllegalStateException(Bundle.UserPasswordAction_Message_PasswordTooWeak());
                        } catch (PasswordException e) {
                            throw new IllegalStateException(e.getMessage());
                        }
                    }, "Changing password...");
                } catch (IllegalStateException e) {
                    notificationLine.setErrorMessage(e.getMessage());
                    dialogDescriptor.setClosingOptions(new Object[]{});
                    return;
                }
                dialogDescriptor.setClosingOptions(null);
            } else {
                dialogDescriptor.setClosingOptions(null);
            }
        });

        DialogDisplayer.getDefault().notify(dialogDescriptor);
    }

    public static void runAction(Object source) {
        Action action = org.openide.awt.Actions.forID(CATEGORY, ID);
        action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null));
    }

}
