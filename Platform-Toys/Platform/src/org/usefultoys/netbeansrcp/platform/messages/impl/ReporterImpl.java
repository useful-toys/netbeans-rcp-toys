/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.impl;

import org.usefultoys.netbeansrcp.platform.messages.api.Reporter;

public class ReporterImpl implements Reporter {

    ReportImpl report = new ReportImpl();

    public ReporterImpl(String category) {
        this.report.category = category;
    }

    public ReporterImpl(String category, String name) {
        this.report.category = category;
        this.report.name = name;
    }

    @Override
    public Reporter start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reporter path(Object pathId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reporter ok() {
        final long newStopTime = System.nanoTime();
        report.stopTime = newStopTime;
        report.failThrowable = null;
        report.rejectId = null;
        report.pathId = null;
        return this;
    }

    @Override
    public Reporter ok(Object pathId) {
        final long newStopTime = System.nanoTime();
        report.stopTime = newStopTime;
        report.failThrowable = null;
        report.rejectId = null;
        if (pathId instanceof String) {
            report.pathId = (String) pathId;
        } else if (pathId instanceof Enum) {
            report.pathId = ((Enum<?>) pathId).name();
        } else if (pathId instanceof Throwable) {
            report.pathId = pathId.getClass().getSimpleName();
        } else if (pathId != null) {
            report.pathId = pathId.toString();
        }
        return this;
    }

    @Override
    public Reporter reject(Object cause) {
        final long newStopTime = System.nanoTime();
        report.stopTime = newStopTime;
        report.failThrowable = null;
        report.pathId = null;
        if (cause instanceof String) {
            report.rejectId = (String) cause;
        } else if (cause instanceof Enum) {
            report.rejectId = ((Enum<?>) cause).name();
        } else if (cause instanceof Throwable) {
            report.rejectId = cause.getClass().getSimpleName();
        } else if (cause != null) {
            report.rejectId = cause.toString();
        }
        return this;
    }

    @Override
    public Reporter fail(Throwable cause) {
        final long newStopTime = System.nanoTime();
        report.stopTime = newStopTime;
        report.failThrowable = null;
        report.pathId = null;
        report.rejectId = null;
        report.failThrowable = cause;
        return this;
    }

}
