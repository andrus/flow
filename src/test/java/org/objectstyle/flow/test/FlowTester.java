package org.objectstyle.flow.test;

import org.objectstyle.flow.Flow;

public class FlowTester {

    public static String flatten(Flow flow) {
        StringBuilder buffer = new StringBuilder();
        flow.accept((p, f) -> {
            buffer.append(p.isRoot() ? "" : ":" + p.getLastSegmentName());
            return true;
        });

        return buffer.toString();
    }
}
