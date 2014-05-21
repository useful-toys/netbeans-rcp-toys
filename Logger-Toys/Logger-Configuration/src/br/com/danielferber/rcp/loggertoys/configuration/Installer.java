/*
 */
package br.com.danielferber.rcp.loggertoys.configuration;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.OnStart;

@OnStart
public final class Installer implements Runnable {
    FileObject rootFO;
    LoggerFileChangeListener fileSystemListener;
    
    @Override
    public void run() {
        rootFO = FileUtil.getConfigFile("/Logger/Level");
        if (rootFO == null) {
            return;
        }
        fileSystemListener = new LoggerFileChangeListener(rootFO);
        fileSystemListener.redefinirLoggers();
        rootFO.addFileChangeListener(fileSystemListener);
    }
}
