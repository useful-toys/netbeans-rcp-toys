package br.com.danielferber.rcp.securitytoys.security.core;

/**
 *
 * @author Daniel
 */
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationException;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationProcessException;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationProcessService;
import br.com.danielferber.rcp.securitytoys.security.ui.CredentialPanel;
import br.com.danielferber.rcp.securitytoys.security.ui.NetbeansDialogConvention;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "AuthenticationProcessServiceDefault_LoginDialog_Title=Login",
    "AuthenticationProcessServiceDefault_Message_Working=Checking credentials...",
    "AuthenticationProcessServiceDefault_Message_IncorrectCredentials=Credentials are not corretct",
    "AuthenticationProcessServiceDefault_Message_InexistingUser=User does not exist",
    "AuthenticationProcessServiceDefault_Message_InactiveUser=User is inactive",
    "AuthenticationProcessServiceDefault_Message_UnavailableService=Authentication is unavailable",
    "AuthenticationProcessServiceDefault_Message_Exceeded=Exceeded allowed number of tries",
    "AuthenticationProcessServiceDefault_Message_Canceled=Login canceled.",
})
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
            if (outbound.tries > MAXIMAL_NUMER_TRIES) {
                return null;
            }
            nbc.getDialogState().changeToInfoState(Bundle.AuthenticationProcessServiceDefault_Message_Working());
            try {
                return SecurityService.getDefault().login(outbound.login, outbound.password);
            } catch (AuthenticationException.IncorrectCredentials e) {
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_IncorrectCredentials());
            } catch (AuthenticationException.InexistingUser e) {
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_InexistingUser());
            } catch (AuthenticationException.InactiveUser e) {
                nbc.getDialogState().changeToErrorState(Bundle.AuthenticationProcessServiceDefault_Message_InactiveUser());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, Bundle.AuthenticationProcessServiceDefault_Message_UnavailableService(), e);
                throw new AuthenticationProcessException.Unavailable();
            }
            return null;
        });
        if (nbc.getOutbound().tries > MAXIMAL_NUMER_TRIES) {
            NotifyDescriptor d = new DialogDescriptor.Message(Bundle.AuthenticationProcessServiceDefault_Message_Exceeded(), DialogDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            throw new AuthenticationProcessException.Exceeded();
        }
        if (authenticatedUser == null) {
            NotifyDescriptor d = new DialogDescriptor.Message(Bundle.AuthenticationProcessServiceDefault_Message_Canceled(), DialogDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
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
