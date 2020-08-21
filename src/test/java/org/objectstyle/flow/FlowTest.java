package org.objectstyle.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FlowTest {

    @Test
    public void testImmutable() {
        Flow f1 = Flow.of((i, c) -> c.proceed(i));
        Flow f2 = Flow.of((i, c) -> c.proceed(i));
        Flow f3 = f2.egress(f1);
        assertNotSame(f3, f2);
    }

    @Test
    public void testOut() {
        Flow f1 = Flow.of((i, c) -> c.proceed(i));
        Flow f2 = Flow.of((i, c) -> c.proceed(i)).egress(f1);
        assertSame(f1, f2.getDefaultEgress());
    }
}
