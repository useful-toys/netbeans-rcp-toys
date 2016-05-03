/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.ext;

import java.util.Map;
import java.util.logging.Logger;
import org.usefultoys.netbeansrcp.platform.messages.api.Report;
import org.usefultoys.netbeansrcp.platform.messages.spi.ReportListener;

public class ReportLogger implements ReportListener {

    @Override
    public void start(Report report) {
        Logger.getLogger(report.getCategory()).fine(readableString(report, new StringBuilder()).toString());
    }

    @Override
    public void progress(Report report) {
        Logger.getLogger(report.getCategory()).info(readableString(report, new StringBuilder()).toString());
    }

    @Override
    public void ok(Report report) {
        Logger.getLogger(report.getCategory()).info(readableString(report, new StringBuilder()).toString());
    }

    @Override
    public void reject(Report report) {
        Logger.getLogger(report.getCategory()).info(readableString(report, new StringBuilder()).toString());
    }

    @Override
    public void fail(Report report) {
        Logger.getLogger(report.getCategory()).severe(readableString(report, new StringBuilder()).toString());
    }

    protected StringBuilder readableString(Report report, final StringBuilder buffer) {
        if (report.getStopTime() != 0) {
            if (report.isOK()) {
//                if (report.isSlow()) {
//                    buffer.append("OK (Slow)");
//                } else {
                buffer.append("OK");
//                }
                if (report.getPathId() != null) {
                    buffer.append(" [");
                    buffer.append(report.getPathId());
                    buffer.append(']');
                }
            } else if (report.isReject()) {
                buffer.append("REJECT");
                if (report.getRejectId() != null) {
                    buffer.append(" [");
                    buffer.append(report.getRejectId());
                    buffer.append(']');
                }
            } else {
                buffer.append("FAIL");
                final Throwable t = report.getFailThrowable();
                if (t != null) {
                    buffer.append(" [");
                    buffer.append(t.getClass());
                    if (t.getMessage() != null) {
                        buffer.append("; ");
                        buffer.append(t.getMessage());
                    }
                    buffer.append(']');
                }
            }
        } else if (report.getStartTime() != 0 && report.getCurrentIteration() == 0) {
            buffer.append("Started");
        } else if (report.getStartTime() != 0) {
            buffer.append("Progress ");
            buffer.append(report.getCurrentIteration());
            if (report.getExpectedIterations() > 0) {
                buffer.append('/');
                buffer.append(report.getExpectedIterations());
            }
        } else {
            buffer.append("Scheduled");
        }
        if (report.getTitle() != null) {
            buffer.append(": ");
            buffer.append(report.getTitle());
        } else if (report.getName() != null) {
            buffer.append(": ");
            buffer.append(report.getName());
        }

        if (report.getStartTime() > 0) {
            buffer.append("; ");
            buffer.append(UnitFormatter.nanoseconds(report.getExecutionTime()));
            if (report.getCurrentIteration() > 0) {
                buffer.append("; ");
                final double iterationsPerSecond = report.getIterationsPerSecond();
                buffer.append(UnitFormatter.iterationsPerSecond(iterationsPerSecond));
                buffer.append(' ');
                final double nanoSecondsPerIteration = 1.0F / iterationsPerSecond * 1000000000;
                buffer.append(UnitFormatter.nanoseconds(nanoSecondsPerIteration));
            }
        }
        
        final Runtime runtime = Runtime.getRuntime();
        long runtime_usedMemory = runtime.totalMemory() - runtime.freeMemory();
        buffer.append("; ");
        buffer.append(UnitFormatter.bytes(runtime_usedMemory));

        if (report.getContext() != null) {
            for (final Map.Entry<String, String> entry : report.getContext().entrySet()) {
                buffer.append("; ");
                buffer.append(entry.getKey());
                if (entry.getValue() != null) {
                    buffer.append("=");
                    buffer.append(entry.getValue());
                }
            }
        }
        return buffer;
    }
}
