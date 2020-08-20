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

        // note that named egress will be present, or an exception is thrown. While default egress may be null
        // TODO: StepProcessor should define a set of supported egress names (including the default), so that we can
        //  tell the difference between a wrong name and no routing
        Flow nextStep = egress != null ? flow.getNextStep(egress) : flow.getDefaultNextStep();

        return (nextStep != null)
                ? runOne(nextStep, context.getOutput(), context.cloneAttributes())
                : context.getOutput();
    }
}
