package org.objectstyle.flow.runner;

import org.objectstyle.flow.StepContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultStepContext implements StepContext {

    private Object output;
    private String egressName;
    private Map<String, Object> attributes;

    public DefaultStepContext(Map<String, Object> attributes) {
        // clone the map, as the context is mutable
        this.attributes = new HashMap<>(attributes);
    }

    public DefaultStepContext newWithClonedAttributes() {
        return new DefaultStepContext(attributes);
    }

    @Override
    public <T> T getAttribute(String name, T defaultValue) {
        return (T) attributes.getOrDefault(name, defaultValue);
    }

    @Override
    public DefaultStepContext setAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    @Override
    public DefaultStepContext setAttributeIfAbsent(String name, Function<String, ?> mappingFunction) {
        attributes.computeIfAbsent(name, mappingFunction);
        return this;
    }

    @Override
    public StepContext proceed(Object output, String egressName) {
        this.egressName = egressName;
        this.output = output;
        return this;
    }

    public Object getOutput() {
        return output;
    }

    public String getEgressName() {
        return egressName;
    }
}
