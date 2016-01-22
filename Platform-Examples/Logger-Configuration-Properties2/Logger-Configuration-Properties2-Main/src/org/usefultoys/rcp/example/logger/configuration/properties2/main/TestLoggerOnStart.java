/*
 * Este arquivo pertence à Petrobras e não pode ser utilizado fora
 * desta empresa sem prévia autorização.
 */
package org.usefultoys.rcp.example.logger.configuration.properties2.main;

import java.util.logging.Logger;
import org.openide.modules.OnStart;
import org.openide.windows.WindowManager;

/**
 *
 * @author x7ws
 */
@OnStart
public class TestLoggerOnStart implements Runnable {

    @Override
    public void run() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                Logger.getLogger("test1.A").finest("Finest");
                Logger.getLogger("test1.A").finer("Finer");
                Logger.getLogger("test1.A").fine("Fine");
                Logger.getLogger("test1.A").config("Config");
                Logger.getLogger("test1.A").info("Info");
                Logger.getLogger("test1.A").warning("Warning");
                Logger.getLogger("test1.A").severe("Severe");
                Logger.getLogger("test1.B").finest("Finest");
                Logger.getLogger("test1.B").finer("Finer");
                Logger.getLogger("test1.B").fine("Fine");
                Logger.getLogger("test1.B").config("Config");
                Logger.getLogger("test1.B").info("Info");
                Logger.getLogger("test1.B").warning("Warning");
                Logger.getLogger("test1.B").severe("Severe");
                Logger.getLogger("test1.C").finest("Finest");
                Logger.getLogger("test1.C").finer("Finer");
                Logger.getLogger("test1.C").fine("Fine");
                Logger.getLogger("test1.C").config("Config");
                Logger.getLogger("test1.C").info("Info");
                Logger.getLogger("test1.C").warning("Warning");
                Logger.getLogger("test1.C").severe("Severe");
                Logger.getLogger("test1.D").finest("Finest");
                Logger.getLogger("test1.D").finer("Finer");
                Logger.getLogger("test1.D").fine("Fine");
                Logger.getLogger("test1.D").config("Config");
                Logger.getLogger("test1.D").info("Info");
                Logger.getLogger("test1.D").warning("Warning");
                Logger.getLogger("test1.D").severe("Severe");
                Logger.getLogger("test1.E").finest("Finest");
                Logger.getLogger("test1.E").finer("Finer");
                Logger.getLogger("test1.E").fine("Fine");
                Logger.getLogger("test1.E").config("Config");
                Logger.getLogger("test1.E").info("Info");
                Logger.getLogger("test1.E").warning("Warning");
                Logger.getLogger("test1.E").severe("Severe");

                Logger.getLogger("test2.A").finest("Finest");
                Logger.getLogger("test2.A").finer("Finer");
                Logger.getLogger("test2.A").fine("Fine");
                Logger.getLogger("test2.A").config("Config");
                Logger.getLogger("test2.A").info("Info");
                Logger.getLogger("test2.A").warning("Warning");
                Logger.getLogger("test2.A").severe("Severe");
                Logger.getLogger("test2.B").finest("Finest");
                Logger.getLogger("test2.B").finer("Finer");
                Logger.getLogger("test2.B").fine("Fine");
                Logger.getLogger("test2.B").config("Config");
                Logger.getLogger("test2.B").info("Info");
                Logger.getLogger("test2.B").warning("Warning");
                Logger.getLogger("test2.B").severe("Severe");
                Logger.getLogger("test2.C").finest("Finest");
                Logger.getLogger("test2.C").finer("Finer");
                Logger.getLogger("test2.C").fine("Fine");
                Logger.getLogger("test2.C").config("Config");
                Logger.getLogger("test2.C").info("Info");
                Logger.getLogger("test2.C").warning("Warning");
                Logger.getLogger("test2.C").severe("Severe");
                Logger.getLogger("test2.D").finest("Finest");
                Logger.getLogger("test2.D").finer("Finer");
                Logger.getLogger("test2.D").fine("Fine");
                Logger.getLogger("test2.D").config("Config");
                Logger.getLogger("test2.D").info("Info");
                Logger.getLogger("test2.D").warning("Warning");
                Logger.getLogger("test2.D").severe("Severe");
                Logger.getLogger("test2.E").finest("Finest");
                Logger.getLogger("test2.E").finer("Finer");
                Logger.getLogger("test2.E").fine("Fine");
                Logger.getLogger("test2.E").config("Config");
                Logger.getLogger("test2.E").info("Info");
                Logger.getLogger("test2.E").warning("Warning");
                Logger.getLogger("test2.E").severe("Severe");
            }
        });
    }

}
