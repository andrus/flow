package org.objectstyle.flow;

import java.util.function.Function;

public interface StepContext<INPUT> {

    default <T> T getProperty(String name) {
        return getProperty(name, null);
    }

    <T> T getProperty(String name, T defaultValue);

    StepContext<INPUT> setProperty(String name, Object value);

    StepContext<INPUT> setPropertyIfAbsent(String name, Function<String, ?> mappingFunction);

    INPUT getInput();

    /**
     * Notifies the runner that all the processing is done, and control can be passed to the next step, and no output
     * was generated. If there is a step connected via the default egress, processing will continue at that step.
     * Otherwise the processing will stop.
     *
     * @return this context
     */
    default StepContext<INPUT> proceed() {
        return proceed(null);
    }

    /**
     * Notifies the runner that all the processing is done, and control can be passed to the next step with the provided
     * output. If there is a step connected via the default egress, processing will continue at that step.
     * Otherwise the processing will stop.
     *
     * @param output output of the processing
     * @return this context
     */
    default StepContext<INPUT> proceed(Object output) {
        return proceed(output, FlowPath.DEFAULT_EGRESS);
    }

    /**
     * Notifies the runner that all the processing is done, and control can be passed to the next step with the provided
     * output. If there is a step connected via the named egress, processing will continue at that step.
     * Otherwise the processing will stop.
     *
     * @param output     output of the processing
     * @param egressName the name of the egress to continue processing
     * @return this context
     */
    StepContext<INPUT> proceed(Object output, String egressName);
}
