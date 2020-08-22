package org.objectstyle.flow;

/**
 * Allows to traverse the flow
 */
public interface FlowVisitor {

    /**
     * A visitor callback method invoked on every flow node.
     *
     * @return true if the visitor wants to to continue traversal of this node's children, false - if traversal
     * should stop at this branch and move on to peer nodes.
     */
    boolean onFlowNode(Flow node, FlowPath path);
}
