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
import org.openide.util.lookup.ServiceProvider;
import br.com.danielferber.rcp.securitytoys.api.AutenticacaoException;
import br.com.danielferber.rcp.securitytoys.api.SegurancaService;
import br.com.danielferber.rcp.securitytoys.api.UsuarioAutenticado;
import br.com.danielferber.rcp.securitytoys.auth.api.ProcessoAutenticacaoException;
import br.com.danielferber.rcp.securitytoys.auth.api.ProcessoAutenticacaoService;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.util.NbPreferences;

@ServiceProvider(service = ProcessoAutenticacaoService.class)
public class ProcessoAutenticacaoServicePadrao extends ProcessoAutenticacaoService {

    public ProcessoAutenticacaoServicePadrao() {
        super();
    }
    private static final int MAXIMO_TENTATIVAS = 3;

    @Override
    public UsuarioAutenticado executarAutenticacao() throws ProcessoAutenticacaoException {

        if (!SegurancaService.getDefault().isDisponivel()) {
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

                try {
                    /* Recorre ao serviço de autenticação. */
                    SegurancaService.getDefault().login(login, senha);
                    NbPreferences.forModule(ProcessoAutenticacaoServicePadrao.class).put("login", login);

                    /* Se a execução chegou aqui, então o login foi aceito. */
                    return SegurancaService.getDefault().getUsuario();

                } catch (final AutenticacaoException.CredenciaisIncorretas e) {
                    contadorTentativas++;
                    notificationLine.setErrorMessage("Estas credenciais estão incorretas.");
                } catch (final AutenticacaoException.UsuarioInexistente e) {
                    contadorTentativas++;
                    notificationLine.setErrorMessage("Estas credenciais estão incorretas.");
                } catch (final AutenticacaoException.UsuarioInativo e) {
                    contadorTentativas++;
                    notificationLine.setErrorMessage("Estas credenciais não esão ativas.");
                } catch (final AutenticacaoException.ServicoIndisponivel e) {
                    notificationLine.setErrorMessage("O serviço de autenticação está indisponível no momento.");
                }
            }
        }
        throw new ProcessoAutenticacaoException.ExcessoTentativas();
    }
}
