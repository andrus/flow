package org.objectstyle.flow.runner;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.StepProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlowRunner {

    private final Flow flow;
    private final Map<String, Object> startProperties;

    protected FlowRunner(Flow flow) {
        this.flow = Objects.requireNonNull(flow);
        this.startProperties = new HashMap<>();
    }

    public static FlowRunner of(Flow flow) {
        return new FlowRunner(flow);
    }

    public FlowRunner property(String name, Object value) {
        startProperties.put(name, value);
        return this;
    }

    // TODO: return result object with metrics and attributes
    public <T> T run(Object input) {
        return (T) run(flow, new DefaultStepContext<>(input, startProperties));
    }

    protected <T> Object run(Flow flow, DefaultStepContext<T> context) {
        StepProcessor processor = flow.getProcessor();
        processor.run(context);
        String egress = context.getEgressName();

        // TODO: StepProcessor should define a set of supported egress names (including the default), so that we can
        //  tell the difference between a wrong name and no routing
        Flow nextStep = flow.getEgress(egress);
        return (nextStep != null)
                ? run(nextStep, context.forNextStep())
                : context.getOutput();
    }
}
