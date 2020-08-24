package org.objectstyle.flow.runner;

import org.objectstyle.flow.FlowPath;
import org.objectstyle.flow.StepContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DefaultStepContext<INPUT> implements StepContext<INPUT> {

    private INPUT input;
    private Object output;
    private String egressName;
    private Map<String, Object> attributes;

    public DefaultStepContext(INPUT input, Map<String, Object> attributes) {
        this.input = input;

        // clone the map, as the context is mutable
        this.attributes = new HashMap<>(attributes);
        this.egressName = FlowPath.DEFAULT_EGRESS;
    }

    @Override
    public INPUT getInput() {
        return input;
    }

    public <T> DefaultStepContext<T> forNextStep() {
        return new DefaultStepContext<>((T) output, attributes);
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
        this.egressName = Objects.requireNonNull(egressName);
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
