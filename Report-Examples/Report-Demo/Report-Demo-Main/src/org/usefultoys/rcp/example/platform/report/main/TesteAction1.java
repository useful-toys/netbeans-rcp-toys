/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.example.platform.report.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.usefultoys.netbeansrcp.platform.messages.api.ReporterService;

@ActionID(
        category = "Teste",
        id = "org.usefultoys.rcp.example.platform.report.main.TesteAction1"
)
@ActionRegistration(
        displayName = "#CTL_TesteAction1"
)
@ActionReference(path = "Menu/Teste", position = 100)
@Messages("CTL_TesteAction1=Teste 1")
public final class TesteAction1 implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ReporterService s = ReporterService.getDefault();
        ReporterService r = s.createReporter("teste");
    }
}
