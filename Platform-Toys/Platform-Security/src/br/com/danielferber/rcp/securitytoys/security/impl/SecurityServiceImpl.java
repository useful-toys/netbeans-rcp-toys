/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.impl;

import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import br.com.danielferber.rcp.securitytoys.security.core.SecurityServiceDefault;
import org.openide.util.lookup.ServiceProvider;


@ServiceProvider(service = SecurityService.class)
public class SecurityServiceImpl extends SecurityServiceDefault {
    
}
