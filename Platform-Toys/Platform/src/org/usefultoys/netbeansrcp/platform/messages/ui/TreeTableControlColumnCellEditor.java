/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
 
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
 
public class TreeTableControlColumnCellEditor extends AbstractCellEditor implements TableCellEditor {
 
    private JTree tree;
    private JTable table;
 
    public TreeTableControlColumnCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
        return tree;
    }
 
    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            int colunm1 = 0;
            MouseEvent me = (MouseEvent) e;
            int doubleClick = 2;
            MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick, me.isPopupTrigger());
            tree.dispatchEvent(newME);
        }
        return false;
    }
 
    @Override
    public Object getCellEditorValue() {
        return null;
    }
 
}
