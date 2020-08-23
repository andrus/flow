package org.objectstyle.flow.op;

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
    private Flow result;
    private boolean pathMatched;

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

        // 2. haven't reached the insertion point yet, continue with this branch
        int stepsLeft = insertAt.length() - path.length();
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
        FlowNode current = pop();

        if (stack.isEmpty()) {
            this.result = current.resolve();
        } else {
            peek().addEgress(current.path, current.resolve());
        }
    }

    private void push(FlowPath path, Flow node) {
        stack.add(new FlowNode(path, node));
    }

    private FlowNode pop() {
        return stack.remove(stack.size() - 1);
    }

    private FlowNode peek() {
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
