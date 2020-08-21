package org.objectstyle.flow;

public class FlowPath {

    static final String ROOT = "[root]";
    static final String DEFAULT_EGRESS = "[default]";

    private String name;
    private FlowPath previous;

    public static FlowPath root() {
        return new FlowPath(ROOT, null);
    }

    protected FlowPath(String name, FlowPath previous) {
        this.name = name;
        this.previous = previous;
    }

    public boolean isRoot() {
        return previous == null;
    }

    public FlowPath defaultSubPath() {
        return subpath(DEFAULT_EGRESS);
    }

    public FlowPath subpath(String name) {
        return new FlowPath(name, this);
    }

    public String getName() {
        return name;
    }

    public FlowPath getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        FlowPath p = this;
        while (p != null) {
            out.insert(0, p.getName());
            p = p.getPrevious();
            if (p != null) {
                out.insert(0, ".");
            }
        }

        return out.toString();
    }
}
