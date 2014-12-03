/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.api;

import java.util.Set;

/**
 *
 * @author Daniel
 */
public interface AuthenticatedUser {

    String getLogin();

    String getNome();

    Set<String> getPerfis();

    /**
     * Verifica se o usuário está autorizado a acessar o recurso indicado. Use
     * este método para decidir se o item de interface associado ao recurso está
     * visível ou habilitado.
     *
     * @param nomeRecurso Nome do recurso para verificar.
     * @return <code>true</code> se o usuário está autorizado a acessar o
     * recurso. <code>false</code> caso contrário.
     */
    boolean temPermissao(final String nomeRecurso);
    
    /**
     * Lança uma exceção se o usuário não está autorizado a acessar o recurso
     * indicado.
     *
     * @param nomeRecurso Nome do recurso para verificar.
     * @throws AutorizacaoException se o usuário não está autorizado a
     * acessar o recurso.
     */
    void garantePermissao(final String nomeRecurso) throws AutorizacaoException; 
}