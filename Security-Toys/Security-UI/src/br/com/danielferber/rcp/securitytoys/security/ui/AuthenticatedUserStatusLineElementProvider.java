package br.com.danielferber.rcp.securitytoys.security.ui;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.spi.AuthenticationListener;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(service = StatusLineElementProvider.class, position = 1),
    @ServiceProvider(service = AuthenticationListener.class)
})
@NbBundle.Messages({
    "UserNameAnonimous=Anônimo"
})
public class AuthenticatedUserStatusLineElementProvider implements StatusLineElementProvider, AuthenticationListener {

    private JLabel label;

    public AuthenticatedUserStatusLineElementProvider() {
        super();
    }

    @Override
    public Component getStatusLineElement() {
        if (label == null) {
            /* Create swing component only once. */
            label = createStatusLineLabel();
        }
        return label;
    }

    protected JLabel createStatusLineLabel() {
        final JLabel label = new JLabel(retrieveUserNameLabelText());
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    UserPropertiesAction.runAction(AuthenticatedUserStatusLineElementProvider.this);
                }
            }
        });
        return label;
    }

    @Override
    public void notificarAutenticacao(final AuthenticatedUser usuario) {
        if (label != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    label.setText(retrieveUserNameLabelText());
                }
            });
        }
    }

    protected String retrieveUserNameLabelText() {
        final AuthenticatedUser usuario = SecurityService.Lookup.getDefault().getCurrentAuthenticatedUser();
        if (usuario == null) {
            return Bundle.UserNameAnonimous();
        } else {
            return usuario.getName();
        }
    }
}
