/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Daniel Felix Ferber
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.usefultoys.netbeansrcp.platform.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;
import org.usefultoys.netbeansrcp.platform.layer.ArgumentUrlLayer;
import org.usefultoys.netbeansrcp.platform.layer.SystemPropertyUrlLayer;

/**
 * Overrides the default logger configuration with properties loaded from two
 * possible sources: the layer.xml and the logger.properties file within the
 * user configuration directory.
 *
 * Note that, by using {@link SystemPropertyUrlLayer} and
 * {@link ArgumentUrlLayer}, your may provide additional layer.xml files located
 * outside your application.
 *
 * @see https://github.com/useful-toys/netbeans-rcp-toys/wiki/Logger-Configuration-Toys
 *
 * @author Daniel Felix Ferber
 */
@OnStart
public final class LoggerConfigurationInstaller implements Runnable {

    static final String PROPERTIES_FILE_NAME = "logger.properties";
    static final String FILE_OBJECT_NAME = "Logger";
    static final String PROPERTY_NAME_SUFFIX = ".level";
    static final int PROPERTY_NAME_SUFFIX_LENGTH = PROPERTY_NAME_SUFFIX.length();
    static final String EXAMPLE_LOGGER_NAME = LoggerConfigurationInstaller.class.getPackage().getName() + ".example";
    public static final String LOGGER_URL_SYSTEM_PROPERTY = "java.util.logging.config.file";

    @Override
    public void run() {
        resetExistingHandlerLevel();

        readLayerFileObject();
        readPropertiesFileFromSystemProperty();
        readPropertiesFileFromConfigurationDirectory();

        resetLogManager();

        printLogManagerConfiguration();
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

            /* 
             * Two property notations are supported:
             * - Just the property name.
             * - Property name suffixed with '.level' as used by the Java native logger configuration
             */
            if (name.endsWith(PROPERTY_NAME_SUFFIX)) {
                final String loggerName = name.substring(0, name.length() - PROPERTY_NAME_SUFFIX_LENGTH);
                applyLoggerLevel(loggerName, levelStr);
            } else {
                applyLoggerLevel(name, levelStr);
            }
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
     * Clear all existing handler configuration. Some handlers were configured
     * with levels that would filter our messages.
     */
    static void resetExistingHandlerLevel() {
        logInfo("Logger configuration: Change all handlers to level ALL.");

        final Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            try {
                handler.setLevel(Level.ALL);
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
        logInfo("Logger configuration: Reinitialize LogManager.");

        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            logWarn("Logger configuration: Failed to reinitialize LogManager. exception={0}", e.getMessage());
        } catch (SecurityException e) {
            logWarn("Logger configuration: Not allowed to reinitialize LogManager. exception={0}", e.getMessage());
        }
    }

    /**
     * Apply a logger configuration as system property that will be understood
     * by {@link #resetLogManager()}.
     *
     * @param loggerName logger name
     * @param value one level name from java native logger, SLF4J or LOG4J.
     */
    static void applyLoggerLevel(final String loggerName, final Object value) {
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
            System.setProperty(loggerName + PROPERTY_NAME_SUFFIX, level.getName());
            LogManager.getLogManager().getLogger(loggerName);
        } catch (SecurityException e) {
            logWarn("Logger configuration: Not allowed to change logger level. logger=" + loggerName + " level=" + level.getName(), e);
        }
    }

    /**
     * Load logger configuration from layer.xml file object.
     * Create xml if it does not yet exist.
     */
    static void readLayerFileObject() {
        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerFile = configRoot.getFileObject(FILE_OBJECT_NAME);
        if (loggerFile == null) {
            try {
                loggerFile = configRoot.createData(FILE_OBJECT_NAME);
                loggerFile.setAttribute(EXAMPLE_LOGGER_NAME, "INFO");
                logInfo("Logger configuration: Create layer.xml file object {0} at {1}", loggerFile.toURI(), FileUtil.toFile(loggerFile));
            } catch (IOException e) {
                logWarn("Logger configuration: Failed to create 'Logger' file in system filesystem. exception={}", e.getMessage());
            }
        } else {
            logInfo("Logger configuration: Load layer.xml file object {0} at {1}", loggerFile.toURI(), FileUtil.toFile(loggerFile));
            readLoggerLevelFromFileObject(loggerFile);
        }
    }

    /**
     * Load logger configuration from property file within user configuration
     * directory.
     */
    static void readPropertiesFileFromConfigurationDirectory() {

        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerPropertiesFile = configRoot.getFileObject(PROPERTIES_FILE_NAME);

        if (loggerPropertiesFile == null) {
            try {
                loggerPropertiesFile = configRoot.createData(PROPERTIES_FILE_NAME);
                logInfo("Logger configuration: Create {2} file {0} at {1}", loggerPropertiesFile.toURI(), FileUtil.toFile(loggerPropertiesFile), PROPERTIES_FILE_NAME);
                OutputStream os = loggerPropertiesFile.getOutputStream();
                Properties properties = new Properties();
                properties.put(EXAMPLE_LOGGER_NAME, "INFO");
                properties.store(os, "Example logger configuration");
                os.close();
            } catch (IOException e) {
                logWarn("Logger configuration: Failed to create {0} file in configuration directory. exception={1}", PROPERTIES_FILE_NAME, e);
            }
        } else {
            logInfo("Logger configuration: Load {2} file {0} at {1}", loggerPropertiesFile.toURI(), FileUtil.toFile(loggerPropertiesFile), PROPERTIES_FILE_NAME);
            Properties properties = new Properties();
            try (InputStream is = loggerPropertiesFile.getInputStream()) {
                properties.load(is);
            } catch (IOException e) {
                logWarn("Failed to load {1} in configuration directory. exception={0}", e, PROPERTIES_FILE_NAME);
            }
            readLoggerLevelFromProperties(properties);
        }
    }

    /**
     * Load logger configuration from property file within user configuration
     * directory.
     */
    static void readPropertiesFileFromSystemProperty() {

        final String urlString = System.getProperty(LOGGER_URL_SYSTEM_PROPERTY);
        if (urlString == null) {
            return;
        }
        final URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            logWarn("Logger configuration: System property {1} is a malformed URL. exception={0}", e.getMessage(), LOGGER_URL_SYSTEM_PROPERTY);
            return;
        }
        logInfo("Logger configuration: Load properties from URL given by {1}: {0}", url.toString(), LOGGER_URL_SYSTEM_PROPERTY);
        Properties properties = new Properties();
        try (InputStream is = url.openStream()) {
            properties.load(is);
        } catch (IOException e) {
            logWarn("Logger configuration: Failed to load properties from URL. exception={0}", e.getMessage());
        }
        readLoggerLevelFromProperties(properties);
    }

    static void printLogManagerConfiguration() {
        final Enumeration<String> enumeration = LogManager.getLogManager().getLoggerNames();
        List<String> loggerNames = new ArrayList<>(Collections.list(enumeration));
        Collections.sort(loggerNames);
        for (String loggerName : loggerNames) {
            final Logger logger = LogManager.getLogManager().getLogger(loggerName);
            if (logger.getLevel() != null) {
                logInfo("Logger configuration: {0}={1}", loggerName, logger.getLevel());
            }
        }
    }

    private static void logWarn(String message, Object... parameters) {
        Logger logger = Logger.getLogger("");
        if (logger.isLoggable(Level.WARNING)) {
            LogRecord record = new LogRecord(Level.WARNING, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void logInfo(String message, Object... parameters) {
        Logger logger = Logger.getLogger("");
        if (logger.isLoggable(Level.INFO)) {
            LogRecord record = new LogRecord(Level.INFO, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void logSereve(String message, Throwable throwable, Object... parameters) {
        Logger logger = Logger.getLogger("");
        if (logger.isLoggable(Level.SEVERE)) {
            LogRecord record = new LogRecord(Level.SEVERE, message);
            record.setParameters(parameters);
            record.setThrown(throwable);
            logger.log(record);
        }
    }
}
