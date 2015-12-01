/*
 */
package org.usefultoys.netbeansrcp.platform.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
 * @see https://github.com/useful-toys/netbeans-rcp-toys/wiki/Logger-Toys
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

    @Override
    public void run() {
        resetHandlerLevel();

        readLayerFileObject();
        readUserConfigurationPropertiesFile();

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
    static void resetHandlerLevel() {
        Logger.getLogger("").info("Logger configuration: set all handlers to level ALL.");

        final Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            try {
                handler.setLevel(Level.ALL);
            } catch (SecurityException e) {
                Logger.getLogger("").log(Level.WARNING, "Not allowed to change handler level. name=" + handler.getClass().getSimpleName() + " stringvalue=", e);
            }
        }
    }

    /**
     * Clear all existing native logger configuration. Reinitialize the logging
     * properties and reread the logging configuration.
     */
    static void resetLogManager() {
        Logger.getLogger("").info("Logger configuration: reinitialize LogManager.");

        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            Logger.getLogger("").log(Level.WARNING, "Failed to change logger configuration.", e);
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Not allowed to change logger configuration.", e);
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
                if ("WARN".equals(levelName)) {
                    level = Level.WARNING;
                } else if ("DEBUG".equals(levelName)) {
                    level = Level.FINER;
                } else if ("TRACE".equals(levelName)) {
                    level = Level.FINEST;
                } else if ("FATAL".equals(levelName)) {
                    level = Level.SEVERE;
                } else if ("ERROR".equals(levelName)) {
                    level = Level.SEVERE;
                } else {
                    try {
                        level = Level.parse(levelName);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Logger level not listed in Level enum. name=" + loggerName + " stringvalue=" + levelName, e);
                    }
                }
            } else {
                throw new IllegalArgumentException("Attribute values must be string. name=" + loggerName);
            }
        } catch (IllegalArgumentException e) {
            Logger.getLogger("").log(Level.WARNING, "Incorrect logger configuration.", e);
            return;
        }
        try {
            logger.setLevel(level);
            System.setProperty(loggerName + PROPERTY_NAME_SUFFIX, level.getName());
            LogManager.getLogManager().getLogger(loggerName);
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Not allowed to change logger level. name=" + loggerName + " level=" + level.getName(), e);
        }
    }

    /**
     * Load logger configuration from layer.xml file object.
     */
    static void readLayerFileObject() {
        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerFile = configRoot.getFileObject(FILE_OBJECT_NAME);
        if (loggerFile == null) {
            try {
                loggerFile = configRoot.createData(FILE_OBJECT_NAME);
                loggerFile.setAttribute(EXAMPLE_LOGGER_NAME, "INFO");
                Logger.getLogger("").log(Level.INFO, "Logger configuration: create layer.xml file object {0} at {1}", new Object[]{loggerFile.toURI(), FileUtil.toFile(loggerFile)});
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create 'Logger' file in system filesystem.", e);
            }
        } else {
            Logger.getLogger("").log(Level.INFO, "Logger configuration: load layer.xml file object {0} at {1}", new Object[]{loggerFile.toURI(), FileUtil.toFile(loggerFile)});
            readLoggerLevelFromFileObject(loggerFile);
        }
    }

    /**
     * Load logger configuration from property file within user configuration
     * directory.
     */
    static void readUserConfigurationPropertiesFile() {

        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerPropertiesFile = configRoot.getFileObject(PROPERTIES_FILE_NAME);

        if (loggerPropertiesFile == null) {
            try {
                loggerPropertiesFile = configRoot.createData(PROPERTIES_FILE_NAME);
                Logger.getLogger("").log(Level.INFO, "Logger configuration: create logger.properties file {0} at {1}", new Object[]{loggerPropertiesFile.toURI(), FileUtil.toFile(loggerPropertiesFile)});
                OutputStream os = loggerPropertiesFile.getOutputStream();
                Properties properties = new Properties();
                properties.put(EXAMPLE_LOGGER_NAME, "INFO");
                properties.store(os, "Example logger configuration");
                os.close();
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create logger.properties file in configuration directory.", e);
            }
        } else {
            Logger.getLogger("").log(Level.INFO, "Logger configuration: load logger.properties file {0} at {1}", new Object[]{loggerPropertiesFile.toURI(), FileUtil.toFile(loggerPropertiesFile)});
            Properties properties = new Properties();
            try {
                InputStream is = loggerPropertiesFile.getInputStream();
                properties.load(is);
                is.close();
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to read logger.properties in configuration directory.", e);
            }
            readLoggerLevelFromProperties(properties);
        }
    }

    static void printLogManagerConfiguration() {
        final Enumeration<String> enumeration = LogManager.getLogManager().getLoggerNames();
        List<String> loggerNames = new ArrayList<>(Collections.list(enumeration));
        Collections.sort(loggerNames);
        for (String loggerName : loggerNames) {
            final Logger logger = LogManager.getLogManager().getLogger(loggerName);
            if (logger.getLevel() != null) {
                Logger.getLogger("").log(Level.INFO, "Logger configuration: {0}={1}", new Object[]{loggerName, logger.getLevel()});
            }
        }
    }
}
