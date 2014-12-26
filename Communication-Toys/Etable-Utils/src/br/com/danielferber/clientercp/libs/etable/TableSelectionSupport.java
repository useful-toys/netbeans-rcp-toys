/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.clientercp.libs.etable;

import br.com.danielferber.rcp.communicationtoys.cookies.api.CookieService;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Daniel Felix Ferber
 * @param <RowType>
 */
public class TableSelectionSupport<RowType extends Object> {

    private final JTable table;
    private ListSelectionListener listSelectionListener;

    public TableSelectionSupport(JTable table) {
        this.table = table;
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

    private ListSelectionListener createListener() {
        return new SelectionListener();
    }

    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int[] rowIndex = table.getSelectedRows();
            if (rowIndex.length == 0) {
                CookieService.Lookup.getDefault().clearSelection();
                return;
            }
            final ColumnDrivenTableModel<RowType> tableModel = (ColumnDrivenTableModel<RowType>) table.getModel();

            Map<String, RowType> selection = new HashMap<>();
            for (int indice : rowIndex) {
                final String rowId = tableModel.getRowId(indice);
                final RowType rowObject = tableModel.getRowObject(indice);
                selection.put(rowId, rowObject);
            }
            CookieService.Lookup.getDefault().setSelectionObjects(selection);
        }
    }
}
