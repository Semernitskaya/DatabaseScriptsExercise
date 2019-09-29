package com.ol.scripts.exercise.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Semernitskaya on 13.04.2019.
 *
 * Used for storing data about certain script in {@link org.jgrapht.Graph}
 */
@Data
@EqualsAndHashCode(exclude = "visited")
public class Node {

    private final Integer scriptId;

    private boolean visited;
}
