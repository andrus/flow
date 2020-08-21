package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.runner.FlowRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowCustomizationTest {

    @Test
    public void testRun_ReplaceDefaultEgress() {
        Flow templateStep3 = Flow.of((i, c) -> c.proceed(i + "_3"));
        Flow templateStep2 = Flow.of((i, c) -> c.proceed(i + "_2")).egress(templateStep3);
        Flow templateStep1 = Flow.of((i, c) -> c.proceed(i + "_1")).egress(templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.egress((i, c) -> c.proceed(i + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }

    @Test
    public void testRun_ReplaceNamedEgress() {
        Flow templateStep3 = Flow.of((i, c) -> c.proceed(i + "_3"));
        Flow templateStep2 = Flow.of((i, c) -> c.proceed(i + "_2", "next")).egress("next", templateStep3);
        Flow templateStep1 = Flow.of((i, c) -> c.proceed(i + "_1", "next")).egress("next", templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.egress("next", (i, c) -> c.proceed(i + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }
}
