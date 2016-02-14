/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.notification;

import org.openide.util.*;
import org.openide.windows.TopComponent;

/**
 * Métodos para ajustar a aparência da aba do TopComponent conforme seu estado.
 *
 */
public class TopComponentTabUtil {

    private TopComponentTabUtil() {
        // não permite instâncias
    }

    public static void plain(TopComponent tc) {
        tc.setIcon(IconBundle.NOTIFICATION_PLAIN.getImage());
    }

    public static void info(TopComponent tc) {
        tc.setIcon(IconBundle.NOTIFICATION_INFO.getImage());
    }

    public static void error(TopComponent tc) {
        tc.setIcon(IconBundle.NOTIFICATION_ERROR.getImage());
    }

    public static void warn(TopComponent tc) {
        tc.setIcon(IconBundle.NOTIFICATION_WARN.getImage());
    }
}
