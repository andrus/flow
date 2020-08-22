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

    private final FlowPath insertAt;
    private final StepProcessor<?> toInsert;

    // contains nodes along "insertAt" path
    private final List<FlowNode> stack;

    public InsertVisitor(FlowPath insertAt, StepProcessor<?> toInsert) {
        this.insertAt = insertAt;
        this.toInsert = toInsert;
        this.stack = new ArrayList<>(insertAt.length());
    }

    public Flow getFlow() {

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

        // 1. root node - it is on the change path, so record and continue
        if (path.isRoot()) {
            stack.add(new FlowNode(path, node));
            return true;
        }

        if (insertAt.startsWith(path)) {

            // 2. still on the way to find it, continue with this branch
            if (insertAt.length() != path.length()) {
                stack.add(new FlowNode(path, node));
                return true;
            }
            // 3. found it - do insert and get out of this branch
            else {
                peekStack().addEgress(path, Flow.of(toInsert).egress(node));
                return false;
            }
        }

        // 4. unrelated branch - can stop traversal in that direction
        return false;
    }

    private FlowNode peekStack() {
        return stack.get(stack.size() - 1);
    }

    static class FlowNode {
        private Flow flow;
        private FlowPath path;
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
