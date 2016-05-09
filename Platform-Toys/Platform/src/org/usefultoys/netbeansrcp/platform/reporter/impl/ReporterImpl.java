/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.usefultoys.netbeansrcp.platform.reporter.Reporter;

/**
 * Default implementation for {@link Reporter}.
 *
 * @author Daniel Felix Ferber
 */
public class ReporterImpl implements Reporter {

    private final ReporterServiceImpl parent;
    private final ReportImpl report;
    private long lastProgressTime = 0;
    private long lastProgressIteration = 0;

    public ReporterImpl(ReporterServiceImpl parent, String category) {
        this.report = new ReportImpl(category, null);
        this.parent = parent;
    }

    public ReporterImpl(ReporterServiceImpl parent, String category, String name) {
        this.report = new ReportImpl(category, name);
        this.parent = parent;
    }

    @Override
    public Reporter start() {
        this.lastProgressTime = this.report.startTime = System.nanoTime();
        parent.getDispatcher().start(report);
        return this;
    }

    @Override
    public Reporter path(Object pathId) {
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
    public Reporter ok() {
        report.stopTime = System.nanoTime();
        report.failThrowable = null;
        report.rejectId = null;
        report.pathId = null;
        parent.getDispatcher().ok(report);
        return this;
    }

    @Override
    public Reporter ok(Object pathId) {
        report.stopTime = System.nanoTime();
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
        parent.getDispatcher().ok(report);
        return this;
    }

    @Override
    public Reporter reject(Object cause) {
        report.stopTime = System.nanoTime();
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
        parent.getDispatcher().reject(report);
        return this;
    }

    @Override
    public Reporter fail(Throwable cause) {
        report.stopTime = System.nanoTime();
        report.failThrowable = null;
        report.pathId = null;
        report.rejectId = null;
        report.failThrowable = cause;
        parent.getDispatcher().fail(report);
        return this;
    }

}
