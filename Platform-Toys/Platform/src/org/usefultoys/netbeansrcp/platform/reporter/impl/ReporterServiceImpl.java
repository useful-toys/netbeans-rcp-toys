/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.impl;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.usefultoys.netbeansrcp.platform.reporter.Report;
import org.usefultoys.netbeansrcp.platform.reporter.Reporter;
import org.usefultoys.netbeansrcp.platform.reporter.ReporterService;

/**
 * Default implementation for {@link ReporterService}.
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = ReporterService.class)
public class ReporterServiceImpl implements ReporterService {

    private static class ReportDispatcherImpl implements ReportDispatcher {

        public static Logger PARENT_LOGGER = Logger.getLogger(ReporterService.class.getName());
        public static Logger LOGGER = Logger.getLogger(ReportDispatcher.class.getName());

        @Override
        public void start(Report report) {
            Collection<? extends ReportListener> listeners = Lookup.getDefault().lookupAll(ReportListener.class);
            logDispatch("start", listeners, report);
            for (ReportListener listener : listeners) {
                try {
                    listener.start(report);
                    logListenerSuccess("start", listener);
                } catch (Exception e) {
                    logListenerException("start", listener, e);
                }
            }
        }

        @Override
        public void progress(Report report) {
            Collection<? extends ReportListener> listeners = Lookup.getDefault().lookupAll(ReportListener.class);
            logDispatch("progress", listeners, report);
            for (ReportListener listener : listeners) {
                try {
                    listener.progress(report);
                    logListenerSuccess("progress", listener);
                } catch (Exception e) {
                    logListenerException("progress", listener, e);
                }
            }
        }

        @Override
        public void ok(Report report) {
            Collection<? extends ReportListener> listeners = Lookup.getDefault().lookupAll(ReportListener.class);
            logDispatch("ok", listeners, report);
            for (ReportListener listener : listeners) {
                try {
                    listener.ok(report);
                    logListenerSuccess("ok", listener);
                } catch (Exception e) {
                    logListenerException("ok", listener, e);
                }
            }
        }

        @Override
        public void reject(Report report) {
            Collection<? extends ReportListener> listeners = Lookup.getDefault().lookupAll(ReportListener.class);
            logDispatch("reject", listeners, report);
            for (ReportListener listener : listeners) {
                try {
                    listener.reject(report);
                    logListenerSuccess("reject", listener);
                } catch (Exception e) {
                    logListenerException("reject", listener, e);
                }
            }
        }

        @Override
        public void fail(Report report) {
            Collection<? extends ReportListener> listeners = Lookup.getDefault().lookupAll(ReportListener.class);
            logDispatch("fail", listeners, report);
            for (ReportListener listener : listeners) {
                try {
                    listener.fail(report);
                    logListenerSuccess("fail", listener);
                } catch (Exception e) {
                    logListenerException("fail", listener, e);
                }
            }
        }

        private void logListenerException(String eventName, ReportListener listener, Exception e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LogRecord record = new LogRecord(Level.SEVERE, "Reporter listener failed. event={0}; class={1}");
                record.setParameters(new Object[] {eventName, listener.getClass().getName()});
                record.setThrown(e);
                LOGGER.log(record);
            }
        }

        private void logListenerSuccess(String eventName, ReportListener listener) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Reporter listener succeded. event={0}; class={1}", new Object[]{eventName, listener.getClass().getName()});
            }
        }

        private void logDispatch(String eventName, Collection<? extends ReportListener> listeners, Report report) {
            if (PARENT_LOGGER.isLoggable(Level.INFO)) {
                PARENT_LOGGER.log(Level.INFO, "Reporter listener to listeners. event={0}; #listeners={1}; report={2}", new Object[]{eventName, listeners.size(), report});
            }
        }
    };

    private final ReportDispatcherImpl dispatcher = new ReportDispatcherImpl();

    @Override
    public Reporter createReporter(Logger logger) {
        return new ReporterImpl(this, logger.getName());
    }

    @Override
    public Reporter createReporter(String category) {
        return new ReporterImpl(this, category);
    }

    @Override
    public Reporter createReporter(Class<?> clazz) {
        return new ReporterImpl(this, clazz.getName());
    }

    @Override
    public Reporter createReporter(Class<?> clazz, String name) {
        return new ReporterImpl(this, clazz.getName(), name);
    }

    @Override
    public Reporter createReporter(Logger logger, String name) {
        return new ReporterImpl(this, logger.getName(), name);
    }

    @Override
    public ReportDispatcher getDispatcher() {
        return dispatcher;
    }
}
