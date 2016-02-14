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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
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
public final class LoggerConfigurationOnStart implements Runnable {

    public static final String LAYER_PROPERTIES_FILE_NAME = "logger.properties";
    public static final String LAYER_FILE_OBJECT_NAME = "Logger";
    public static final String EXAMPLE_LOGGER_NAME = LoggerConfigurationOnStart.class.getPackage().getName() + ".example";
    public static final String LOGGER_URL_SYSTEM_PROPERTY1 = "loggerLevel";
    public static final String LOGGER_URL_SYSTEM_PROPERTY2 = "jnlp.loggerLevel";
    public static final String LOGGER_URL_SYSTEM_PROPERTY3 = "javaws.loggerLevel";

    @Override
    public void run() {
        LoggerConfigurationHelper.resetExistingHandlerLevel();

        readAttributesFromLayerFileObject();
        readPropertiesFileFromSystemProperty(LOGGER_URL_SYSTEM_PROPERTY1);
        readPropertiesFileFromSystemProperty(LOGGER_URL_SYSTEM_PROPERTY2);
        readPropertiesFileFromSystemProperty(LOGGER_URL_SYSTEM_PROPERTY3);
        readPropertiesFileFromConfigurationDirectory();

        LoggerConfigurationHelper.resetLogManager();

        LoggerConfigurationHelper.printLogManagerConfiguration();
    }

    /**
     * Load logger configuration from layer.xml file object.
     * Create xml if it does not yet exist.
     */
    static void readAttributesFromLayerFileObject() {
        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerFile = configRoot.getFileObject(LAYER_FILE_OBJECT_NAME);
        if (loggerFile == null) {
            try {
                loggerFile = configRoot.createData(LAYER_FILE_OBJECT_NAME);
                loggerFile.setAttribute(EXAMPLE_LOGGER_NAME, "INFO");
                LoggerConfigurationHelper.logInfo("Logger configuration: Created example object. object={0}, uri={1}", LoggerConfigurationOnStart.LAYER_FILE_OBJECT_NAME, loggerFile.toURI().toString());
            } catch (IOException e) {
                LoggerConfigurationHelper.logInfo("Logger configuration: Failed to create example object. object={0}, uri={1}", LoggerConfigurationOnStart.LAYER_FILE_OBJECT_NAME, loggerFile.toURI().toString());
            }
        } else {
            LoggerConfigurationHelper.readLoggerLevelFromFileObject(loggerFile);
            LoggerConfigurationHelper.logInfo("Logger configuration: Read object. object={0}, uri={1}", LoggerConfigurationOnStart.LAYER_FILE_OBJECT_NAME, loggerFile.toURI().toString());
        }
    }

    /**
     * Load logger configuration from property file within user configuration
     * directory.
     */
    static void readPropertiesFileFromConfigurationDirectory() {

        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerPropertiesFile = configRoot.getFileObject(LAYER_PROPERTIES_FILE_NAME);

        if (loggerPropertiesFile == null) {
            try {
                loggerPropertiesFile = configRoot.createData(LAYER_PROPERTIES_FILE_NAME);
                OutputStream os = loggerPropertiesFile.getOutputStream();
                Properties properties = new Properties();
                properties.put(EXAMPLE_LOGGER_NAME, "INFO");
                properties.store(os, "Example logger configuration");
                os.close();
                LoggerConfigurationHelper.logInfo("Logger configuration: Created example properties file. file={0}, uri={1}, file={2}", LoggerConfigurationOnStart.LAYER_PROPERTIES_FILE_NAME, loggerPropertiesFile.toURI().toString(), FileUtil.toFile(loggerPropertiesFile).toString());
            } catch (IOException e) {
                LoggerConfigurationHelper.logWarn("Logger configuration: Failed to create example properties file. file={0}, uri={1}, file={2}", LoggerConfigurationOnStart.LAYER_PROPERTIES_FILE_NAME, loggerPropertiesFile.toURI().toString(), FileUtil.toFile(loggerPropertiesFile).toString());
            }
        } else {
            Properties properties = new Properties();
            try (InputStream is = loggerPropertiesFile.getInputStream()) {
                properties.load(is);
                LoggerConfigurationHelper.logInfo("Logger configuration: Loaded properties file. file={0}, uri={1}, file={2}", LoggerConfigurationOnStart.LAYER_PROPERTIES_FILE_NAME, loggerPropertiesFile.toURI().toString(), FileUtil.toFile(loggerPropertiesFile).toString());
            } catch (IOException e) {
                LoggerConfigurationHelper.logWarn("Logger configuration: Failed to load properties file. file={0}, uri={1}, file={2}", LoggerConfigurationOnStart.LAYER_PROPERTIES_FILE_NAME, loggerPropertiesFile.toURI().toString());
            }
            LoggerConfigurationHelper.readLoggerLevelFromProperties(properties);
        }
    }

    /**
     * Load logger configuration from property file within user configuration
     * directory.
     */
    static void readPropertiesFileFromSystemProperty(String propertyName) {

        final String urlString = System.getProperty(propertyName);
        if (urlString == null) {
            LoggerConfigurationHelper.logInfo("Logger configuration: systemProperty={0}, value not set.", propertyName);
            return;
        }
        LoggerConfigurationHelper.logInfo("Logger configuration: systemProperty={0}, value={1}", propertyName, urlString);

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // ignora;
        }
        File file = null;
        if (url == null) {
            file = new File(urlString);
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                LoggerConfigurationHelper.logWarn("Logger configuration: Cannot read properties file given by {0}: {1}.", propertyName, urlString);
                return;
            }
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException ex) {
                // Should neve be thrown.
            }
        }

        if (url != null) {
            final Properties properties = new Properties();
            try (InputStream is = url.openStream()) {
                properties.load(is);
                LoggerConfigurationHelper.logInfo("Logger configuration: Loaded properties file given by {0}: {1}.", propertyName, urlString);
            } catch (IOException e) {
                LoggerConfigurationHelper.logWarn("Logger configuration: Failed to load properties file given by {0}: {1}.", propertyName, urlString);
            }
            LoggerConfigurationHelper.readLoggerLevelFromProperties(properties);
        }
    }
}
