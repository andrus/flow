package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;

import java.util.ArrayList;
import java.util.List;

public class FlowBuilderStack {

    private final List<FlowBuilderNode> stack;

    protected FlowBuilderStack(int stackDepth) {
        this.stack = new ArrayList<>(stackDepth);
    }

    protected void push(FlowPath path, Flow node) {
        stack.add(new FlowBuilderNode(path, node));
    }

    protected FlowBuilderNode pop() {
        return stack.remove(stack.size() - 1);
    }

    protected FlowBuilderNode peek() {
        return stack.get(stack.size() - 1);
    }

    protected boolean isEmpty() {
        return stack.isEmpty();
    }
}
