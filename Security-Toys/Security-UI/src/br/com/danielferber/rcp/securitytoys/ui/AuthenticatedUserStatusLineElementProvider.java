package br.com.danielferber.rcp.securitytoys.ui;

import br.com.danielferber.rcp.securitytoys.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.api.AuthenticationListener;
import br.com.danielferber.rcp.securitytoys.api.SecurityService;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(service = StatusLineElementProvider.class, position = 1),
    @ServiceProvider(service = AuthenticationListener.class)
})
public class AuthenticatedUserStatusLineElementProvider implements StatusLineElementProvider, AuthenticationListener {

    private JLabel usuarioLabel;

    public AuthenticatedUserStatusLineElementProvider() {
        super();
    }

    @Override
    public Component getStatusLineElement() {
        if (usuarioLabel == null) {
            usuarioLabel = new JLabel(obterStringNomeUsuario());
        }
        return usuarioLabel;
    }

    @Override
    public void notificarAutenticacao(final AuthenticatedUser usuario) {
        if (usuarioLabel != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    usuarioLabel.setText(obterStringNomeUsuario());
                }
            });
        }
    }

    private String obterStringNomeUsuario() {
        final AuthenticatedUser usuario = SecurityService.Lookup.getDefault().getCurrentAuthenticatedUser();
        if (usuario == null) {
            return "Anônimo";
        } else {
            return usuario.getName();
        }
    }

}
