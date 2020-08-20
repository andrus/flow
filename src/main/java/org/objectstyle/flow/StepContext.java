package org.objectstyle.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class StepContext {

    private Object output;
    private String egress;
    private Map<String, Object> attributes;

    public StepContext(Map<String, Object> attributes) {
        // clone the map, as the context is mutable
        this.attributes = new HashMap<>(attributes);
    }

    public StepContext cloneAttributes() {
        return new StepContext(attributes);
    }

    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public <T> T getAttribute(String name, T defaultValue) {
        return (T) attributes.getOrDefault(name, defaultValue);
    }

    public StepContext setAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public StepContext setAttributeIfAbsent(String name, Function<String, ?> mappingFunction) {
        attributes.computeIfAbsent(name, mappingFunction);
        return this;
    }

    public StepContext setOutput(Object output) {
        this.output = output;
        return this;
    }

    public Object getOutput() {
        return output;
    }

    public StepContext setEgress(String egress) {
        this.egress = egress;
        return this;
    }

    public String getEgress() {
        return egress;
    }
}
