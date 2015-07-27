/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.usefultoys.platform.etable;

import java.awt.Component;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.ActionPresenterProvider;
import org.openide.util.actions.Presenter;

/**
 * Popula um toolbar com actions gerenciadas pela API do Netbeans RCP.
 * Este código foi inspirado no código do próprio Netbeans RCP.
 * @author x8r7
 */
public final class ToolbarBuilder {

    public static void build(final JToolBar toolbar, final List<? extends Action> actions, final Lookup lookup) {
        for (Action action : actions) {
            if (action == null) {
                toolbar.addSeparator();
            } else {
                if (lookup != null && action instanceof ContextAwareAction) {
                    action = ((ContextAwareAction) action).createContextAwareInstance(lookup);
                }
                Component item;
                if (action instanceof Presenter.Toolbar) {
                    item = ((Presenter.Toolbar) action).getToolbarPresenter();
                    if (item == null) {
                        java.util.logging.Logger.getLogger(Utilities.class.getName()).log(Level.WARNING, "findContextMenuImpl, getPopupPresenter returning null for {0}", action);
                        continue;
                    }
                } else {
                    // We need to correctly handle mnemonics with '&' etc.
                    item = ActionPresenterProvider.getDefault().createToolbarPresenter(action);
                }
                toolbar.add(item);
            }
        }
//        toolbar.setPreferredSize(toolbar.getPreferredSize());
//        toolbar.validate();
    }
}
