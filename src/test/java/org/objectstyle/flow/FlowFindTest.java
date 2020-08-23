package org.objectstyle.flow;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FlowFindTest {

    private static StepProcessor<Object> doNothing() {
        return (i, c) -> {/* */};
    }

    private static final Flow f31 = Flow.of(doNothing());
    private static final Flow f21 = Flow.of(doNothing()).egress("f31", f31);
    private static final Flow f22 = Flow.of(doNothing());
    private static final Flow f1 = Flow.of(doNothing()).egress("f21", f21).egress("f22", f22);

    private void assertFound(Flow expected, Optional<Flow> found) {
        assertTrue(found.isPresent(), "Not found");
        assertSame(expected, found.get());
    }

    private void assertNotFound(Optional<Flow> found) {
        assertTrue(found.isEmpty(), () -> "Expected no matches, instead got: " + found.get());
    }

    @Test
    public void testFind_Level0() {
        assertFound(f1, f1.find(""));
    }

    @Test
    public void testFind_Level1() {
        assertFound(f21, f1.find("f21"));
    }

    @Test
    public void testFind_Level2() {
        assertFound(f31, f1.find("f21.f31"));
    }

    @Test
    public void testFind_NotFound() {
        assertNotFound(f1.find("fx.fy"));
    }

    @Test
    public void testFind_NotFound_PartialMatch() {
        assertNotFound(f1.find("f21.fy"));
    }

    @Test
    public void testFind_NotFound_PastLeaf() {
        assertNotFound(f1.find("f21.f31.fx"));
    }
}
