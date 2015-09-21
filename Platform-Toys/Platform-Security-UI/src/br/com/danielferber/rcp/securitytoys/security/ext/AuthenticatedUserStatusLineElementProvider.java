package br.com.danielferber.rcp.securitytoys.security.ext;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.spi.AuthenticationListener;
import br.com.danielferber.rcp.securitytoys.security.ui.AuthenticatedUserComponent;
import java.awt.Component;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(service = StatusLineElementProvider.class, position = 1),
    @ServiceProvider(service = AuthenticationListener.class)
})
public class AuthenticatedUserStatusLineElementProvider implements StatusLineElementProvider, AuthenticationListener {

    private AuthenticatedUserComponent instance;

    public AuthenticatedUserStatusLineElementProvider() {
        super();
    }

    @Override
    public Component getStatusLineElement() {
        if (instance == null) {
            /* Create swing component only once. */
            instance = createStatusLineLabel();
        }
        return instance;
    }

    protected AuthenticatedUserComponent createStatusLineLabel() {
        return new AuthenticatedUserComponent();
    }

    @Override
    public void notifyAuthenticatedUser(final AuthenticatedUser usuario) {
        if (instance != null) {
            instance.update();
        }
    }

 
}
