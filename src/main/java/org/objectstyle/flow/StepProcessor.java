package org.objectstyle.flow;

/**
 * A function that does whatever custom processing a flow step requires.
 */
public interface StepProcessor<T> {

    /**
     * Runs a flow step.
     *
     * @param input   step input
     * @param context a mutable map of values that allows to pass data between the steps.
     */
    void run(T input, StepContext context);
}
