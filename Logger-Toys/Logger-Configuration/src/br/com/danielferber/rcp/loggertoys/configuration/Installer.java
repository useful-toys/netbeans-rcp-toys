/*
 */
package br.com.danielferber.rcp.loggertoys.configuration;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;

@OnStart
public final class Installer implements Runnable {

    @Override
    public void run() {
        final FileObject rootFO = FileUtil.getConfigFile("/Logger/Level");
        if (rootFO == null) {
            return;
        }
        final LoggerFileChangeListener fileSystemListener = new LoggerFileChangeListener(rootFO);
        fileSystemListener.redefinirLoggers();
        rootFO.addFileChangeListener(fileSystemListener);
    }
}
