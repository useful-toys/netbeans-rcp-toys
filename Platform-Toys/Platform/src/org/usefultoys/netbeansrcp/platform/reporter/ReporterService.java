/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter;

import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author x7ws
 */
public interface ReporterService {

    Reporter createReporter(Logger logger);

    Reporter createReporter(String category);

    Reporter createReporter(Class<?> clazz);

    Reporter createReporter(Class<?> clazz, String operationName);

    Reporter createReporter(Logger logger, String operationName);

    /**
     * @return the default {@link ReporterService} instance.
     */
    static ReporterService getDefault() {
        return Lookup.getDefault().lookup(ReporterService.class);
    }
    
    ReportDispatcher getDispatcher();

    interface ReportDispatcher {

        void start(Report report);

        void progress(Report report);

        void ok(Report report);

        void reject(Report report);

        void fail(Report report);
    }
    
    interface ReportListener extends ReportDispatcher {

    }

}
