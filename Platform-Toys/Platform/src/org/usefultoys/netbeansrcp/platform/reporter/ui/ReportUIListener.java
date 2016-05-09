/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.ui;

import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;
import org.usefultoys.netbeansrcp.platform.reporter.Report;

@ServiceProvider(service = org.usefultoys.netbeansrcp.platform.reporter.ReporterService.ReportListener.class)
public class ReportUIListener implements org.usefultoys.netbeansrcp.platform.reporter.ReporterService.ReportListener {

    @Override
    public void start(Report report) {
        final ReportTopComponent tp = (ReportTopComponent) WindowManager.getDefault().findTopComponent(ReportTopComponent.TOP_COMPONENT_PREFERRED_ID);
        tp.start(report);
    }

    @Override
    public void progress(Report report) {
        final ReportTopComponent tp = (ReportTopComponent) WindowManager.getDefault().findTopComponent(ReportTopComponent.TOP_COMPONENT_PREFERRED_ID);
        tp.progress(report);
    }

    @Override
    public void ok(Report report) {
        final ReportTopComponent tp = (ReportTopComponent) WindowManager.getDefault().findTopComponent(ReportTopComponent.TOP_COMPONENT_PREFERRED_ID);
        tp.ok(report);
    }

    @Override
    public void reject(Report report) {
        final ReportTopComponent tp = (ReportTopComponent) WindowManager.getDefault().findTopComponent(ReportTopComponent.TOP_COMPONENT_PREFERRED_ID);
        tp.reject(report);
    }

    @Override
    public void fail(Report report) {
        final ReportTopComponent tp = (ReportTopComponent) WindowManager.getDefault().findTopComponent(ReportTopComponent.TOP_COMPONENT_PREFERRED_ID);
        tp.fail(report);
    }
}
