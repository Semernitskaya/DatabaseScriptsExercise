package com.ol;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
public class ExecutionPlanBuilder {

    public List<Integer> buildExecutionPlan(List<VulnerabilityScript> scripts) {
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
            List<NodeWrapper> nodeWrappers = new ArrayList<>();
            for (Node node : allNodes) {
                if (node.isVisited()) {
                    continue;
                }
                Integer pathLength = (int)path.getPathWeight(root, node);
                if (pathLength < Integer.MAX_VALUE) {
                    node.setVisited(true);
                    nodeWrappers.add(new NodeWrapper(node, pathLength));
                }
            }
            Collections.sort(nodeWrappers);
            nodeWrappers.forEach(nodeWrapper -> executionPlan.add(nodeWrapper.getNode().getScriptId()));
        }
        return executionPlan;
    }

    private Graph<Node, DefaultWeightedEdge> buildWeightedGraph(List<VulnerabilityScript> scripts,
                                                                Set<Node> allNodes) {
        Graph<Node, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (VulnerabilityScript script : scripts) {
            Node fromNode = new Node(script.getScriptId());
            allNodes.add(fromNode);
            for (Integer dependencyId : script.getDependencies()) {
                Node toNode = new Node(dependencyId);
                if (!graph.containsVertex(fromNode)) {
                    graph.addVertex(fromNode);
                }
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
     * Returns list of root-nodes, root-node has in-degree == 0
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
