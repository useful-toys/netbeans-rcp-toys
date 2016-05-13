/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.TreePath;
import org.openide.util.ImageUtilities;
import org.usefultoys.netbeansrcp.platform.reporter.Report;
import static org.usefultoys.netbeansrcp.platform.reporter.ui.NodeFactory.ROOT_PATH;

public class NodeFactoryImpl implements NodeFactory {

    public static class NodeImpl implements Node {

        Icon icon;
        String statusText;
        String operationText;
        String whereText;
        List<Node> children;

        final TreePath path;

        NodeImpl() {
            this.path = ROOT_PATH.pathByAddingChild(this);
        }

        NodeImpl(Node parent) {
            this.path = parent.getTreePath().pathByAddingChild(this);
            NodeImpl parentImpl = (NodeImpl) parent;
            if (parentImpl.children == null) {
                parentImpl.children = new ArrayList<>();
            }
            parentImpl.children.add(this);
        }

        @Override
        public Icon getIcon() {
            return icon;
        }

        @Override
        public String getOperationText() {
            return operationText;
        }

        @Override
        public TreePath getTreePath() {
            return path;
        }

        @Override
        public String getStatusText() {
            return statusText;
        }

        @Override
        public String getWhereText() {
            return whereText;
        }

        @Override
        public Object getChild(int index) {
            return children.get(index);
        }

        @Override
        public boolean hasChildren() {
            return children != null && !children.isEmpty();
        }

        @Override
        public int getChildenCount() {
            return children.size();
        }

        @Override
        public int getIndex(Node childNode) {
            return children.indexOf(childNode);
        }
    }

    @Override
    public Node createRootNode() {
        return new NodeImpl();
    }

    @Override
    public Node createChildNode(Node parent) {
        return new NodeImpl(parent);
    }

    public static final String BASE = NodeFactoryImpl.class.getPackage().getName().replace('.', '/') + '/';
    public static final ImageIcon ICON_START = ImageUtilities.loadImageIcon(BASE + "report-start.png", true);
    public static final ImageIcon ICON_START_GROUP = ImageUtilities.loadImageIcon(BASE + "report-start-group.png", true);
    public static final ImageIcon ICON_OK = ImageUtilities.loadImageIcon(BASE + "report-ok.png", true);
    public static final ImageIcon ICON_OK_GROUP = ImageUtilities.loadImageIcon(BASE + "report-ok-group.png", true);
    public static final ImageIcon ICON_ALTERNATIVE = ImageUtilities.loadImageIcon(BASE + "report-alternative.png", true);
    public static final ImageIcon ICON_ALTERNATIVE_GROUP = ImageUtilities.loadImageIcon(BASE + "report-alternative-group.png", true);
    public static final ImageIcon ICON_REJECT = ImageUtilities.loadImageIcon(BASE + "report-reject.png", true);
    public static final ImageIcon ICON_REJECT_GROUP = ImageUtilities.loadImageIcon(BASE + "report-reject-group.png", true);
    public static final ImageIcon ICON_FAIL = ImageUtilities.loadImageIcon(BASE + "report-fail.png", true);
    public static final ImageIcon ICON_FAIL_GROUP = ImageUtilities.loadImageIcon(BASE + "report-fail-group.png", true);

    @Override
    public void update(Node node, Report r) {
        NodeImpl nodeImpl = (NodeImpl) node;
        if (r.isCancel()) {
            nodeImpl.icon = node.hasChildren() ? ICON_REJECT_GROUP : ICON_REJECT;
            nodeImpl.statusText = "Canceled";
        } else if (r.isOK()) {
            if (r.getPathId() == null) {
                nodeImpl.icon = node.hasChildren() ? ICON_OK_GROUP : ICON_OK;
                nodeImpl.statusText = "Succeded";
            } else {
                nodeImpl.icon = node.hasChildren() ? ICON_ALTERNATIVE_GROUP : ICON_ALTERNATIVE;
                nodeImpl.statusText = "Alternative";
            }
        } else if (r.isReject()) {
            nodeImpl.icon = node.hasChildren() ? ICON_REJECT_GROUP : ICON_REJECT;
            nodeImpl.statusText = "Rejected";
        } else if (r.isFail()) {
            nodeImpl.icon = node.hasChildren() ? ICON_FAIL_GROUP : ICON_FAIL;
            nodeImpl.statusText = "Failed";
        } else {
            nodeImpl.icon = node.hasChildren() ? ICON_START_GROUP : ICON_START;
            nodeImpl.statusText = "Running";
        }
        final String title = r.getTitle();
        nodeImpl.operationText = title != null ? title : r.getHash();
    }
}
