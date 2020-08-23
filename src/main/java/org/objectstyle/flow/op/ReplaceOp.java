package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.FlowVisitor;

public class ReplaceOp extends FlowRebuildOp implements FlowVisitor {

    private final Flow flow;
    private final FlowPath replaceAt;
    private final Flow replacement;

    private boolean pathMatched;
    private Flow result;

    public ReplaceOp(Flow flow, FlowPath replaceAt, Flow replacement) {
        super(replaceAt.length());
        this.flow = flow;
        this.replaceAt = replaceAt;
        this.replacement = replacement;
    }

    public Flow replace() {
        if (replaceAt.isRoot()) {
            return replacement;
        }

        flow.accept(this);

        if (!pathMatched) {
            throw new RuntimeException("Flow is incompatible with the replacement path: " + replaceAt);
        }

        return result;
    }

    @Override
    public boolean beforeNode(FlowPath path, Flow node) {
        if (!replaceAt.startsWith(path)) {
            // 1. unrelated branch - can stop traversal in that direction
            return false;
        }

        int stepsLeft = replaceAt.length() - path.length();

        // 2. haven't reached the replacement point yet, continue with this branch
        if (stepsLeft > 0) {

            // 2.1 if the node is a leaf node, and we are one component short of replacement point, append it here
            if (node.isLeaf() && stepsLeft == 1) {
                peek().addEgress(replaceAt, replacement);
                pathMatched = true;
                return false;
            }

            // "push" requires to return "true", so that "afterNode" is called, and stack push/pop is balanced
            push(path, node);
            return true;
        }

        // 3. found the replacement point - do replace and get out of this branch
        peek().addEgress(replaceAt, replacement);
        pathMatched = true;
        return false;
    }

    @Override
    public void afterNode(FlowPath path, Flow node) {
        FlowBuilderNode current = pop();

        if (isEmptyStack()) {
            this.result = current.resolve();
        } else {
            peek().addEgress(current);
        }
    }
}
