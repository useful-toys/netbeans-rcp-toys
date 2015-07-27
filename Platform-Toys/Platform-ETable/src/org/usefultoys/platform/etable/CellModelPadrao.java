/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.usefultoys.platform.etable;


public class CellModelPadrao<RowType, ColumnType> implements CellModel<RowType, ColumnType> {
    protected final Class<ColumnType> columnClass;
    protected final String columnName;
    protected final boolean editable;

    public CellModelPadrao(Class<ColumnType> columnClass, String columnName, boolean editable) {
        this.columnClass = columnClass;
        this.columnName = columnName;
        this.editable = editable;
    }
    
    @Override
    public Class<ColumnType> getColumnClass() {
        return columnClass;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
    
    @Override
    public void setValueAt(RowType rowObject, ColumnType value) {
        // ignore
    }

    @Override
    public ColumnType getValueAt(RowType rowObject) {
        return null;
    }

    @Override
    public boolean isCellEditable(RowType rowObject) {
        return editable;
    }    
}
