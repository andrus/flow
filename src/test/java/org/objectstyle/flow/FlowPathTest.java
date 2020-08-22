package org.objectstyle.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FlowPathTest {

    @Test
    public void testParse() {
        assertEquals(FlowPath.of("a"), FlowPath.parse("a"));
        assertEquals(FlowPath.of("a", "b", "c"), FlowPath.parse("a.b.c"));
    }

    @Test
    public void testEquals() {
        FlowPath p1 = FlowPath.of("p1", "p2", "p3");
        FlowPath p2 = FlowPath.of("p1", "p2");
        FlowPath p3 = FlowPath.of("p0", "p2", "p3");
        FlowPath p4 = FlowPath.of("p1", "p2", "p3");

        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));
        assertFalse(p1.equals(p3));
        assertTrue(p1.equals(p4));
    }

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
