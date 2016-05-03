/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.api;

import java.util.logging.Logger;

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
    public static ReporterService getDefault() {
        return org.openide.util.Lookup.getDefault().lookup(ReporterService.class);
    }

}
