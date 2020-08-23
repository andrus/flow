package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.FlowVisitor;
import org.objectstyle.flow.StepProcessor;

public class InsertOp extends FlowRebuildOp implements FlowVisitor {

    private final Flow flow;
    private final FlowPath insertAt;
    private final StepProcessor<?> toInsert;

    private boolean pathMatched;
    private Flow result;

    public InsertOp(Flow flow, FlowPath insertAt, StepProcessor<?> toInsert) {
        super(insertAt.length());
        this.flow = flow;
        this.insertAt = insertAt;
        this.toInsert = toInsert;
    }

    public Flow insert() {

        // if inserting at root, skip the walk, as the insert is trivial
        if (insertAt.isRoot()) {
            return Flow.of(toInsert).egress(flow);
        }

        // process the tree, detect insertion point
        flow.accept(this);

        if (!pathMatched) {
            throw new RuntimeException("Flow is incompatible with the insertion path: " + insertAt);
        }

        return result;
    }

    @Override
    public boolean beforeNode(FlowPath path, Flow node) {

        if (!insertAt.startsWith(path)) {
            // 1. unrelated branch - can stop traversal in that direction
            return false;
        }

        int stepsLeft = insertAt.length() - path.length();

        // 2. haven't reached the insertion point yet, continue with this branch
        if (stepsLeft > 0) {

            // 2.1 if the node is a leaf node, and we are one component short of insertion point, append it here
            if (node.isLeaf() && stepsLeft == 1) {
                peek().addEgress(insertAt, Flow.of(toInsert));
                pathMatched = true;
                return false;
            }

            // "push" requires to return "true", so that "afterNode" is called, and stack push/pop is balanced
            push(path, node);
            return true;
        }

        // 3. found the insertion point - do insert and get out of this branch
        peek().addEgress(path, Flow.of(toInsert).egress(node));
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
