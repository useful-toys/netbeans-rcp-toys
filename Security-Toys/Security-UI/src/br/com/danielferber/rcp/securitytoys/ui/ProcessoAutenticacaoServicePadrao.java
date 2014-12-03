/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.ui;

/**
 *
 * @author Daniel
 */
import br.com.danielferber.rcp.securitytoys.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.api.AuthenticationException;
import br.com.danielferber.rcp.securitytoys.api.SecurityService;
import br.com.danielferber.rcp.securitytoys.auth.api.ProcessoAutenticacaoException;
import br.com.danielferber.rcp.securitytoys.auth.api.ProcessoAutenticacaoService;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ProcessoAutenticacaoService.class)
public class ProcessoAutenticacaoServicePadrao extends ProcessoAutenticacaoService {

    public ProcessoAutenticacaoServicePadrao() {
        super();
    }
    private static final int MAXIMO_TENTATIVAS = 3;

    @Override
    public AuthenticatedUser executarAutenticacao() throws ProcessoAutenticacaoException {

        if (!SecurityService.Lookup.getDefault().isServiceAvailable()) {
            throw new ProcessoAutenticacaoException.ServicoIndisponivel();
        }

        /* Definição do diálogo de login, seguindo a API do Netbeans RCP. */
        final String loginAnterior = NbPreferences.forModule(ProcessoAutenticacaoServicePadrao.class).get("login", "");
        final CredenciaisPanel credenciaisPanel = new CredenciaisPanel(loginAnterior);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(credenciaisPanel, "Login");
        dialogDescriptor.setClosingOptions(null);
        dialogDescriptor.setModal(true);
        dialogDescriptor.setLeaf(true);
        final NotificationLineSupport notificationLine = dialogDescriptor.createNotificationLineSupport();
        notificationLine.setInformationMessage("Informe seu login e sua senha.");

        /* Realiza até MAXIMO_TENTATIVAS tentativas. */
        int contadorTentativas = 1;
        while (contadorTentativas <= MAXIMO_TENTATIVAS) {

            /* Mostra o diálogo modal e aguarda resposta. */
            final Object result = DialogDisplayer.getDefault().notify(dialogDescriptor);

            if (result == DialogDescriptor.CANCEL_OPTION || result == DialogDescriptor.CLOSED_OPTION) {
                /* O usuário cancelou o login ou fechou o diálogo de login. */
                throw new ProcessoAutenticacaoException.ProcessoCancelado();
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
                            NbPreferences.forModule(ProcessoAutenticacaoServicePadrao.class).put("login", login);
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
                    throw new ProcessoAutenticacaoException.ServicoIndisponivel();
                } else {
                    throw new ProcessoAutenticacaoException.ServicoIndisponivel();
                }
            }
        }
        throw new ProcessoAutenticacaoException.ExcessoTentativas();
    }

    @Override
    public void executarLogoff() {
        ProgressUtils.showProgressDialogAndRun(new Runnable() {

            @Override
            public void run() {
                try {
                    if (SecurityService.Lookup.getDefault().getCurrentAuthenticatedUser()!= null) {
                        SecurityService.Lookup.getDefault().logoff();
                    }
                } catch (Exception ex) {
                    // ignora
                }
            }
        }, "Logoff...");
    }
}
