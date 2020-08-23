package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.StepProcessor;

public class InsertOp extends ReplaceOp {

    public InsertOp(Flow flow, FlowPath insertAt, StepProcessor<?> toInsert) {
        super(flow, insertAt, Flow.of(toInsert));
    }

    @Override
    protected Flow replacement(Flow node) {
        return super.replacement(node).egress(node);
    }
}
