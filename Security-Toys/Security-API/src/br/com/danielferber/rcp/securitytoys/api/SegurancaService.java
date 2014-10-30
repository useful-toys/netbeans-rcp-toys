/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.api;

import java.util.ArrayList;
import java.util.List;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel
 */
public abstract class SegurancaService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SegurancaService.class);

    protected SegurancaService() {
        super();
    }

    public static final SegurancaService getDefault() {
        final SegurancaService instance = Lookup.getDefault().lookup(SegurancaService.class);
        if (instance == null) {
            throw new IllegalStateException("Nenhum módulo provê SegurancaService.");
        }
        return instance;
    }

    public abstract boolean isDisponivel();
    
    /**
     * @return O usuário autenticado no momento ou null se não houver.
     */
    public abstract UsuarioAutenticado getUsuario();

    public abstract UsuarioAutenticado login(String login, char [] senha) throws AutenticacaoException.CredenciaisIncorretas, AutenticacaoException.ServicoIndisponivel, AutenticacaoException.UsuarioInativo, AutenticacaoException.UsuarioInexistente;
    public abstract AutenticacaoException getLoginException();

    public abstract void logoff();
    
    public abstract void register(SegurancaListener listener);
    public abstract void unregister(SegurancaListener listener);
}
