package org.objectstyle.flow;

/**
 * A function that does whatever custom processing a flow step requires.
 */
public interface StepProcessor<T> {

    /**
     * Runs a flow step.
     *
     * @param context a context that allows to pass information between steps
     */
    void run(StepContext<T> context);
}
