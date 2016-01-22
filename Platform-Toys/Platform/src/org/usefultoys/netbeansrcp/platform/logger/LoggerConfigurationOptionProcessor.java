package org.usefultoys.netbeansrcp.platform.logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.openide.util.lookup.ServiceProvider;

/*
 * Este arquivo pertence à Petrobras e não pode ser utilizado fora
 * desta empresa sem prévia autorização.
 */
/**
 *
 * @author x7ws
 */
@ServiceProvider(service = OptionProcessor.class)
public class LoggerConfigurationOptionProcessor extends OptionProcessor {

    private Option loggerConfigurationOption = Option.requiredArgument(Option.NO_SHORT_NAME, "loggerLevel");

    @Override
    protected Set<Option> getOptions() {
        return Collections.singleton(loggerConfigurationOption);
    }

    @Override
    protected void process(Env env, Map<Option, String[]> optionValues) throws CommandException {
        final String[] propertyFileUrls = optionValues.get(loggerConfigurationOption);
        for (String urlString : propertyFileUrls) {
            readPropertiesFileFromArgument(urlString);
        }
        if (propertyFileUrls.length > 0) {
            LoggerConfigurationHelper.resetLogManager();
            LoggerConfigurationHelper.printLogManagerConfiguration();
        }
    }

    private void readPropertiesFileFromArgument(String urlString) {
        LoggerConfigurationHelper.logInfo("Logger configuration: command line argument=value={0}", urlString);

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
                LoggerConfigurationHelper.logWarn("Logger configuration: Cannot read properties file given by command line: {0}.", urlString);
                return;
            }
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException ex) {
                // Should neve be thrown.
            }
        }

        if (url != null) {
            Properties properties = new Properties();
            try (InputStream is = url.openStream()) {
                properties.load(is);
                LoggerConfigurationHelper.logInfo("Logger configuration: Loaded properties file given by command line: {0}.", urlString);
            } catch (IOException e) {
                LoggerConfigurationHelper.logWarn("Logger configuration: Failed to load properties file given by command line: {0}.", urlString);
            }
            LoggerConfigurationHelper.readLoggerLevelFromProperties(properties);
        }
    }
}
