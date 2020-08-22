package org.objectstyle.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlowPathTest {

    @Test
    public void testStartsWith() {
        FlowPath p1 = FlowPath.of("p1", "p2", "p3");
        FlowPath p2 = FlowPath.of("p1", "p2");
        FlowPath p3 = FlowPath.of("p0", "p2", "p3");
        FlowPath p4 = FlowPath.of("p1", "p2", "p3");

        assertTrue(p1.startsWith(p2));
        assertFalse(p2.startsWith(p1));
        assertFalse(p1.startsWith(p3));
        assertTrue(p1.startsWith(p4));
    }
}
