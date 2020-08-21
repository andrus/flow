package org.objectstyle.flow;

import java.util.function.Function;

public interface StepContext {

    default <T> T getAttribute(String name) {
        return getAttribute(name, null);
    }

    <T> T getAttribute(String name, T defaultValue);

    StepContext setAttribute(String name, Object value);

    StepContext setAttributeIfAbsent(String name, Function<String, ?> mappingFunction);

    default StepContext proceed(Object output) {
        return proceed(output, null);
    }

    StepContext proceed(Object output, String egressName);
}
