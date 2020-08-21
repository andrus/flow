package org.objectstyle.flow;

import java.util.*;

/**
 * A root of the flow step tree. Represents a single step in the flow, connected to zero or more continuation steps.
 * Flow object is immutable. All customizations create new Flow objects. So each instance can be safely reused in
 * different configurations.
 */
public class Flow {

    private final StepProcessor<?> processor;
    private final Map<String, Flow> namedEgresses;
    private final Flow defaultEgress;

    protected Flow(StepProcessor<?> processor, Flow defaultEgress, Map<String, Flow> namedEgresses) {
        this.processor = Objects.requireNonNull(processor);
        this.defaultEgress = defaultEgress;
        this.namedEgresses = Objects.requireNonNull(namedEgresses);
    }

    /**
     * Creates a new single-step flow object.
     */
    public static Flow of(StepProcessor<?> processor) {
        return new Flow(processor, null, Collections.emptyMap());
    }

    /**
     * Specifies flow continuation that will be called after this step, if the step requested an unnamed egress.
     *
     * @param subFlow an egress flow called when an unnamed egress is requested
     * @return a copy of this Flow object connected to specified egress flow.
     */
    public Flow out(Flow subFlow) {
        return new Flow(processor, subFlow, namedEgresses);
    }

    /**
     * Specifies flow continuation that will be called after this step, if the step requested an egress with the
     * specified name.
     *
     * @param egress  the name of the egress for the sub flow.
     * @param subFlow an egress flow called when an egress with name is requested
     * @return a copy of this Flow object connected to specified named egress flow.
     */
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

    public Flow getDefaultEgress() {
        return defaultEgress;
    }

    public Flow getEgress(String egressName) {
        Flow egress = namedEgresses.get(egressName);
        if (egress == null) {
            throw new IllegalArgumentException("No such egress: " + egressName);
        }

        return egress;
    }
}
