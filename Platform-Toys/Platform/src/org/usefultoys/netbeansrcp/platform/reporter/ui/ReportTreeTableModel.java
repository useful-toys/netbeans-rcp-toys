/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.usefultoys.netbeansrcp.platform.reporter.Report;

/**
 *
 * @author x7ws
 */
public class ReportTreeTableModel extends AbstractTreeTableModel {

    static protected final String[] columnNames = {"", "Operação"};
    static protected final Class<?>[] columnTypes = {TreeTableModel.class, String.class};

    private static class Node {

        private boolean ok;
        private String title;
        private final Node[] path;
        private List<Node> children;

        Node() {
            /* Constructor for ROOT node. */
            this.title = "root";
            this.children = new ArrayList<>();
            this.ok = false;
            this.path = new Node[]{this};
        }

        Node(Node parent, Report r) {
            this.title = r.getTitle();
            this.ok = r.isOK();
            this.path = new Node[parent.path.length + 1];
            System.arraycopy(parent.path, 0, this.path, 0, parent.path.length);
            this.path[parent.path.length] = parent;
        }

        void update(Report r) {
            this.title = r.getTitle();
            this.ok = r.isOK();
        }
    }

    private final static Node ROOT = new Node();
    private final Map<String, Node> hashToNode = new HashMap<String, Node>();

    public ReportTreeTableModel() {
        super(ROOT);
    }

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
        if (node == ROOT) {
            return "root";
        } else if (node instanceof Report) {
            switch (column) {
                case 0:
                    return ((Node) node);
                case 1:
                    return ((Node) node).title;
                default:
                    return null;
            }
        } else {
            return "not a report";
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
        return ((Node) parent).children.get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Node parentNode = (Node) parent;
        if (parentNode.children == null || parentNode.children.isEmpty()) {
            return 0;
        } else {
            return parentNode.children.size();
        }
    }

    private static int[] nonThreadSafeIntArraySingleton = new int[1];
    private static Node[] nonThreadSafeNodeArraySingleton = new Node[1];

    void adicionarOuAtualizar(Report report) {
        final Node currentNode = hashToNode.get(report.getHash());
        if (currentNode != null) {
            /* There is a node, update it. */
            Node parentNode = hashToNode.get(report.getParentHash());
            if (parentNode == null) parentNode = ROOT;
            currentNode.update(report);
            nonThreadSafeIntArraySingleton[0] = parentNode.children.size();
            nonThreadSafeNodeArraySingleton[0] = currentNode;
            fireTreeNodesChanged(this, parentNode.path, nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
            return;
        }

        if (report.getParentHash() == null) {
            /* First level nodes are always added. */
            Node newNode = new Node(ROOT, report);
            hashToNode.put(report.getHash(), newNode);
            nonThreadSafeIntArraySingleton[0] = ROOT.children.size();
            nonThreadSafeNodeArraySingleton[0] = newNode;
            ROOT.children.add(newNode);
            fireTreeNodesInserted(this, ROOT.path, nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
            return;
        }
        if (currentNode == null) {
            final Node parentNode = hashToNode.get(report.getParentHash());
            if (parentNode == null) {
                /* If the parent node was not added, then there is no need to add child node. */
                return;
            }
            Node newNode = new Node(parentNode, report);
            hashToNode.put(report.getHash(), newNode);
            if (parentNode.children == null) {
                parentNode.children = new ArrayList<Node>();
            }
            nonThreadSafeIntArraySingleton[0] = parentNode.children.size();
            nonThreadSafeNodeArraySingleton[0] = newNode;
            parentNode.children.add(newNode);
            fireTreeNodesInserted(this, parentNode.path, nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
        } else {
        }
    }
}
