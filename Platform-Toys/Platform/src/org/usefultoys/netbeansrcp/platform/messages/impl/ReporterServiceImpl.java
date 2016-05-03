/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.impl;

import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import org.usefultoys.netbeansrcp.platform.messages.api.Reporter;
import org.usefultoys.netbeansrcp.platform.messages.api.ReporterService;


@ServiceProvider(service = ReporterService.class)
public class ReporterServiceImpl implements ReporterService {

    @Override
    public Reporter createReporter(Logger logger) {
        return new ReporterImpl(logger.getName());
    }

    @Override
    public Reporter createReporter(String category) {
        return new ReporterImpl(category);
    }

    @Override
    public Reporter createReporter(Class<?> clazz) {
        return new ReporterImpl(clazz.getName());
    }

    @Override
    public Reporter createReporter(Class<?> clazz, String name) {
        return new ReporterImpl(clazz.getName(), name);
    }

    @Override
    public Reporter createReporter(Logger logger, String name) {
        return new ReporterImpl(logger.getName(), name);
    }
    
}
