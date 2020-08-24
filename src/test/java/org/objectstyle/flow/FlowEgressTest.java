package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.runner.FlowRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowEgressTest {

    @Test
    public void testEgress_ReplaceDefault() {
        Flow templateStep3 = Flow.of(c -> c.proceed(c.getInput() + "_3"));
        Flow templateStep2 = Flow.of(c -> c.proceed(c.getInput() + "_2")).egress(templateStep3);
        Flow templateStep1 = Flow.of(c -> c.proceed(c.getInput() + "_1")).egress(templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.egress(c -> c.proceed(c.getInput() + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }

    @Test
    public void testEgress_ReplaceNamed() {
        Flow templateStep3 = Flow.of(c -> c.proceed(c.getInput() + "_3"));
        Flow templateStep2 = Flow.of(c -> c.proceed(c.getInput() + "_2", "next")).egress("next", templateStep3);
        Flow templateStep1 = Flow.of(c -> c.proceed(c.getInput() + "_1", "next")).egress("next", templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.egress("next", c -> c.proceed(c.getInput() + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }
}
