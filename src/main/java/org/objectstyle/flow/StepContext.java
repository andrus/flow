package org.objectstyle.flow;

import java.util.function.Function;

public interface StepContext {

    default <T> T getAttribute(String name) {
        return getAttribute(name, null);
    }

    <T> T getAttribute(String name, T defaultValue);

    StepContext setAttribute(String name, Object value);

    StepContext setAttributeIfAbsent(String name, Function<String, ?> mappingFunction);

    StepContext setOutput(Object output);

    /**
     * Sets a named egress for the processor. If not set or null, a default egress is assumed.
     *
     * @param egress Egress name
     * @return this context
     */
    StepContext setEgress(String egress);
}
