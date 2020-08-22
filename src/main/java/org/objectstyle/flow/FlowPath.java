package org.objectstyle.flow;

public class FlowPath {

    static final String ROOT = "_root";
    static final String DEFAULT_EGRESS = "_default";

    private String name;
    private FlowPath previous;

    static void validatePathSegment(String pathSegment) {
        if (pathSegment.startsWith("_")) {
            throw new IllegalArgumentException("Path segment name must not start with '_', " +
                    "which is reserved for internal uses. Offending name: " + pathSegment);
        }

        if (pathSegment.contains(".")) {
            throw new IllegalArgumentException("Path segment name must not contain '.', " +
                    "which is reserved as segment separator. Offending name: " + pathSegment);
        }
    }

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

    public FlowPath subpath() {
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
