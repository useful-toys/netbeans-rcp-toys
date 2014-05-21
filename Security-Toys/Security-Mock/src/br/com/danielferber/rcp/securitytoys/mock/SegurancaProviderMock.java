/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.mock;

import br.com.danielferber.rcp.securitytoys.api.AutorizacaoException;
import br.com.danielferber.rcp.securitytoys.api.SegurancaProvider;
import br.com.danielferber.rcp.securitytoys.api.UsuarioAutenticado;
import br.com.danielferber.rcp.securitytoys.core.UsuarioAutenticadoPadrao;
import java.util.Collections;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Daniel
 */
@ServiceProvider(service = SegurancaProvider.class)
public class SegurancaProviderMock implements SegurancaProvider {

    UsuarioAutenticado usuarioMock = null;

    @Override
    public UsuarioAutenticado login(String usuario, char[] senha) {
        return usuarioMock = new UsuarioAutenticadoPadrao(usuario, usuario, Collections.EMPTY_SET) {

            @Override
            protected AutorizacaoException avaliaPermissao(String nomeRecurso) {
                return null;
            }
        };
    }

    @Override
    public void logoff() {
        // nada
    }

    @Override
    public boolean isDisponivel() {
        return true;
    }

}
