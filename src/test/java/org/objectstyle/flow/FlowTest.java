package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.processor.PassThroughProcessor;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FlowTest {

    @Test
    public void testImmutable() {
        Flow f1 = Flow.of(PassThroughProcessor.getInstance());
        Flow f2 = Flow.of(PassThroughProcessor.getInstance());
        Flow f3 = f2.out(PassThroughProcessor.DEFAULT_EGRESS, f1);
        assertNotSame(f3, f2);
    }

    @Test
    public void testOut() {
        Flow f1 = Flow.of(PassThroughProcessor.getInstance());
        Flow f2 = Flow
                .of(PassThroughProcessor.getInstance())
                .out(PassThroughProcessor.DEFAULT_EGRESS, f1);

        assertSame(f1, f2.getEgress(PassThroughProcessor.DEFAULT_EGRESS));
    }
}
