package org.objectstyle.flow;

/**
 * Allows to traverse the flow
 */
public interface FlowVisitor {

    void onFlowNode(Flow node, FlowPath path);
}
