package org.usefultoys.rcp.security.core;

/**
 *
 * @author Daniel
 */
import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.AuthenticationException;
import org.usefultoys.rcp.security.api.SecurityService;
import org.usefultoys.rcp.security.api.AuthenticationProcessException;
import org.usefultoys.rcp.security.api.AuthenticationProcessService;
import org.usefultoys.rcp.security.ui.CredentialPanel;
import org.usefultoys.rcp.platform.dialog.api.NetbeansDialogConvention;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "AuthenticationProcessServiceDefault_LoginDialog_Title=Login",
    "AuthenticationProcessServiceDefault_Message_Working=Checking credentials...",
    "AuthenticationProcessServiceDefault_Message_IncorrectCredentials=Credentials are not corretct",
    "AuthenticationProcessServiceDefault_Message_InexistingUser=User does not exist",
    "AuthenticationProcessServiceDefault_Message_InactiveUser=User is inactive",
    "AuthenticationProcessServiceDefault_Message_UnavailableService=Authentication is unavailable",
    "AuthenticationProcessServiceDefault_Message_Exceeded=Exceeded allowed number of tries",
    "AuthenticationProcessServiceDefault_Message_Canceled=Login canceled.",})
public class AuthenticationProcessServiceDefault implements AuthenticationProcessService {

    public static final Logger LOGGER = Logger.getLogger(AuthenticationProcessServiceDefault.class.getName());

    public AuthenticationProcessServiceDefault() {
        super();
    }
    private static final int MAXIMAL_NUMER_TRIES = 3;

    @Override
    public AuthenticatedUser executeAuthenticationQuery() throws AuthenticationProcessException {
        if (!SecurityService.getDefault().isServiceAvailable()) {
            throw new AuthenticationProcessException.Unavailable();
        }

        final CredentialPanel.Descriptor descriptor = new CredentialPanel.Descriptor();
        final CredentialPanel panel = new CredentialPanel(descriptor);
        final NetbeansDialogConvention<CredentialPanel.Inbound, CredentialPanel.Outbound> nbc
                = NetbeansDialogConvention.create(panel, Bundle.AuthenticationProcessServiceDefault_LoginDialog_Title());
        final AuthenticatedUser authenticatedUser = nbc.editAndProcess(() -> {
            final CredentialPanel.Outbound outbound = nbc.getOutbound();
            outbound.tries++;
            nbc.getDialogState().changeToInfoState(Bundle.AuthenticationProcessServiceDefault_Message_Working());
            try {
                return SecurityService.getDefault().login(outbound.login, outbound.password);
            } catch (AuthenticationException.IncorrectCredentials e) {
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_IncorrectCredentials());
                if (outbound.tries > MAXIMAL_NUMER_TRIES) {
                    return null;
                } 
                throw new NetbeansDialogConvention.PreventClose();
            } catch (AuthenticationException.InexistingUser e) {
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_InexistingUser());
                if (outbound.tries > MAXIMAL_NUMER_TRIES) {
                    return null;
                }
                throw new NetbeansDialogConvention.PreventClose();
            } catch (AuthenticationException.InactiveUser e) {
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_InactiveUser());
                if (outbound.tries > MAXIMAL_NUMER_TRIES) {
                    return null;
                }
                throw new NetbeansDialogConvention.PreventClose();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, Bundle.AuthenticationProcessServiceDefault_Message_UnavailableService(), e);
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_UnavailableService());
                throw new NetbeansDialogConvention.PreventClose();
            }
        });
        if (nbc.getOutbound().tries > MAXIMAL_NUMER_TRIES) {
            throw new AuthenticationProcessException.Exceeded();
        }
        if (authenticatedUser == null) {
            throw new AuthenticationProcessException.Canceled();
        }
        return authenticatedUser;
    }

    @Override
    public void executeLogoff() {
        ProgressUtils.showProgressDialogAndRun(() -> {
            try {
                if (SecurityService.getDefault().getCurrentAuthenticatedUser() != null) {
                    SecurityService.getDefault().logoff();
                }
            } catch (Exception ex) {
                // ignora
            }
        }, "Logoff...");
    }
}
