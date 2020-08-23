package org.objectstyle.flow;

/**
 * Allows to traverse the flow tree depth-first.
 *
 * @see Flow#accept(FlowVisitor)
 */
public interface FlowVisitor {

    /**
     * A visitor callback method invoked before each flow node.
     *
     * @param path path to the node from the traversal root
     * @param node visited node
     * @return true if the visitor wants to continue traversal of this node's children, false - if traversal
     * should stop at this branch and move on to peer nodes. Note that on "false" matching
     * {@link #afterNode(FlowPath, Flow)} will not be called.
     */
    boolean beforeNode(FlowPath path, Flow node);

    /**
     * Invoked after the node and all its children are processed. Not invoked if the matching
     * {@link #beforeNode(FlowPath, Flow)} returned false.
     *
     * @param path path to the node from the traversal root
     * @param node visited node
     */
    default void afterNode(FlowPath path, Flow node) {
    }
}
