package org.objectstyle.flow.op;

import org.objectstyle.flow.Flow;
import org.objectstyle.flow.FlowPath;

import java.util.HashMap;
import java.util.Map;

public class FlowBuilderNode {

    private final Flow flow;
    private final FlowPath path;
    private Map<String, Flow> egresses;

    FlowBuilderNode(FlowPath path, Flow flow) {
        this.flow = flow;
        this.path = path;
    }

    void addEgress(FlowBuilderNode subflow) {
        addEgress(subflow.path, subflow.resolve());
    }

    void addEgress(FlowPath path, Flow flow) {
        if (egresses == null) {
            egresses = new HashMap<>();
        }

        egresses.put(path.getLastSegmentName(), flow);
    }

    Flow resolve() {
        return egresses != null ? flow.egresses(egresses) : flow;
    }
}
