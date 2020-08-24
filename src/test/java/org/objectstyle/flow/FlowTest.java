package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.runner.FlowRunner;
import org.objectstyle.flow.test.FlowTester;

import static org.junit.jupiter.api.Assertions.*;

public class FlowTest {

    private static final StepProcessor<?> doNothing = c -> {/* */};

    @Test
    public void testOfSequence_One() {
        Flow f = Flow.ofSequence(doNothing);
        assertEquals("", FlowTester.flatten(f));
    }

    @Test
    public void testOfSequence() {
        Flow f = Flow.ofSequence(
                (StepContext<StringBuilder> c) -> c.proceed(c.getInput().append("one,")),
                (StepContext<StringBuilder> c) -> c.proceed(c.getInput().append("two,")),
                (StepContext<StringBuilder> c) -> c.proceed(c.getInput().append("three")));
        assertEquals("one,two,three", FlowRunner.of(f).run(new StringBuilder()).toString());
    }

    @Test
    public void testEgress_Immutable() {
        Flow f1 = Flow.of(c -> c.proceed(c.getInput()));
        Flow f2 = Flow.of(c -> c.proceed(c.getInput()));
        Flow f3 = f2.egress(f1);
        assertNotSame(f3, f2);
    }

    @Test
    public void testEgress() {
        Flow f1 = Flow.of(c -> c.proceed(c.getInput()));
        Flow f2 = Flow.of(c -> c.proceed(c.getInput())).egress(f1);
        assertSame(f1, f2.getEgress(FlowPath.DEFAULT_EGRESS));
    }

    @Test
    public void testAccept() {

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
