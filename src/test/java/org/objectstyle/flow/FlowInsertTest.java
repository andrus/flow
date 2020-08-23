package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.test.FlowTester;

import static org.junit.jupiter.api.Assertions.*;

public class FlowInsertTest {

    private static final StepProcessor<Object> doNothing = (i, c) -> {/* */};

    private Flow testFlow() {
        Flow f31 = Flow.of(doNothing);
        Flow f21 = Flow.of(doNothing).egress("f31", f31);
        Flow f22 = Flow.of(doNothing);
        return Flow.of(doNothing).egress("f21", f21).egress("f22", f22);
    }

    @Test
    public void testLevel0() {

        Flow template = testFlow();

        Flow inserted = template.insert("", doNothing);
        assertNotSame(template, inserted);
        assertEquals(":_default:f21:f31:f22", FlowTester.flatten(inserted));
    }

    @Test
    public void testLevel1() {

        Flow template = testFlow();

        Flow inserted = template.insert("f21", doNothing);
        assertNotSame(template, inserted);
        assertEquals(":f21:_default:f31:f22", FlowTester.flatten(inserted));
    }

    @Test
    public void testLevel2() {

        Flow template = testFlow();

        Flow inserted = template.insert("f21.f31", doNothing);
        assertNotSame(template, inserted);
        assertEquals(":f21:f31:_default:f22", FlowTester.flatten(inserted));
    }

    @Test
    public void testLeaf() {

        Flow template = testFlow();

        Flow inserted = template.insert("f22.newNode", doNothing);
        assertNotSame(template, inserted);
        assertEquals(":f21:f31:f22:newNode", FlowTester.flatten(inserted));
    }

    @Test
    public void testInvalidPath() {
        Flow template = testFlow();
        assertThrows(RuntimeException.class, () -> template.insert("fx.fy", doNothing));
    }
}
