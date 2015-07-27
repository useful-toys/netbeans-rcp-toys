/*
 */
package org.usefultoys.rcp.logger.configuration;

import java.io.IOException;
import java.io.InputStream;
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

    static void readLoggerLevelFromProperties(Properties properties) {
        for (String name : properties.stringPropertyNames()) {
            final Object valor = properties.getProperty(name);
            applyLoggerLevel(name, valor);
        }
    }

    static void readLoggerLevelFromFileObject(FileObject loggerFO) {
        final Enumeration<String> attributeEnum = loggerFO.getAttributes();
        while (attributeEnum.hasMoreElements()) {
            final String nome = attributeEnum.nextElement();
            final Object valor = loggerFO.getAttribute(nome);
            applyLoggerLevel(nome, (String) valor);
        }
    }

    static void resetHandlerLevel() {
        Logger rootLogger = Logger.getLogger("");
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

        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerFile = configRoot.getFileObject("Logger");
        try {
            if (loggerFile == null) {
                loggerFile = configRoot.createData("Logger");
            }
        } catch (IOException e) {
            Logger.getLogger("").log(Level.WARNING, "Failed to create 'Logger' file in system filesystem.", e);
        }

        FileObject loggerPropertiesFile = configRoot.getFileObject("logger.properties");
        try {
            if (loggerPropertiesFile == null) {
                loggerPropertiesFile = configRoot.createData("logger", "properties");
            }
        } catch (IOException e) {
            Logger.getLogger("").log(Level.WARNING, "Failed to create logger.properties file in configuration directory.", e);
        }

        if (loggerFile != null) {
            readLoggerLevelFromFileObject(loggerFile);
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

        resetLogManager();
    }

}
