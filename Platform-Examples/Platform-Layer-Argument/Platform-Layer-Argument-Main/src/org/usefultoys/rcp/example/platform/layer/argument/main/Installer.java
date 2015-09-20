/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.example.platform.layer.argument.main;

import java.io.IOException;
import java.util.Enumeration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        try {
            final FileObject configRoot = FileUtil.getConfigRoot();
            Enumeration<String> attributes = configRoot.getAttributes();
            FileObject file = configRoot;
            while (attributes.hasMoreElements()) {
                String nextElement = attributes.nextElement();
                System.out.println(nextElement + "=" + file.getAttribute(nextElement));
            }
            file = configRoot.getFileObject("folder/teste.txt");
            file.setAttribute("abc", "abcd");
            attributes = file.getAttributes();
            while (attributes.hasMoreElements()) {
                String nextElement = attributes.nextElement();
                System.out.println(nextElement + "=" + file.getAttribute(nextElement));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
