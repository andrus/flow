package org.objectstyle.flow.visitor;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.FlowVisitor;
import org.objectstyle.flow.StepProcessor;

import java.util.ArrayList;
import java.util.List;

public class InsertVisitor implements FlowVisitor {

    private FlowPath insertAt;
    private StepProcessor<?> toInsert;

    private List<Flow> insertStack;

    public InsertVisitor(FlowPath insertAt, StepProcessor<?> toInsert) {
        this.insertAt = insertAt;
        this.toInsert = toInsert;

        this.insertStack = new ArrayList<>(insertAt.length());
    }

    public Flow getFlow() {
        return insertStack.get(0);
    }

    @Override
    public boolean onFlowNode(FlowPath path, Flow node) {

        // 1. root node
        if (path.isRoot()) {
            insertStack.add(node);
            return true;
        }

        if (insertAt.startsWith(path)) {

            // 2. we found it - do insert and get out of this branch
            if (insertAt.length() == path.length()) {
                Flow toInsertFlow = Flow.of(toInsert).egress(node);
                attachEgressToTheStackTop(path.getLastSegmentName(), toInsertFlow);
                return false;
            }
            // 3. we are still on the way to find it, continue with this branch
            else {
                attachEgressToTheStackTop(path.getLastSegmentName(), node);
                insertStack.add(node);
                return true;
            }
        }

        // 4. unrelated branch - can attach it unchanged and stop traversal in that direction
        attachEgressToTheStackTop(path.getLastSegmentName(), node);
        return false;
    }

    private void attachEgressToTheStackTop(String egressName, Flow node) {
        int top = insertStack.size() - 1;
        Flow parent = insertStack.get(top);
        Flow parentClone = parent.egress(egressName, node);
        insertStack.set(top, parentClone);
    }
}
