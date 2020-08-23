package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.test.FlowTester;

import static org.junit.jupiter.api.Assertions.*;

public class FlowReplaceTest {

    private static final StepProcessor<Object> doNothing = (i, c) -> {/* */};
    private static final Flow replacement = Flow.of(doNothing).egress("r", doNothing);

    private Flow testFlow() {
        Flow f31 = Flow.of(doNothing);
        Flow f21 = Flow.of(doNothing).egress("f31", f31);
        Flow f22 = Flow.of(doNothing);
        return Flow.of(doNothing).egress("f21", f21).egress("f22", f22);
    }

    @Test
    public void testLevel0() {
        assertSame(replacement, testFlow().replace("", replacement));
    }

    @Test
    public void testLevel1() {
        Flow template = testFlow();
        Flow replaced = template.replace("f21", replacement);
        assertNotSame(template, replaced);
        assertEquals(":f21:r:f22", FlowTester.flatten(replaced));
    }

    @Test
    public void testLevel2() {
        Flow template = testFlow();
        Flow replaced = template.replace("f21.f31", replacement);
        assertNotSame(template, replaced);
        assertEquals(":f21:f31:r:f22", FlowTester.flatten(replaced));
    }

    @Test
    public void testLeaf() {
        Flow template = testFlow();
        Flow replaced = template.replace("f22.newNode", replacement);
        assertNotSame(template, replaced);
        assertEquals(":f21:f31:f22:newNode:r", FlowTester.flatten(replaced));
    }

    @Test
    public void testInvalidPath() {
        Flow template = testFlow();
        assertThrows(RuntimeException.class, () -> template.replace("fx.fy", replacement));
    }
}
