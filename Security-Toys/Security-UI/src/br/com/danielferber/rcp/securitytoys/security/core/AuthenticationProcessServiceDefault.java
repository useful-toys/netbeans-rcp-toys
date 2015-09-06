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
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "AuthenticationProcessServiceDefault_LoginDialog_Title=Login"
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

        final CredentialPanel.Inbound inbound = new CredentialPanel.Inbound();
        final CredentialPanel.Descriptor descriptor = new CredentialPanel.Descriptor();
        final CredentialPanel panel = new CredentialPanel(descriptor);
        final NetbeansDialogConvention<CredentialPanel.Inbound, CredentialPanel.Outbound> nbc
                = NetbeansDialogConvention.create(panel, Bundle.AuthenticationProcessServiceDefault_LoginDialog_Title());
        final AuthenticatedUser authenticatedUser = nbc.editAndProcess(() -> {
            final CredentialPanel.Outbound outbound = nbc.getOutbound();
            outbound.tries++;
            if (outbound.tries > MAXIMAL_NUMER_TRIES) {
                throw new AuthenticationProcessException.Exceeded();
            }
            nbc.getDialogState().changeToInfoState("Verificando credenciais...");
            try {
                return SecurityService.getDefault().login(outbound.login, outbound.password);
            } catch (AuthenticationException.IncorrectCredentials e) {
                nbc.getDialogState().changeToErrorState("As  credenciais estão incorretas.");
            } catch (AuthenticationException.InexistingUser e) {
                nbc.getDialogState().changeToErrorState("Estas credenciais estão incorretas.");
            } catch (AuthenticationException.InactiveUser e) {
                nbc.getDialogState().changeToErrorState("Estas credenciais não esão ativas.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failure while calling service.", e);
                throw new AuthenticationProcessException.Unavailable();
            }
            return null;
        });
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
