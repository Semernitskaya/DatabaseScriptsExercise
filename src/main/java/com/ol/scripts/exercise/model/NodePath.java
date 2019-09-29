package com.ol.scripts.exercise.model;

import lombok.Data;

/**
 * Created by Semernitskaya on 13.04.2019.
 * <p>
 * <br/>Used for storing {@link Node} and pathLength to the rootNode
 * <br/>to find the farthest dependency-script
 */
@Data
public class NodePath implements Comparable<NodePath> {

    private final Node node;

    private final Integer pathLength;

    @Override
    public int compareTo(NodePath o) {
        return this.pathLength.compareTo(o.pathLength);
    }

}
