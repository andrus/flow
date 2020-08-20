package org.objectstyle.flow;

import java.util.*;

public class Flow {

    private final StepProcessor<?> processor;
    private final Map<String, Flow> namedEgresses;
    private final Flow defaultEgress;

    protected Flow(StepProcessor<?> processor, Flow defaultEgress, Map<String, Flow> namedEgresses) {
        this.processor = Objects.requireNonNull(processor);
        this.defaultEgress = defaultEgress;
        this.namedEgresses = Objects.requireNonNull(namedEgresses);
    }

    public static Flow of(StepProcessor<?> processor) {
        return new Flow(processor, null, Collections.emptyMap());
    }

    public Flow out(Flow subFlow) {
        return new Flow(processor, subFlow, namedEgresses);
    }

    public Flow out(String egress, Flow subFlow) {

        Map<String, Flow> egresses = new HashMap<>((int) ((this.namedEgresses.size() + 2) / 0.75));
        egresses.putAll(this.namedEgresses);
        egresses.put(egress, subFlow);

        return new Flow(processor, defaultEgress, egresses);
    }

    public Flow out(StepProcessor<?> subProcessor) {
        return out(Flow.of(subProcessor));
    }

    public Flow out(String egress, StepProcessor<?> subProcessor) {
        return out(egress, Flow.of(subProcessor));
    }

    public StepProcessor<?> getProcessor() {
        return processor;
    }

    public Flow getDefaultNextStep() {
        return defaultEgress;
    }

    public Flow getNextStep(String egressName) {
        Flow egress = namedEgresses.get(egressName);
        if (egress == null) {
            throw new IllegalArgumentException("No such egress: " + egressName);
        }

        return egress;
    }
}
