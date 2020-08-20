package org.objectstyle.flow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Flow {

    private final StepProcessor<?> processor;
    private final Map<String, Flow> egresses;

    protected Flow(StepProcessor<?> processor, Map<String, Flow> egresses) {
        this.processor = Objects.requireNonNull(processor);
        this.egresses = Objects.requireNonNull(egresses);
    }

    public static Flow of(StepProcessor<?> processor) {
        return new Flow(processor, Collections.emptyMap());
    }

    public Flow out(String egress, Flow subFlow) {

        Map<String, Flow> egresses = new HashMap<>((int) ((this.egresses.size() + 2) / 0.75));
        egresses.putAll(this.egresses);
        egresses.put(egress, subFlow);

        return new Flow(processor, egresses);
    }

    public Flow out(String egress, StepProcessor<?> subProcessor) {
        return out(egress, Flow.of(subProcessor));
    }

    public StepProcessor<?> getProcessor() {
        return processor;
    }

    public Flow getEgress(String name) {
        Flow egress = egresses.get(name);
        if (egress == null) {
            throw new IllegalArgumentException("No such egress: " + name);
        }

        return egress;
    }
}
