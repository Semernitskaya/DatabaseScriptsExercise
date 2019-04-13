package com.ol;

/**
 * Created by Semernitskaya on 13.04.2019.
 *
 * <br/>Used for storing {@link Node} and pathLength to the rootNode
 * <br/>to find the farthest dependency-script
 */
class NodeWrapper implements Comparable<NodeWrapper> {

    private final Node node;

    private final Integer pathLength;

    NodeWrapper(Node node, Integer pathLength) {
        this.node = node;
        this.pathLength = pathLength;
    }

    @Override
    public int compareTo(NodeWrapper o) {
        return this.pathLength.compareTo(o.pathLength);
    }

    public Node getNode() {
        return node;
    }

    public Integer getPathLength() {
        return pathLength;
    }
}
