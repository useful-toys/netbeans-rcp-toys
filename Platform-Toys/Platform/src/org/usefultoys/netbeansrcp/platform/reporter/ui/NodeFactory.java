/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.ui;

import java.util.List;
import javax.swing.Icon;
import javax.swing.tree.TreePath;
import org.usefultoys.netbeansrcp.platform.reporter.Report;

/**
 *
 * @author x7ws
 */
public interface NodeFactory {

    interface Node {

        Icon getIcon();

        String getStatusText();

        String getOperationText();

        String getWhereText();

        TreePath getTreePath();

        boolean hasChildren();

        Object getChild(int index);

        int getChildenCount();

        int getIndex(Node childNode);

    }

    Object ROOT = new Object();
    TreePath ROOT_PATH = new TreePath(new Object[]{ROOT});

    Node createRootNode();
    Node createChildNode(Node parent);
    void update(Node node, Report report);
}
