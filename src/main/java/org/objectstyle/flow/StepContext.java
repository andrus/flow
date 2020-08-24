package org.objectstyle.flow;

import java.util.function.Function;

public interface StepContext<INPUT> {

    default <T> T getProperty(String name) {
        return getProperty(name, null);
    }

    <T> T getProperty(String name, T defaultValue);

    StepContext setProperty(String name, Object value);

    StepContext setPropertyIfAbsent(String name, Function<String, ?> mappingFunction);

    INPUT getInput();

    default StepContext proceed(Object output) {
        return proceed(output, FlowPath.DEFAULT_EGRESS);
    }

    StepContext proceed(Object output, String egressName);
}
