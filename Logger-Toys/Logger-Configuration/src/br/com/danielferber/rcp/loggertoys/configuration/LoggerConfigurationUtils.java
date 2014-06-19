/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.loggertoys.configuration;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Daniel Felix Ferber
 */
final class LoggerConfigurationUtils {

    public static void readLoggerLevelFromFileObject(FileObject loggerFO) {
        final Enumeration<String> attributeEnum = loggerFO.getAttributes();
        while (attributeEnum.hasMoreElements()) {
            final String nome = attributeEnum.nextElement();
            final Object valor = loggerFO.getAttribute(nome);
            LoggerConfigurationUtils.setLoggerLevel(nome, (String) valor);
        }
    }

    static void readLoggerLevelFromProperties(Properties properties) {
        for (String name : properties.stringPropertyNames()) {
            final Object valor = properties.getProperty(name);
            LoggerConfigurationUtils.setLoggerLevel(name, valor);
        }
    }

    private LoggerConfigurationUtils() {
        // prevent instances 
    }

    static void resetLogManager() {
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            Logger.getLogger("").log(Level.WARNING, "Falha ao redefinir loggers.", e);
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Permiss\u00e3o recusada para reconfigurar loggers.", e);
        }
    }

    static void setLoggerLevel(final String loggerName, final Object value) {
        final Logger logger = Logger.getLogger(loggerName);
        final Level level;
        try {
            if (value instanceof String) {
                final String stringValue = (String) value;
                final String levelName = stringValue.toUpperCase();
                try {
                    level = Level.parse(levelName);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Logger level not listed in Level enum. name=" + loggerName + " stringvalue=" + levelName, e);
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

}
