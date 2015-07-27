/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.usefultoys.platform.etable;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface  CellModel<RowType, ColumnType> {

    Class<ColumnType> getColumnClass();
    String getColumnName();
    void setValueAt(RowType rowObject, ColumnType value);
    ColumnType getValueAt(RowType rowObject);
    boolean isCellEditable(RowType rowObject);
}
