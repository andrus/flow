package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.FlowVisitor;

public class ReplaceOp implements FlowVisitor {

    private final Flow flow;
    private final FlowPath modifyAt;
    private final Flow replacement;

    private final FlowBuilderStack stack;
    private boolean pathMatched;
    private Flow result;

    public ReplaceOp(Flow flow, FlowPath modifyAt, Flow replacement) {
        this.flow = flow;
        this.modifyAt = modifyAt;
        this.replacement = replacement;
        this.stack = new FlowBuilderStack(modifyAt.length());
    }

    protected Flow leafReplacement() {
        return replacement;
    }

    protected Flow replacement(Flow node) {
        return replacement;
    }

    public Flow replace() {

        // if replacing at root, the operation is trivial and does not require a tree walk
        if (modifyAt.isRoot()) {
            return replacement(flow);
        }

        // process the tree, detect replacement point
        flow.accept(this);

        if (!pathMatched) {
            throw new RuntimeException("Flow is incompatible with the search path: " + modifyAt);
        }

        return result;
    }

    @Override
    public boolean beforeNode(FlowPath path, Flow node) {

        if (!modifyAt.startsWith(path)) {
            // 1. unrelated branch - can stop traversal in that direction
            return false;
        }

        int stepsLeft = modifyAt.length() - path.length();

        // 2. haven't reached the replacement point yet, continue with this branch
        if (stepsLeft > 0) {

            // 2.1 if the node is a leaf node, and we are one component short of replacement point, append it here
            if (node.isLeaf() && stepsLeft == 1) {
                stack.peek().addEgress(modifyAt, leafReplacement());
                pathMatched = true;
                return false;
            }

            // "push" requires to return "true", so that "afterNode" is called, and stack push/pop is balanced
            stack.push(path, node);
            return true;
        }

        // 3. found the replacement point - do insert and get out of this branch
        stack.peek().addEgress(modifyAt, replacement(node));
        pathMatched = true;
        return false;
    }


    @Override
    public void afterNode(FlowPath path, Flow node) {
        FlowBuilderNode current = stack.pop();

        if (stack.isEmpty()) {
            this.result = current.resolve();
        } else {
            stack.peek().addEgress(current);
        }
    }
}
