/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.etable;

import java.util.HashMap;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.swing.etable.ETable;
import org.usefultoys.platform.cookies.api.TopComponentCookieContext;

/**
 *
 * @author Daniel Felix Ferber
 * @param <RowType>
 */
public class TableSelectionSupport<RowType extends Object> {

    private final ETable table;
    private final TopComponentCookieContext context;
    private ListSelectionListener listSelectionListener;

    public TableSelectionSupport(ETable table, TopComponentCookieContext context) {
        this.table = table;
        this.context = context;
    }

    public synchronized TableSelectionSupport ativar() {
        if (this.listSelectionListener == null) {
            listSelectionListener = createListener();
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
        }
        return this;
    }

    public synchronized TableSelectionSupport desativar() {
        if (this.listSelectionListener != null) {
            table.getSelectionModel().removeListSelectionListener(listSelectionListener);
            listSelectionListener = null;
        }
        return this;
    }

    public void atualizar() {
        final Map<String, RowType> selection = calculateSelection();
        if (selection == null) {
            context.clearSelection();
            return;
        }
        context.setSelection(selection, null).apply();
    }

    private Map<String, RowType> calculateSelection() {
        final int[] rowIndex = table.getSelectedRows();
        if (rowIndex.length == 0) {
            return null;
        }
        final ColumnDrivenTableModel<RowType> tableModel = (ColumnDrivenTableModel<RowType>) table.getModel();
        Map<String, RowType> selection = new HashMap<>();
        for (int indice : rowIndex) {
            indice = table.convertRowIndexToModel(indice);
            final String rowId = tableModel.getRowId(indice);
            final RowType rowObject = tableModel.getRowObject(indice);
            selection.put(rowId, rowObject);
        }
        return selection;
    }

    private ListSelectionListener createListener() {
        return new SelectionListener();
    }

    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            atualizar();
        }
    }
}
