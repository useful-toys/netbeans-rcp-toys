/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.ui;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface DialogConvention<Inbound, Outbound> {

    interface Support<Inbound, Outbound> {

        DialogConvention<Inbound, Outbound> getDialogConvention();
    }

    DialogState getDialogState();
   
    Outbound createOutbound();

    Inbound createInbound();

    void toFields(Inbound inbound);

    void fromFields(Outbound outbound);

    void executeValidation();
    
    void scheduleValidation();

}
