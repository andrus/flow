package org.objectstyle.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlowTest {

    @Test
    public void testEgress_Immutable() {
        Flow f1 = Flow.of((i, c) -> c.proceed(i));
        Flow f2 = Flow.of((i, c) -> c.proceed(i));
        Flow f3 = f2.egress(f1);
        assertNotSame(f3, f2);
    }

    @Test
    public void testEgress() {
        Flow f1 = Flow.of((i, c) -> c.proceed(i));
        Flow f2 = Flow.of((i, c) -> c.proceed(i)).egress(f1);
        assertSame(f1, f2.getDefaultEgress());
    }

    @Test
    public void testAccept() {
        StepProcessor<Object> doNothing = (i, c) -> {/* */};
        Flow f31 = Flow.of(doNothing);
        Flow f21 = Flow.of(doNothing).egress("f31", f31);
        Flow f22 = Flow.of(doNothing);

        Flow f1 = Flow.of(doNothing).egress("f21", f21).egress("f22", f22);

        StringBuilder buffer = new StringBuilder();
        f1.accept((f, p) -> buffer.append(p.isRoot() ? "" : ":").append(p.getName()));
        assertEquals("[root]:f21:f31:f22", buffer.toString(), "Expected depth-first flow traversal");
    }
}
