package org.objectstyle.flow.processor;

import org.objectstyle.flow.StepContext;
import org.objectstyle.flow.StepProcessor;

public class PassThroughProcessor implements StepProcessor {

    public static final String DEFAULT_EGRESS = "default";

    private static final PassThroughProcessor instance = new PassThroughProcessor();

    public static <T> StepProcessor<T> getInstance() {
        return instance;
    }

    @Override
    public void run(Object input, StepContext context) {
        context.setOutput(input).setEgress(DEFAULT_EGRESS);
    }
}
