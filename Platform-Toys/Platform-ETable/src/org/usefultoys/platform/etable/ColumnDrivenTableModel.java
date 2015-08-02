/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.etable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Daniel Felix Ferber
 */
public class ColumnDrivenTableModel<RowType> extends AbstractTableModel {
    
    protected final List<String> rowIds = new ArrayList<String>();
    protected final List<RowType> rowObjects = new ArrayList<RowType>();
    protected final List<? extends CellModel> cellModels;
    protected final Map<String, RowType> idToTowObject = new HashMap<String, RowType>();
    
    public ColumnDrivenTableModel(List<? extends CellModel> cellModels) {
        this.cellModels = cellModels;
    }
    
    public ColumnDrivenTableModel(List<? extends CellModel> cellModels, Map<String, RowType> idToTowObject) {
        this(cellModels);
        this.reload(idToTowObject);
    }
    
    @Override
    public int getRowCount() {
        return rowObjects.size();
    }
    
    @Override
    public int getColumnCount() {
        return cellModels.size();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return cellModels.get(columnIndex).getValueAt(rowObjects.get(rowIndex));
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return cellModels.get(columnIndex).getColumnName();
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return cellModels.get(columnIndex).getColumnClass();
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        cellModels.get(columnIndex).setValueAt(rowObjects.get(rowIndex), aValue);
        return;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return cellModels.get(columnIndex).isCellEditable(rowObjects.get(rowIndex));
    }
    
    public RowType getRowObject(int rowIndex) {
        return rowObjects.get(rowIndex);
    }
    
    public String getRowId(int rowIndex) {
        return rowIds.get(rowIndex);
    }
    
    public RowType getRowObject(String id) {
        return idToTowObject.get(id);
    }
    
    protected void notifyAddOrChange(String id, RowType rowObject) {
        RowType added = this.idToTowObject.put(id, rowObject);
        if (added == null) {
            this.rowObjects.add(rowObject);
            this.rowIds.add(id);
            this.fireTableRowsInserted(rowIds.size(), rowIds.size());
        } else {
            int index = this.rowIds.indexOf(id);
            this.rowObjects.set(index, rowObject);
            this.fireTableRowsUpdated(index, index);
        }
    }
    
    protected void notifyAddOrChange(Map<String, RowType> rowObjects) {
        for (String key : rowObjects.keySet()) {
            RowType value = rowObjects.get(key);
            notifyAddOrChange(key, value);
        }
    }
    
    protected void notifyRemove(String id) {
        /* Remove elemento do hashmap. */
        final RowType removed = this.idToTowObject.remove(id);
        if (removed != null) {
            /* Só atualiza a tabela se o elemento realmente existia. */
            final int index = this.rowIds.indexOf(id);
            this.rowIds.remove(index);
            this.rowObjects.remove(index);
            this.fireTableRowsDeleted(index, index);
        }
    }
    
    protected void notifyRemove(Map<String, RowType> rowObjects) {
        for (String key : rowObjects.keySet()) {
            notifyRemove(key);
        }
    }
    
    public void reset(Map<String, RowType> idToTowObject) {
        reload(idToTowObject);
        this.fireTableDataChanged();
    }
    
    public void reload(Map<String, RowType> map) {
        this.rowIds.clear();
        this.rowObjects.clear();
        this.idToTowObject.clear();
        this.idToTowObject.putAll(map);
        for (Map.Entry<String, RowType> entry : map.entrySet()) {
            String string = entry.getKey();
            RowType rowtype = entry.getValue();
            rowIds.add(string);
            rowObjects.add(rowtype);
        }
        fireTableDataChanged();
    }
}
