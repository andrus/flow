package org.objectstyle.flow.runner;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.StepProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlowRunner {

    private final Flow flow;
    private final Map<String, Object> startAttributes;

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
        DefaultStepContext context = new DefaultStepContext(startAttributes);
        return (T) runOne(flow, input, context);
    }

    protected Object runOne(Flow flow, Object input, DefaultStepContext context) {
        StepProcessor processor = flow.getProcessor();
        processor.run(input, context);
        String egress = context.getEgressName();

        // TODO: StepProcessor should define a set of supported egress names (including the default), so that we can
        //  tell the difference between a wrong name and no routing
        Flow nextStep = flow.getEgress(egress);
        return (nextStep != null)
                ? runOne(nextStep, context.getOutput(), context.newWithClonedAttributes())
                : context.getOutput();
    }
}
