/*
 */
package br.com.danielferber.rcp.loggertoys.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;
import org.openide.util.Exceptions;

@OnStart
public final class Installer implements Runnable {

    private static LoggerFileChangeListener loggerAttributesFileListener;

    @Override
    public void run() {
        LoggerConfigurationUtils.resetHandlerLevel();

        final FileObject configRoot = FileUtil.getConfigRoot();
        FileObject loggerFolder = configRoot.getFileObject("Logger");
        FileObject loggerPropertiesFile = null;
        if (loggerFolder == null) {
            try {
                loggerFolder = configRoot.createFolder("Logger");
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create Logger configuration directory.", e);
            }
        }
        if (loggerFolder != null) {
            loggerPropertiesFile = loggerFolder.getFileObject("logger.properties");
            try {
                loggerFolder.setAttribute(Installer.class.getPackage().getName() + ".exampleLogger", "INFO");
            } catch (IOException ex) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create logger configuration example in Logger folder.", ex);
            }
        }
        if (loggerFolder != null && loggerPropertiesFile == null) {
            try {
                loggerPropertiesFile = loggerFolder.createData("logger", "properties");
                PrintStream os = new PrintStream(loggerPropertiesFile.getOutputStream());
                os.println(Installer.class.getPackage().getName() + ".exampleLogger=INFO");
                os.close();
            } catch (IOException e) {
                Logger.getLogger("").log(Level.WARNING, "Failed to create Logger configuration properties file.", e);
            }
        }

        if (loggerFolder != null) {
            loggerAttributesFileListener = new LoggerFileChangeListener();
            loggerFolder.addFileChangeListener(loggerAttributesFileListener);
            LoggerConfigurationUtils.readLoggerLevelFromFileObject(loggerFolder);
        }

        if (loggerPropertiesFile != null) {
            Properties properties = new Properties();
            try {
                InputStream is = loggerPropertiesFile.getInputStream();
                properties.load(is);
                is.close();
            } catch (IOException ex) {
                Logger.getLogger("").log(Level.WARNING, "Failed to read Logger configuration properties file.", ex);
            }
            LoggerConfigurationUtils.readLoggerLevelFromProperties(properties);
        }

        LoggerConfigurationUtils.resetLogManager();
    }

}
