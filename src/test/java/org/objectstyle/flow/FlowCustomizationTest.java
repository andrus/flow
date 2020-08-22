package org.objectstyle.flow;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.runner.FlowRunner;
import org.objectstyle.flow.test.FlowTester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class FlowCustomizationTest {

    @Test
    public void testEgress_ReplaceDefault() {
        Flow templateStep3 = Flow.of((i, c) -> c.proceed(i + "_3"));
        Flow templateStep2 = Flow.of((i, c) -> c.proceed(i + "_2")).egress(templateStep3);
        Flow templateStep1 = Flow.of((i, c) -> c.proceed(i + "_1")).egress(templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.egress((i, c) -> c.proceed(i + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }

    @Test
    public void testEgress_ReplaceNamed() {
        Flow templateStep3 = Flow.of((i, c) -> c.proceed(i + "_3"));
        Flow templateStep2 = Flow.of((i, c) -> c.proceed(i + "_2", "next")).egress("next", templateStep3);
        Flow templateStep1 = Flow.of((i, c) -> c.proceed(i + "_1", "next")).egress("next", templateStep2);

        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"));

        Flow alt2 = templateStep1.egress("next", (i, c) -> c.proceed(i + "_4"));
        assertEquals("a_1_4", FlowRunner.of(alt2).run("a"));
        assertEquals("a_1_2_3", FlowRunner.of(templateStep1).run("a"), "The original template must be unchanged");
    }

    @Test
    public void testInsert() {

        StepProcessor<Object> doNothing = (i, c) -> {/* */};
        Flow f31 = Flow.of(doNothing);
        Flow f21 = Flow.of(doNothing).egress("f31", f31);
        Flow f22 = Flow.of(doNothing);
        Flow template = Flow.of(doNothing).egress("f21", f21).egress("f22", f22);

        Flow inserted1 = template.insert("f21", doNothing);
        assertNotSame(template, inserted1);
        assertEquals(":f21:_default:f31:f22", FlowTester.flatten(inserted1));

        Flow inserted2 = template.insert("f21.f31", doNothing);
        assertNotSame(template, inserted2);
        assertEquals(":f21:f31:_default:f22", FlowTester.flatten(inserted2));
    }
}
