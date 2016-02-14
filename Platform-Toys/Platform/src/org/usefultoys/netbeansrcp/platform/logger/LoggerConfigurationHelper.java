/*
 * Este arquivo pertence à Petrobras e não pode ser utilizado fora
 * desta empresa sem prévia autorização.
 */
package org.usefultoys.netbeansrcp.platform.logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;

/**
 *
 * @author x7ws
 */
public interface LoggerConfigurationHelper {

    static final String PROPERTY_NAME_OPTIONAL_SUFFIX = ".level";
    static final int PROPERTY_NAME_OPTIONAL_SUFFIX_LENGTH = PROPERTY_NAME_OPTIONAL_SUFFIX.length();

    /**
     * Clear all existing handler configuration. Some handlers were configured
     * with levels that would filter our messages.
     */
    static void resetExistingHandlerLevel() {
        final Logger rootLogger = Logger.getLogger("");
        for (final Handler handler : rootLogger.getHandlers()) {
            try {
                final Level before = handler.getLevel();
                if (before == Level.ALL) {
                    logInfo("Logger configuration: No need to change handler level. handler={0}", handler.getClass().getSimpleName());
                } else {
                    handler.setLevel(Level.ALL);
                    logInfo("Logger configuration: Changed handler level. handler={0}, before={1}", handler.getClass().getSimpleName(), before);
                }
            } catch (SecurityException e) {
                logWarn("Logger configuration: Not allowed change handler level. handler={0}, exception={1}", handler.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * Clear all existing native logger configuration. Reinitialize the logging
     * properties and re-read the logging configuration.
     */
    static void resetLogManager() {
        try {
            LogManager.getLogManager().readConfiguration();
            LoggerConfigurationHelper.logInfo("Logger configuration: Reinitialized LogManager.");
        } catch (IOException e) {
            LoggerConfigurationHelper.logWarn("Logger configuration: Failed to reinitialize LogManager. exception={0}", e.getMessage());
        } catch (SecurityException e) {
            LoggerConfigurationHelper.logWarn("Logger configuration: Not allowed to reinitialize LogManager. exception={0}", e.getMessage());
        }
    }

    /**
     * Print existing logger configuration.
     */
    static void printLogManagerConfiguration() {
        final Enumeration<String> enumeration = LogManager.getLogManager().getLoggerNames();
        final List<String> loggerNames = new ArrayList<>(Collections.list(enumeration));
        Collections.sort(loggerNames);
        for (final String loggerName : loggerNames) {
            final Logger logger = LogManager.getLogManager().getLogger(loggerName);
            if (logger.getLevel() != null) {
                logInfo("Logger configuration: logger={0}, level={1}", loggerName, logger.getLevel());
            }
        }
    }

    /**
     * Load all properties from the given properties repository as logger level
     * configuration.
     *
     * @param properties the properties repository
     */
    static void readLoggerLevelFromProperties(Properties properties) {
        for (String name : properties.stringPropertyNames()) {
            final Object levelStr = properties.getProperty(name);
            applyLoggerLevel(name, levelStr);
        }
    }

    /**
     * Load all properties from the given layer.xml file object as logger level
     * configuration.
     *
     * @param loggerFO file object that holds properties
     */
    static void readLoggerLevelFromFileObject(FileObject loggerFO) {
        final Enumeration<String> attributeEnum = loggerFO.getAttributes();
        while (attributeEnum.hasMoreElements()) {
            final String loggerName = attributeEnum.nextElement();
            final Object levelStr = loggerFO.getAttribute(loggerName);
            applyLoggerLevel(loggerName, (String) levelStr);
        }
    }

    /**
     * Apply a logger configuration as system property that will be understood
     * by {@link #resetLogManager()}.
     *
     * @param loggerName logger name
     * @param value one level name from java native logger, SLF4J or LOG4J.
     */
    static void applyLoggerLevel(String loggerName, final Object value) {
        if (loggerName.endsWith(PROPERTY_NAME_OPTIONAL_SUFFIX)) {
            loggerName = loggerName.substring(0, loggerName.length() - PROPERTY_NAME_OPTIONAL_SUFFIX_LENGTH);
        }

        final Logger logger = Logger.getLogger(loggerName);
        final Level level;
        try {
            if (value instanceof String) {
                final String stringValue = (String) value;
                final String levelName = stringValue.toUpperCase();
                switch (levelName) {
                    case "WARN":
                        level = Level.WARNING;
                        break;
                    case "DEBUG":
                        level = Level.FINER;
                        break;
                    case "TRACE":
                        level = Level.FINEST;
                        break;
                    case "FATAL":
                        level = Level.SEVERE;
                        break;
                    case "ERROR":
                        level = Level.SEVERE;
                        break;
                    default:
                        try {
                            level = Level.parse(levelName);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Logger level must be one of Level enum. name=" + loggerName + " stringvalue=" + levelName, e);
                        }
                        break;
                }
            } else {
                throw new IllegalArgumentException("Attribute value must be string. name=" + loggerName);
            }
        } catch (IllegalArgumentException e) {
            logWarn("Logger configuration: Incorrect logger configuration. " + e.getMessage());
            return;
        }
        try {
            logger.setLevel(level);
            System.setProperty(loggerName + PROPERTY_NAME_OPTIONAL_SUFFIX, level.getName());
            LogManager.getLogManager().getLogger(loggerName);
        } catch (SecurityException e) {
            logWarn("Logger configuration: Not allowed to change logger level. logger=" + loggerName + " level=" + level.getName(), e);
        }
    }

    static void logWarn(String message, Object... parameters) {
        Logger logger = Logger.getLogger("");
        if (logger.isLoggable(Level.WARNING)) {
            LogRecord record = new LogRecord(Level.WARNING, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    static void logInfo(String message, Object... parameters) {
        Logger logger = Logger.getLogger("");
        if (logger.isLoggable(Level.INFO)) {
            LogRecord record = new LogRecord(Level.INFO, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    static void logSereve(String message, Throwable throwable, Object... parameters) {
        Logger logger = Logger.getLogger("");
        if (logger.isLoggable(Level.SEVERE)) {
            LogRecord record = new LogRecord(Level.SEVERE, message);
            record.setParameters(parameters);
            record.setThrown(throwable);
            logger.log(record);
        }
    }
}
