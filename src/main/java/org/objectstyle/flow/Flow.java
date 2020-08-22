package org.objectstyle.flow;

import org.objectstyle.flow.visitor.InsertVisitor;

import java.util.*;

/**
 * A root of the flow step tree. Represents a single step in the flow, connected to zero or more continuation steps.
 * Flow object is immutable. All customizations create new Flow objects. So each instance can be safely reused in
 * different configurations.
 */
public class Flow {

    private final StepProcessor<?> processor;
    private final Map<String, Flow> egresses;

    protected Flow(StepProcessor<?> processor, Map<String, Flow> egresses) {
        this.processor = Objects.requireNonNull(processor);
        this.egresses = Objects.requireNonNull(egresses);
    }

    /**
     * Creates a new single-step flow object.
     */
    public static Flow of(StepProcessor<?> processor) {
        return new Flow(processor, Collections.emptyMap());
    }

    /**
     * Creates a new flow, connecting the default egress of this flow with another flow.
     *
     * @param subFlow an egress flow called when an egress is requested
     * @return a copy of this Flow object connected to provided egress flow.
     */
    public Flow egress(Flow subFlow) {
        return doEgress(FlowPath.DEFAULT_EGRESS, subFlow);
    }

    /**
     * Creates a new flow, connecting a named egress of this flow with another flow.
     *
     * @param egressName the name of the egress for the sub flow.
     * @param subFlow    an egress flow called when an egress with name is requested
     * @return a copy of this Flow object connected to provided egress flow.
     */
    public Flow egress(String egressName, Flow subFlow) {
        FlowPath.validateSegmentName(egressName);
        return doEgress(egressName, subFlow);
    }

    protected Flow doEgress(String egressName, Flow subFlow) {

        Map<String, Flow> egresses = new LinkedHashMap<>((int) ((this.egresses.size() + 2) / 0.75));
        egresses.putAll(this.egresses);
        egresses.put(egressName, subFlow);

        return new Flow(processor, egresses);
    }

    public Flow egress(StepProcessor<?> subProcessor) {
        return egress(Flow.of(subProcessor));
    }

    public Flow egress(String egressName, StepProcessor<?> subProcessor) {
        return egress(egressName, Flow.of(subProcessor));
    }

    public Flow insert(String at, StepProcessor<?> processor) {
        InsertVisitor visitor = new InsertVisitor(FlowPath.parse(at), processor);
        accept(visitor);
        return visitor.getFlow();
    }

    public StepProcessor<?> getProcessor() {
        return processor;
    }

    public Flow getEgress(String egressName) {
        return egresses.get(egressName);
    }

    /**
     * Accepts a visitor passing it to this and each child flow node in a depth-first manner.
     *
     * @param visitor custom flow node visitor
     */
    public void accept(FlowVisitor visitor) {
        accept(visitor, FlowPath.root());
    }

    protected void accept(FlowVisitor visitor, FlowPath path) {
        if (visitor.onFlowNode(path, this)) {

            for (Map.Entry<String, Flow> e : egresses.entrySet()) {
                e.getValue().accept(visitor, path.subpath(e.getKey()));
            }
        }
    }
}
