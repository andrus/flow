package org.objectstyle.flow;

import java.util.Arrays;
import java.util.Objects;

/**
 * Encapsulates a single linear path through the flow tree identified by the names of egresses.
 */
public class FlowPath {

    public static final String DEFAULT_EGRESS = "_default";

    private final String[] segments;

    static void validateSegmentName(String pathSegment) {
        if (pathSegment.contains(".")) {
            throw new IllegalArgumentException("Path segment name must not contain '.', " +
                    "which is reserved as segment separator. Offending name: " + pathSegment);
        }
    }

    /**
     * Creates a path pointing to the current node.
     */
    public static FlowPath root() {
        return new FlowPath(new String[]{});
    }

    /**
     * Creates a path from an array of segments.
     */
    public static FlowPath of(String... segments) {
        // TODO: clone the array for guaranteed immutability?
        return new FlowPath(segments);
    }

    /**
     * Creates a path from a dot-separated String.
     */
    public static FlowPath parse(String path) {
        Objects.requireNonNull(path, "Null 'path'");

        return path.length() > 0
                ? new FlowPath(path.split("\\."))
                : FlowPath.root();
    }

    protected FlowPath(String[] segments) {
        this.segments = segments;
    }

    public boolean isRoot() {
        return segments.length == 0;
    }

    public FlowPath subpathForDefaultEgress() {
        return subpath(DEFAULT_EGRESS);
    }

    public FlowPath subpath(String name) {
        String[] segmentsPlusOne = new String[segments.length + 1];
        System.arraycopy(segments, 0, segmentsPlusOne, 0, segments.length);
        segmentsPlusOne[segments.length] = name;
        return new FlowPath(segmentsPlusOne);
    }

    public String getSegmentName(int pos) {
        return segments[pos];
    }

    public String getLastSegmentName() {
        return segments.length == 0 ? null : segments[segments.length - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowPath flowPath = (FlowPath) o;
        return Arrays.equals(segments, flowPath.segments);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(segments);
    }

    public boolean startsWith(FlowPath path) {
        int otherLen = path.segments.length;
        if (segments.length < otherLen) {
            return false;
        }

        return Arrays.equals(segments, 0, otherLen, path.segments, 0, otherLen);
    }

    public int length() {
        return segments.length;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        int len = segments.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                out.append(".");
            }
            out.append(segments[i]);

        }
        return out.toString();
    }
}
