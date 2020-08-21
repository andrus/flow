package org.objectstyle.flow;

import java.util.List;

public interface FlowVisitor {

    void visitNode(Flow node, List<String> path);
}
