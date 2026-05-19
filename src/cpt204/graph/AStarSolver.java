package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AStarSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public AStarSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "A* (h=0)";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Map<String, Long> costFromStart = new HashMap<>();
        Map<String, String> previousNode = new HashMap<>();
        PriorityQueue<NodeScore> waitingNodes = new PriorityQueue<>(new Comparator<NodeScore>() {
            @Override
            public int compare(NodeScore first, NodeScore second) {
                return Long.compare(first.getTotalScore(), second.getTotalScore());
            }
        });

        costFromStart.put(start, 0L);
        waitingNodes.add(new NodeScore(start, 0L, heuristic(start, destination)));

        // A* chooses the node with the smallest g + h score.
        while (!waitingNodes.isEmpty()) {
            NodeScore current = waitingNodes.poll();
            long knownCost = costFromStart.getOrDefault(current.getNode(), Long.MAX_VALUE);
            if (current.getCostFromStart() > knownCost) {
                continue;
            }
            if (current.getNode().equals(destination)) {
                break;
            }

            for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
                long newCost = current.getCostFromStart() + edge.getWeight();
                long oldCost = costFromStart.getOrDefault(edge.getTo(), Long.MAX_VALUE);
                if (newCost < oldCost) {
                    // A better route to this neighbor was found.
                    costFromStart.put(edge.getTo(), newCost);
                    previousNode.put(edge.getTo(), current.getNode());
                    waitingNodes.add(new NodeScore(edge.getTo(), newCost, newCost + heuristic(edge.getTo(), destination)));
                }
            }
        }

        if (!costFromStart.containsKey(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }
        return new PathResult(true, costFromStart.get(destination), buildPath(previousNode, start, destination));
    }

    private long heuristic(String current, String destination) {
        // No coordinates are provided in paths.csv, so h=0 keeps A* correct.
        return 0L;
    }

    private List<String> buildPath(Map<String, String> previousNode, String start, String destination) {
        List<String> reversedPath = new ArrayList<>();
        String node = destination;
        reversedPath.add(node);
        while (!node.equals(start)) {
            node = previousNode.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            reversedPath.add(node);
        }

        Collections.reverse(reversedPath);
        return reversedPath;
    }

    private static class NodeScore {
        private final String node;
        private final long costFromStart;
        private final long totalScore;

        private NodeScore(String node, long costFromStart, long totalScore) {
            this.node = node;
            this.costFromStart = costFromStart;
            this.totalScore = totalScore;
        }

        private String getNode() {
            return node;
        }

        private long getCostFromStart() {
            return costFromStart;
        }

        private long getTotalScore() {
            return totalScore;
        }
    }
}
