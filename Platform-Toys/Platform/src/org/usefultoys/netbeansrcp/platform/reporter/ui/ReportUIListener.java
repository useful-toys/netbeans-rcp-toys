/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.ui;

import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;
import org.usefultoys.netbeansrcp.platform.reporter.Report;

@ServiceProvider(service = org.usefultoys.netbeansrcp.platform.reporter.ReporterService.ReportListener.class)
public class ReportUIListener implements org.usefultoys.netbeansrcp.platform.reporter.ReporterService.ReportListener {

    private static ReportTopComponent TC_INSTANCE;
    
    private static ReportTopComponent findReportTopCompoent() {
        if (TC_INSTANCE == null) {
            TC_INSTANCE = (ReportTopComponent) WindowManager.getDefault().findTopComponent(ReportTopComponent.TOP_COMPONENT_PREFERRED_ID);
        }
        return TC_INSTANCE;
    }

    @Override
    public void start(Report report) {
        SwingUtilities.invokeLater(() -> {
            final ReportTopComponent tp = findReportTopCompoent();
            tp.start(report);
        });
    }

    @Override
    public void progress(Report report) {
        SwingUtilities.invokeLater(() -> {
            final ReportTopComponent tp = findReportTopCompoent();
            tp.progress(report);
        });
    }

    @Override
    public void ok(Report report) {
        SwingUtilities.invokeLater(() -> {
            final ReportTopComponent tp = findReportTopCompoent();
            tp.ok(report);
        });
    }

    @Override
    public void reject(Report report) {
        SwingUtilities.invokeLater(() -> {
            final ReportTopComponent tp = findReportTopCompoent();
            tp.reject(report);
        });
    }

    @Override
    public void fail(Report report) {
        SwingUtilities.invokeLater(() -> {
            final ReportTopComponent tp = findReportTopCompoent();
            tp.fail(report);
        });
    }
}
