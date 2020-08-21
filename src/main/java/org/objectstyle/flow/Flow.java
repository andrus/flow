package org.objectstyle.flow;

import java.util.*;

/**
 * A root of the flow step tree. Represents a single step in the flow, connected to zero or more continuation steps.
 * Flow object is immutable. All customizations create new Flow objects. So each instance can be safely reused in
 * different configurations.
 */
public class Flow {

    static final String DEFAULT_EGRESS = "_default_";

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
     * Specifies flow continuation that should be called after this processor is executed, when the processor didn't
     * specify an explicit egress.
     *
     * @param subFlow an egress flow called when an unnamed egress is requested
     * @return a copy of this Flow object connected to provided egress flow.
     */
    public Flow egress(Flow subFlow) {
        return new Flow(processor, subFlow, namedEgresses);
    }

    /**
     * Specifies flow continuation that will be called after this processor is executed, when the processor explicitly
     * requested an egress with this name.
     *
     * @param egressName the name of the egress for the sub flow.
     * @param subFlow    an egress flow called when an egress with name is requested
     * @return a copy of this Flow object connected to provided egress flow.
     */
    public Flow egress(String egressName, Flow subFlow) {

        Map<String, Flow> egresses = new HashMap<>((int) ((this.namedEgresses.size() + 2) / 0.75));
        egresses.putAll(this.namedEgresses);
        egresses.put(egressName, subFlow);

        return new Flow(processor, defaultEgress, egresses);
    }

    public Flow egress(StepProcessor<?> subProcessor) {
        return egress(Flow.of(subProcessor));
    }

    public Flow egress(String egressName, StepProcessor<?> subProcessor) {
        return egress(egressName, Flow.of(subProcessor));
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

    public void accept(FlowVisitor visitor) {
        accept(visitor, new LinkedList<>());
    }

    protected void accept(FlowVisitor visitor, LinkedList<String> path) {
        visitor.visitNode(this, path);

        if (defaultEgress != null) {
            path.addLast(DEFAULT_EGRESS);
            defaultEgress.accept(visitor, path);
            path.removeLast();
        }

        for (Map.Entry<String, Flow> e : namedEgresses.entrySet()) {
            path.addLast(e.getKey());
            e.getValue().accept(visitor, path);
            path.removeLast();
        }
    }
}
