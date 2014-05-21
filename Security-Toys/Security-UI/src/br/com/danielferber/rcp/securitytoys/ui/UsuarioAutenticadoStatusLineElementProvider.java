/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.ui;

import br.com.danielferber.rcp.securitytoys.api.SegurancaListener;
import br.com.danielferber.rcp.securitytoys.api.SegurancaService;
import br.com.danielferber.rcp.securitytoys.api.UsuarioAutenticado;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(service = StatusLineElementProvider.class, position = 1),
    @ServiceProvider(service = SegurancaListener.class)
})
public class UsuarioAutenticadoStatusLineElementProvider implements StatusLineElementProvider, SegurancaListener {

    private JLabel usuarioLabel;

    public UsuarioAutenticadoStatusLineElementProvider() {
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
    public void notificarAutenticacao(final UsuarioAutenticado usuario) {
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
        final UsuarioAutenticado usuario = SegurancaService.getDefault().getUsuario();
        if (usuario == null) {
            return "Anônimo";
        } else {
            return usuario.getNome();
        }
    }

}
