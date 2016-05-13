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
import org.usefultoys.netbeansrcp.platform.reporter.ui.NodeFactory.Node;
import static org.usefultoys.netbeansrcp.platform.reporter.ui.NodeFactory.ROOT;

/**
 *
 * @author x7ws
 */
public class ReportTreeTableModel extends AbstractTreeTableModel {

    private final NodeFactory factory;

    static protected final String[] columnNames = {"", "Status", "Operation", "Where"};
    static protected final Class<?>[] columnTypes = {TreeTableModel.class, String.class, String.class, String.class};

    public ReportTreeTableModel(NodeFactory factory) {
        super(NodeFactory.ROOT);
        this.factory = factory;
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
        if (node == "root") {
            return null;
        } else if (node instanceof Node) {
            switch (column) {
                case 0:
                    return ((Node) node).getIcon();
                case 1:
                    return ((Node) node).getStatusText();
                case 2:
                    return ((Node) node).getOperationText();
                case 3:
                    return ((Node) node).getWhereText();
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
        if (parent == ROOT) {
            return rootNodes.get(index);
        }
        return ((Node) parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == ROOT) {
            return rootNodes.size();
        }
        Node parentNode = (Node) parent;
        if (parentNode.hasChildren()) {
            return parentNode.getChildenCount();
        }
        return 0;
    }

    List<Node> rootNodes = new ArrayList<>();
    private final Map<String, NodeFactory.Node> hashToNode = new HashMap<String, NodeFactory.Node>();

    private static int[] nonThreadSafeIntArraySingleton = new int[1];
    private static Object[] nonThreadSafeNodeArraySingleton = new Object[1];

    public Node adicionarOuAtualizar(Report report) {
        final Node currentNode = hashToNode.get(report.getHash());
        if (currentNode != null) {
            /* There is a node, update it. */
            factory.update(currentNode, report);
            Node parentNode = hashToNode.get(report.getParentHash());
            if (parentNode != null) {
                /* The node has a parent node. */
                nonThreadSafeIntArraySingleton[0] = parentNode.getIndex(currentNode);
                nonThreadSafeNodeArraySingleton[0] = parentNode;
                fireTreeNodesChanged(this, parentNode.getTreePath().getPath(), nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
            } else {
                /* The node is a root node. */
                nonThreadSafeIntArraySingleton[0] = rootNodes.indexOf(currentNode);
                nonThreadSafeNodeArraySingleton[0] = NodeFactory.ROOT;
                fireTreeNodesChanged(this, NodeFactory.ROOT_PATH.getPath(), nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
            }
            return currentNode;
        }

        if (report.getParentHash() == null) {
            /* First level nodes are always added. */
            Node newNode = factory.createRootNode();
            factory.update(newNode, report);
            hashToNode.put(report.getHash(), newNode);
            rootNodes.add(newNode);
            nonThreadSafeIntArraySingleton[0] = rootNodes.size()-1;
            nonThreadSafeNodeArraySingleton[0] = newNode;
            fireTreeNodesInserted(this, NodeFactory.ROOT_PATH.getPath(), nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
            return newNode;
        }

        final Node parentNode = hashToNode.get(report.getParentHash());
        if (parentNode == null) {
            /* If the parent node was not added, then there is no need to add child node. */
            return null;
        }
        Node newNode = factory.createChildNode(parentNode);
        factory.update(newNode, report);
        hashToNode.put(report.getHash(), newNode);
        nonThreadSafeIntArraySingleton[0] = parentNode.getChildenCount()-1;
        nonThreadSafeNodeArraySingleton[0] = parentNode;
        fireTreeNodesInserted(this, parentNode.getTreePath().getPath(), nonThreadSafeIntArraySingleton, nonThreadSafeNodeArraySingleton);
        return newNode;
    }
}
