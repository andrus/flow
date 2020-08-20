package org.objectstyle.flow.runner;

import org.junit.jupiter.api.Test;
import org.objectstyle.flow.Flow;
import org.objectstyle.flow.StepProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowRunnerTest {

    @Test
    public void testRun_Sequential() {
        Flow f = Flow
                .of((i, c) -> c.setEgress("next").setOutput(i + "_one"))
                .out("next", (i, c) -> c.setOutput(i + "_two"));

        String result = FlowRunner.of(f).run("a");
        assertEquals("a_one_two", result);
    }

    @Test
    public void testRun_Conditional() {

        StepProcessor<Integer> oddEvenSplitter = (i, c) -> c.setEgress(i % 2 == 0 ? "even" : "odd").setOutput(i);
        Flow f = Flow
                .of(oddEvenSplitter)
                .out("odd", (i, c) -> c.setOutput(i + " is odd"))
                .out("even", (i, c) -> c.setOutput(i + " is even"));

        FlowRunner runner = FlowRunner.of(f);

        assertEquals("13 is odd", runner.run(13));
        assertEquals("54 is even", runner.run(54));
    }
}
