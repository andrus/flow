package org.objectstyle.flow.runner;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.Flow;
import org.objectstyle.flow.StepContext;
import org.objectstyle.flow.StepProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowRunnerTest {

    @Test
    public void testRun_Sequential_DefaultEgress() {
        Flow f = Flow
                .of(c -> c.proceed(c.getInput() + "_one"))
                .egress(c -> c.proceed(c.getInput() + "_two"));

        String result = FlowRunner.of(f).run("a");
        assertEquals("a_one_two", result);
    }

    @Test
    public void testRun_Sequential_NamedEgress() {
        Flow f = Flow
                .of(c -> c.proceed(c.getInput() + "_one", "next"))
                .egress("next", c -> c.proceed(c.getInput() + "_two"));

        String result = FlowRunner.of(f).run("a");
        assertEquals("a_one_two", result);
    }

    @Test
    public void testRun_Conditional() {

        StepProcessor<Integer> oddEvenSplitter = (StepContext<Integer> c) -> c.proceed(c.getInput(), c.getInput() % 2 == 0 ? "even" : "odd");
        Flow f = Flow
                .of(oddEvenSplitter)
                .egress("odd", c -> c.proceed(c.getInput() + " is odd"))
                .egress("even", c -> c.proceed(c.getInput() + " is even"));

        FlowRunner runner = FlowRunner.of(f);

        assertEquals("13 is odd", runner.run(13));
        assertEquals("54 is even", runner.run(54));
    }
}
