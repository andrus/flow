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
    private Map<String, Object> properties;

    public DefaultStepContext(INPUT input, Map<String, Object> properties) {
        this.input = input;

        // clone the map, as the context is mutable
        this.properties = new HashMap<>(properties);
        this.egressName = FlowPath.DEFAULT_EGRESS;
    }

    @Override
    public INPUT getInput() {
        return input;
    }

    public <T> DefaultStepContext<T> forNextStep() {
        return new DefaultStepContext<>((T) output, properties);
    }

    @Override
    public <T> T getProperty(String name, T defaultValue) {
        return (T) properties.getOrDefault(name, defaultValue);
    }

    @Override
    public DefaultStepContext<INPUT> setProperty(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public DefaultStepContext<INPUT> setPropertyIfAbsent(String name, Function<String, ?> mappingFunction) {
        properties.computeIfAbsent(name, mappingFunction);
        return this;
    }

    @Override
    public DefaultStepContext<INPUT> proceed(Object output, String egressName) {
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
