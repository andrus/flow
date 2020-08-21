package org.objectstyle.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FlowTest {

    @Test
    public void testImmutable() {
        Flow f1 = Flow.of((i, c) -> c.setOutput(i));
        Flow f2 = Flow.of((i, c) -> c.setOutput(i));
        Flow f3 = f2.out(f1);
        assertNotSame(f3, f2);
    }

    @Test
    public void testOut() {
        Flow f1 = Flow.of((i, c) -> c.setOutput(i));
        Flow f2 = Flow.of((i, c) -> c.setOutput(i)).out(f1);
        assertSame(f1, f2.getDefaultEgress());
    }
}
