/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.ui;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.usefultoys.netbeansrcp.platform.messages.api.Report;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.usefultoys.netbeansrcp.platform.messages.ui//Report//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "ReportTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = ReportTopComponent.TOP_COMPONENT_ID)
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ReportAction",
        preferredID = "ReportTopComponent"
)
@Messages({
    "CTL_ReportAction=Report",
    "CTL_ReportTopComponent=Report Window",
    "HINT_ReportTopComponent=This is a Report window"
})
public final class ReportTopComponent extends TopComponent {

    public static final String TOP_COMPONENT_ID = "org.usefultoys.netbeansrcp.platform.messages.ui.ReportTopComponent";

    // Spalten Name.
    static protected final String[] columnNames = {"", "Operação"};

    // Spalten Typen.
    static protected final Class<?>[] columnTypes = {TreeTableModel.class, String.class};

    protected final List<Report> reportList = new ArrayList<>();

    public ReportTopComponent() {
        initComponents();
        setName(Bundle.CTL_ReportTopComponent());
        setToolTipText(Bundle.HINT_ReportTopComponent());

        TreeTableModel treeTableModel = new AbstractTreeTableModel(null) {

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return columnTypes[column];
            }

            @Override
            public Object getValueAt(Object node, int column) {
                if (node == null) {
                    return "null";
                } else {
                    switch (column) {
                        case 0:
                            return ((Report) node).isOK();
                        case 1:
                            return ((Report) node).getTitle();
                        default:
                            return null;
                    }
                }
            }

            @Override
            public boolean isCellEditable(Object node, int column) {
                return false;
            }

            @Override
            public void setValueAt(Object aValue, Object node, int column) {
                throw new UnsupportedOperationException("Not supported.");
            }

            @Override
            public Object getChild(Object parent, int index) {
                if (parent == null) {
                    return reportList.get(index);
                } else {
                    throw new UnsupportedOperationException("Not supported.");
                }
            }

            @Override
            public int getChildCount(Object parent) {
                if (parent == null) {
                    return reportList.size();
                } else {
                    return 0;
                }
            }
        };
        TreeTableCellRenderer tree = new TreeTableCellRenderer(table, treeTableModel);
        table.setModel(new TreeTableModelAdapter(treeTableModel, tree));
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        tree.setSelectionModel(selectionModel); //For the tree
        table.setSelectionModel(selectionModel.getListSelectionModel()); //For the table
        table.setDefaultRenderer(TreeTableModel.class, tree);
        table.setDefaultEditor(TreeTableModel.class, new TreeTableControlColumnCellEditor(tree, table));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    void start(Report report) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void progress(Report report) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void ok(Report report) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void reject(Report report) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void fail(Report report) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
