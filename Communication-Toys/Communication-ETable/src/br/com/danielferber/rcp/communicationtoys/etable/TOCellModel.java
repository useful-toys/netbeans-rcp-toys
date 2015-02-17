/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.communicationtoys.etable;

import java.lang.reflect.Field;

public class TOCellModel<RowType, ColumnType> implements CellModel<RowType, ColumnType> {

    protected final String columnName;
    protected final boolean editable;
    protected final Field field;
    protected final Class<RowType> rowType;

    public TOCellModel(String columnName, Class<RowType> rowType, String attributeName, boolean editable) {
        this.columnName = columnName;
        this.editable = editable;
        this.rowType = rowType;
        try {
            this.field = rowType.getField(attributeName);

        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setValueAt(RowType rowObject, ColumnType value) {
        try {
            field.set(rowObject, value);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ColumnType getValueAt(RowType rowObject) {
        try {
            return (ColumnType) field.get(rowObject);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Class<ColumnType> getColumnClass() {
        return (Class<ColumnType>) field.getType();
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean isCellEditable(RowType rowObject) {
        return editable && rowType.isAssignableFrom(rowObject.getClass());
    }
}
