package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.FlowVisitor;

import java.util.Optional;

public class FindOp implements FlowVisitor {

    private final Flow flow;
    private final FlowPath toFind;
    private Flow result;

    public FindOp(Flow flow, FlowPath toFind) {
        this.flow = flow;
        this.toFind = toFind;
    }

    public Optional<Flow> find() {

        if (toFind.isRoot()) {
            return Optional.of(flow);
        }

        flow.accept(this);
        return Optional.ofNullable(result);
    }

    @Override
    public boolean beforeNode(FlowPath path, Flow node) {
        if (!toFind.startsWith(path)) {
            // 1. unrelated branch - can stop traversal in that direction
            return false;
        }

        // 2. haven't reached the end of the path yet, continue with this branch
        int stepsLeft = toFind.length() - path.length();
        if (stepsLeft > 0) {
            return true;
        }

        // 3. found the path - save result and get out of this branch
        this.result = node;
        return false;
    }
}
