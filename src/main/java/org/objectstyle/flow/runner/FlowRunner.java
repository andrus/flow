package org.objectstyle.flow.runner;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.StepContext;
import org.objectstyle.flow.StepProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlowRunner {

    private Flow flow;
    private Map<String, Object> startAttributes;

    protected FlowRunner(Flow flow) {
        this.flow = Objects.requireNonNull(flow);
        this.startAttributes = new HashMap<>();
    }

    public static FlowRunner of(Flow flow) {
        return new FlowRunner(flow);
    }

    public FlowRunner attribute(String name, Object value) {
        startAttributes.put(name, value);
        return this;
    }

    // TODO: return result object with metrics and attributes
    public <T> T run(Object input) {
        StepContext context = new StepContext(startAttributes);
        return (T) runOne(flow, input, context);
    }

    protected Object runOne(Flow flow, Object input, StepContext context) {
        StepProcessor processor = flow.getProcessor();
        processor.run(input, context);
        String egress = context.getEgress();
        return (egress != null)
                ? runOne(flow.getEgress(egress), context.getOutput(), context.cloneAttributes())
                : context.getOutput();
    }
}
