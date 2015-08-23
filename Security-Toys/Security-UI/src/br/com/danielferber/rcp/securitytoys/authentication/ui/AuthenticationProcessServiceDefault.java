package br.com.danielferber.rcp.securitytoys.authentication.ui;

/**
 *
 * @author Daniel
 */
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationException;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationProcessException;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationProcessService;
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
@ServiceProvider(service = AuthenticationProcessService.class)
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

        /* Definição do diálogo de login, seguindo a API do Netbeans RCP. */
        final String loginAnterior = NbPreferences.forModule(AuthenticationProcessServiceDefault.class).get("login", "");
        final CredentialPanel credenciaisPanel = new CredentialPanel(loginAnterior);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(credenciaisPanel, Bundle.AuthenticationProcessServiceDefault_LoginDialog_Title());
        dialogDescriptor.setClosingOptions(null);
        dialogDescriptor.setModal(true);
        dialogDescriptor.setLeaf(true);
        final NotificationLineSupport notificationLine = dialogDescriptor.createNotificationLineSupport();
        notificationLine.setInformationMessage("Informe seu login e sua senha.");

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
                final String login = credenciaisPanel.getLogin().trim();
                final char[] senha = credenciaisPanel.getSenha();
                if (login.length() == 0 || senha.length == 0) {
                    /* Informa sobre campos inválidos, mas não contabiliza a tentativa. */
                    notificationLine.setErrorMessage("O login e a senha são obrigatórios.");
                    continue;
                }

                /* Recorre ao serviço de autenticação. */
                ProgressUtils.showProgressDialogAndRun(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            SecurityService.Lookup.getDefault().login(login, senha);
                            NbPreferences.forModule(AuthenticationProcessServiceDefault.class).put("login", login);
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
