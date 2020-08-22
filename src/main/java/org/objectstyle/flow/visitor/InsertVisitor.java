package org.objectstyle.flow.visitor;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.FlowVisitor;
import org.objectstyle.flow.StepProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertVisitor implements FlowVisitor {

    private final Flow flow;
    private final FlowPath insertAt;
    private final StepProcessor<?> toInsert;

    // contains nodes along "insertAt" path
    private final List<FlowNode> stack;

    public InsertVisitor(Flow flow, FlowPath insertAt, StepProcessor<?> toInsert) {
        this.flow = flow;
        this.insertAt = insertAt;
        this.toInsert = toInsert;
        this.stack = new ArrayList<>(insertAt.length());
    }

    public Flow insert() {

        // if inserting at root, skip the walk, as the insert is trivial
        if (insertAt.isRoot()) {
            return Flow.of(toInsert).egress(flow);
        }

        // process the tree, detect insertion point
        flow.accept(this);

        // walk back the stack, creating any needed extra connections
        FlowNode last = null;
        for (int i = stack.size() - 1; i >= 0; i--) {

            FlowNode current = stack.get(i);
            if (last != null) {
                current.addEgress(last.path, last.resolve());
            }

            last = current;
        }

        return last.resolve();
    }

    @Override
    public boolean onFlowNode(FlowPath path, Flow node) {

        if (!insertAt.startsWith(path)) {
            // 1. unrelated branch - can stop traversal in that direction
            return false;
        }

        // 2. haven't reached the insertion point yet, continue with this branch
        int stepsLeft = insertAt.length() - path.length();
        if (stepsLeft > 0) {
            stack.add(new FlowNode(path, node));

            // 2.1 if the node is a leaf node, and we are one component short of insertion point, append it here
            if (node.isLeaf() && stepsLeft == 1) {
                peekStack().addEgress(insertAt, Flow.of(toInsert));
                return false;
            }

            return true;
        }

        // 3. found the insertion point - do insert and get out of this branch
        peekStack().addEgress(path, Flow.of(toInsert).egress(node));
        return false;
    }

    private FlowNode peekStack() {
        return stack.get(stack.size() - 1);
    }

    static class FlowNode {
        private final Flow flow;
        private final FlowPath path;
        private Map<String, Flow> egresses;

        FlowNode(FlowPath path, Flow flow) {
            this.flow = flow;
            this.path = path;
        }

        void addEgress(FlowPath path, Flow flow) {
            if (egresses == null) {
                egresses = new HashMap<>();
            }

            egresses.put(path.getLastSegmentName(), flow);
        }

        Flow resolve() {
            return egresses != null ? flow.egresses(egresses) : flow;
        }
    }
}
