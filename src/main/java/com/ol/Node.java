package com.ol;

import java.util.Objects;

/**
 * Created by Semernitskaya on 13.04.2019.
 *
 * Used for storing data about certain script in {@link org.jgrapht.Graph}
 */
public class Node {

    private final Integer scriptId;

    private boolean visited;

    public Node(Integer scriptId) {
        this.scriptId = scriptId;
    }

    public Integer getScriptId() {
        return scriptId;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(scriptId, node.scriptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scriptId);
    }
}
