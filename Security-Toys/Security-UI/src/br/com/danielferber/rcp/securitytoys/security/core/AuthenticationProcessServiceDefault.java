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
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

@NbBundle.Messages({
    "AuthenticationProcessServiceDefault_LoginDialog_Title=Login"
})
public class AuthenticationProcessServiceDefault implements AuthenticationProcessService {

    public AuthenticationProcessServiceDefault() {
        super();
    }
    private static final int MAXIMAL_NUMER_TRIES = 3;

    @Override
    public AuthenticatedUser executeAuthenticationQuery() throws AuthenticationProcessException {
        if (!SecurityService.Lookup.getDefault().isServiceAvailable()) {
            throw new AuthenticationProcessException.Unavailable();
        }
        
        final CredentialPanel.Descriptor descriptor = new CredentialPanel.Descriptor();
        final CredentialPanel panel = new CredentialPanel(descriptor);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(panel, Bundle.AuthenticationProcessServiceDefault_LoginDialog_Title());
        dialogDescriptor.setClosingOptions(null);
        dialogDescriptor.setModal(true);
        dialogDescriptor.setLeaf(true);
        dialogDescriptor.setOptionType(DialogDescriptor.OK_CANCEL_OPTION);
        final NotificationLineSupport notificationLine = dialogDescriptor.createNotificationLineSupport();
        panel.setNotificationLine(notificationLine);
        final CredentialPanel.Inbound inbound = new CredentialPanel.Inbound();
        panel.toField(inbound);

        /* Realiza até MAXIMO_TENTATIVAS tentativas. */
        int contadorTentativas = 1;
        while (contadorTentativas <= MAXIMAL_NUMER_TRIES) {

            /* Mostra o diálogo modal e aguarda resposta. */
            final Object result = DialogDisplayer.getDefault().notify(dialogDescriptor);

            if (result == DialogDescriptor.CANCEL_OPTION || result == DialogDescriptor.CLOSED_OPTION) {
                /* O usuário cancelou o login ou fechou o diálogo de login. */
                throw new AuthenticationProcessException.Canceled();
            } else if (result == DialogDescriptor.OK_OPTION) {
                /* O usuário confirmou o login. Valida os campos. */
                CredentialPanel.Outbound outbound = new CredentialPanel.Outbound();
                panel.fromField(outbound);
                /* Recorre ao serviço de autenticação. */
                ProgressUtils.showProgressDialogAndRun(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            SecurityService.Lookup.getDefault().login(outbound.login, outbound.password);
                        } catch (AuthenticationException ex) {
                            // ignora
                        }
                    }
                }, "Validar credenciais...");

                Exception ex = SecurityService.Lookup.getDefault().getLastLoginException();
                if (ex == null) {
                    /* Se a execução chegou aqui, então o login foi aceito. */
                    return SecurityService.Lookup.getDefault().getCurrentAuthenticatedUser();
                } else if (ex instanceof AuthenticationException.IncorrectCredentials) {
                    contadorTentativas++;
                    notificationLine.setErrorMessage("Estas credenciais estão incorretas.");
                } else if (ex instanceof AuthenticationException.InexistingUser) {
                    contadorTentativas++;
                    notificationLine.setErrorMessage("Estas credenciais estão incorretas.");
                } else if (ex instanceof AuthenticationException.InactiveUser) {
                    contadorTentativas++;
                    notificationLine.setErrorMessage("Estas credenciais não esão ativas.");
                } else if (ex instanceof AuthenticationException.UnavailableService) {
                    throw new AuthenticationProcessException.Unavailable();
                } else {
                    throw new AuthenticationProcessException.Unavailable();
                }
            }
        }
        throw new AuthenticationProcessException.Exceeded();
    }

    @Override
    public void executeLogoff() {
        ProgressUtils.showProgressDialogAndRun(new Runnable() {

            @Override
            public void run() {
                try {
                    if (SecurityService.Lookup.getDefault().getCurrentAuthenticatedUser() != null) {
                        SecurityService.Lookup.getDefault().logoff();
                    }
                } catch (Exception ex) {
                    // ignora
                }
            }
        }, "Logoff...");
    }
}
