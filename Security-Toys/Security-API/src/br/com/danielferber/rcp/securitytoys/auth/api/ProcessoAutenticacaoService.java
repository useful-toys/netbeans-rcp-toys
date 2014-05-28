/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.auth.api;

import br.com.danielferber.rcp.securitytoys.api.UsuarioAutenticado;
import org.openide.util.Lookup;

public abstract class ProcessoAutenticacaoService {

    protected ProcessoAutenticacaoService() {
        super();
    }

    public static final ProcessoAutenticacaoService getDefault() {
        final ProcessoAutenticacaoService instance = Lookup.getDefault().lookup(ProcessoAutenticacaoService.class);
        if (instance == null) {
            throw new IllegalStateException("Nenhum módulo provê ProcessoAutenticacaoService.");
        }
        return instance;
    }

    public abstract UsuarioAutenticado executarAutenticacao() throws ProcessoAutenticacaoException;
    public abstract void executarLogoff();

    

}
