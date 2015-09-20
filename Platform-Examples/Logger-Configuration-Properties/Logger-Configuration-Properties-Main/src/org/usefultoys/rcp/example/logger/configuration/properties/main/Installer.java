/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.example.logger.configuration.properties.main;

import java.util.logging.Logger;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                Logger.getLogger("test.A").fine("Fine on A");
                Logger.getLogger("test.A").info("Info on A");
                Logger.getLogger("test.A").warning("Warning on A");
                Logger.getLogger("test.A").severe("Severe on A");
                Logger.getLogger("test.B").fine("Fine on B");
                Logger.getLogger("test.B").info("Info on B");
                Logger.getLogger("test.B").warning("Warning on B");
                Logger.getLogger("test.B").severe("Severe on B");
            }
        });
    }

}
