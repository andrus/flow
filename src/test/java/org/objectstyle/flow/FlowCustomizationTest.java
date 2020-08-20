package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.runner.FlowRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowCustomizationTest {

    @Test
    public void testRun_ReplaceEgress() {
        Flow templateStep3 = Flow.of((i, c) -> c.setOutput(i + "_3"));
        Flow templateStep2 = Flow.of((i, c) -> c.setEgress("next").setOutput(i + "_2")).out("next", templateStep3);
        Flow templateStep1 = Flow.of((i, c) -> c.setEgress("next").setOutput(i + "_1")).out("next", templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.out("next", (i, c) -> c.setOutput(i + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }
}
