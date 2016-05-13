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
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.usefultoys.netbeansrcp.platform.reporter.Reporter;
import org.usefultoys.netbeansrcp.platform.reporter.ReporterService;

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

    int counter = 1;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread() {
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                ReporterService s = ReporterService.getDefault();
                Reporter r1 = s.createReporter("teste").title("Ope 1 "+counter++).start();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                r1.ok();

                Reporter r2 = s.createReporter("teste").title("Ope 2 "+counter++).start();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                r2.fail(new Exception());

                Reporter r3 = s.createReporter("teste").title("Ope 3 "+counter++).start();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                r3.reject("bla");
            }

        }.start();
    }
}
