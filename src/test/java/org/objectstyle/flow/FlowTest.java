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
        assertSame(f1, f2.getEgress(FlowPath.DEFAULT_EGRESS));
    }

    @Test
    public void testAccept() {
        StepProcessor<Object> doNothing = (i, c) -> {/* */};
        Flow f31 = Flow.of(doNothing);
        Flow f21 = Flow.of(doNothing).egress("f31", f31);
        Flow f22 = Flow.of(doNothing);

        Flow f1 = Flow.of(doNothing).egress("f21", f21).egress("f22", f22);

        StringBuilder buffer = new StringBuilder();
        f1.accept((p, f) -> {
            buffer.append(p.isRoot() ? "" : ":" + p.getLastSegmentName());
            return true;
        });
        assertEquals(":f21:f31:f22", buffer.toString(), "Expected depth-first flow traversal");
    }

    @Test
    public void testAccept_TerminateSubtree() {
        StepProcessor<Object> doNothing = (i, c) -> {/* */};
        Flow f31 = Flow.of(doNothing);
        Flow f21 = Flow.of(doNothing).egress("f31", f31);
        Flow f22 = Flow.of(doNothing);

        Flow f1 = Flow.of(doNothing).egress("f21", f21).egress("f22", f22);

        StringBuilder buffer = new StringBuilder();
        f1.accept((p, f) -> {
            buffer.append(p.isRoot() ? "" : ":" + p.getLastSegmentName());

            // terminate a single branch
            return p.isRoot() || !p.getLastSegmentName().equals("f21");
        });
        assertEquals(":f21:f22", buffer.toString(), "Expected 'f21' branch to be terminated");
    }
}
