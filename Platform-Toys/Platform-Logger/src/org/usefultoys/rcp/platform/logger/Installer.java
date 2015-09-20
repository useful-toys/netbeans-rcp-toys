/*
 */
package org.usefultoys.rcp.platform.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;

@OnStart
public final class Installer implements Runnable {

    static final String PROPERTIES_FILE_NAME = "logger.properties";
    static final String FILE_OBJECT_NAME = "Logger";

    static void readLoggerLevelFromProperties(Properties properties) {
        for (String name : properties.stringPropertyNames()) {
            final Object levelStr = properties.getProperty(name);
            if (name.endsWith(".level")) {
                final String loggerName = name.substring(0, name.length() - 6);
                applyLoggerLevel(loggerName, levelStr);
            } else {
                applyLoggerLevel(name, levelStr);
            }
        }
    }

    static void readLoggerLevelFromFileObject(FileObject loggerFO) {
        final Enumeration<String> attributeEnum = loggerFO.getAttributes();
        while (attributeEnum.hasMoreElements()) {
            final String loggerName = attributeEnum.nextElement();
            final Object levelStr = loggerFO.getAttribute(loggerName);
            applyLoggerLevel(loggerName, (String) levelStr);
        }
    }

    static void resetHandlerLevel() {
        final Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            try {
                handler.setLevel(Level.ALL);
            } catch (SecurityException e) {
                Logger.getLogger("").log(Level.WARNING, "Not allowed to change handler level. name=" + handler.getClass().getSimpleName() + " stringvalue=", e);
            }
        }
    }

    static void resetLogManager() {
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            Logger.getLogger("").log(Level.WARNING, "Failed to change logger configuration.", e);
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Not allowed to change logger configuration.", e);
        }
    }

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
            System.setProperty(loggerName + ".level", level.getName());
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Not allowed to change logger level. name=" + loggerName + " level=" + level.getName(), e);
        }
    }

    @Override
    public void run() {
        resetHandlerLevel();

        readLayerFileObject();
        readUserConfigurationPropertiesFile();

        resetLogManager();
    }

    static void readLayerFileObject() {
        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerFile = configRoot.getFileObject(FILE_OBJECT_NAME);
        if (loggerFile == null) {
            try {
                loggerFile = configRoot.createData(FILE_OBJECT_NAME);
                loggerFile.setAttribute(getExampleLoggerName(), "INFO");
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create 'Logger' file in system filesystem.", e);
            }
        }
        if (loggerFile != null) {
            readLoggerLevelFromFileObject(loggerFile);
        }
    }

    private static String getExampleLoggerName() {
        return Installer.class.getPackage().getName() + ".example";
    }

    static void readUserConfigurationPropertiesFile() {
        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerPropertiesFile = configRoot.getFileObject(PROPERTIES_FILE_NAME);
        if (loggerPropertiesFile == null) {
            try {
                loggerPropertiesFile = configRoot.createData(PROPERTIES_FILE_NAME);
                OutputStream os = loggerPropertiesFile.getOutputStream();
                Properties properties = new Properties();
                properties.put(getExampleLoggerName(), "INFO");
                properties.store(os, "Example logger configuration");
                os.close();
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create logger.properties file in configuration directory.", e);
            }
        }
        if (loggerPropertiesFile != null) {
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

}
