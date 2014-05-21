/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.auth.api;

public class ProcessoAutenticacaoException extends RuntimeException {

    public ProcessoAutenticacaoException() {
        super();
    }

    public static class ExcessoTentativas extends ProcessoAutenticacaoException {

    }

    public static class ServicoIndisponivel extends ProcessoAutenticacaoException {

    }
    
    public static class ProcessoCancelado extends ProcessoAutenticacaoException {

    }

}
