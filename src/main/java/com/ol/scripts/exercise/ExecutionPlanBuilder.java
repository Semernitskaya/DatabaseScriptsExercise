package com.ol.scripts.exercise;

import com.ol.scripts.exercise.model.Node;
import com.ol.scripts.exercise.model.NodePath;
import com.ol.scripts.exercise.model.VulnerabilityScript;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * Created by Semernitskaya on 13.04.2019.
 * Builds an execution plan for list of scripts with dependencies
 * <br/>Threadsafe
 */
@Slf4j
public class ExecutionPlanBuilder {

    public List<Integer> buildExecutionPlan(List<VulnerabilityScript> scripts) {
        log.info("Building execution plan for list of scripts, list size {}", scripts.size());
        log.info("Scripts {}", scripts);
        Set<Node> allNodes = new HashSet<>();
        Graph<Node, DefaultWeightedEdge> graph = buildWeightedGraph(scripts, allNodes);
        List<Integer> executionPlan = new ArrayList<>();

        if (allNodes.isEmpty() || allNodes.size() == 1) {
            //simple case - script doesn't have any dependencies
            allNodes.forEach(node -> executionPlan.add(node.getScriptId()));
            return executionPlan;
        }

        //usage Bellman-Ford algorithm for working with negative edges-weights
        BellmanFordShortestPath path = new BellmanFordShortestPath(graph);
        for (Node root : getRootNodes(graph, allNodes)) {
            getNodePaths(allNodes, path, root)
                    .forEach(nodePath -> executionPlan.add(nodePath.getNode().getScriptId()));
        }
        log.info("Build execution plan, size {}", executionPlan.size());
        return executionPlan;
    }

    private List<NodePath> getNodePaths(Set<Node> allNodes, BellmanFordShortestPath path, Node root) {
        List<NodePath> nodePaths = new ArrayList<>();
        for (Node node : allNodes) {
            if (node.isVisited()) {
                continue;
            }
            Integer pathLength = (int)path.getPathWeight(root, node);
            if (pathLength < Integer.MAX_VALUE) {
                node.setVisited(true);
                nodePaths.add(new NodePath(node, pathLength));
            }
        }
        Collections.sort(nodePaths);
        return nodePaths;
    }

    private Graph<Node, DefaultWeightedEdge> buildWeightedGraph(List<VulnerabilityScript> scripts,
                                                                Set<Node> allNodes) {
        Graph<Node, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (VulnerabilityScript script : scripts) {
            Node fromNode = new Node(script.getScriptId());
            allNodes.add(fromNode);
            if (!graph.containsVertex(fromNode)) {
                graph.addVertex(fromNode);
            }
            for (Integer dependencyId : script.getDependencies()) {
                Node toNode = new Node(dependencyId);
                if (!graph.containsVertex(toNode)) {
                    graph.addVertex(toNode);
                }
                allNodes.add(toNode);
                DefaultWeightedEdge edge = graph.addEdge(fromNode, toNode);
                //use negative edges-weights for searching the longest paths
                graph.setEdgeWeight(edge, -1);
            }
        }
        return graph;
    }

    /**
     * Returns list of root-nodes (root-node has in-degree == 0)
     * @param graph
     * @param allNodes
     * @return list of nodes, which have in-degree == 0
     */
    private Collection<Node> getRootNodes(Graph<Node, DefaultWeightedEdge> graph,
                                          Collection<Node> allNodes) {
        List<Node> rootNodes = new ArrayList<>();
        for (Node node : allNodes) {
            if (graph.inDegreeOf(node) == 0) {
                rootNodes.add(node);
            }
        }
        return rootNodes;
    }
}
