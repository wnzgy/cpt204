package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class DijkstraSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public DijkstraSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "Dijkstra";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Map<String, Long> shortestDistance = new HashMap<>();
        Map<String, String> previousNode = new HashMap<>();
        PriorityQueue<NodeDistance> waitingNodes = new PriorityQueue<>(new Comparator<NodeDistance>() {
            @Override
            public int compare(NodeDistance first, NodeDistance second) {
                return Long.compare(first.getDistance(), second.getDistance());
            }
        });

        shortestDistance.put(start, 0L);
        waitingNodes.add(new NodeDistance(start, 0L));

        while (!waitingNodes.isEmpty()) {
            NodeDistance current = waitingNodes.poll();
            long knownDistance = shortestDistance.getOrDefault(current.getNode(), Long.MAX_VALUE);
            if (current.getDistance() > knownDistance) {
                continue;
            }
            if (current.getNode().equals(destination)) {
                break;
            }

            for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
                long newDistance = current.getDistance() + edge.getWeight();
                long oldDistance = shortestDistance.getOrDefault(edge.getTo(), Long.MAX_VALUE);
                if (newDistance < oldDistance) {
                    shortestDistance.put(edge.getTo(), newDistance);
                    previousNode.put(edge.getTo(), current.getNode());
                    waitingNodes.add(new NodeDistance(edge.getTo(), newDistance));
                }
            }
        }

        if (!shortestDistance.containsKey(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        List<String> path = reconstructPath(previousNode, start, destination);
        return new PathResult(true, shortestDistance.get(destination), path);
    }

    private List<String> reconstructPath(Map<String, String> previous, String start, String destination) {
        List<String> reversedPath = new ArrayList<>();
        String node = destination;
        reversedPath.add(node);
        while (!node.equals(start)) {
            node = previous.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            reversedPath.add(node);
        }

        List<String> path = new ArrayList<>(reversedPath.size());
        for (int i = reversedPath.size() - 1; i >= 0; i--) {
            path.add(reversedPath.get(i));
        }
        return path;
    }

    private static class NodeDistance {
        private final String node;
        private final long distance;

        private NodeDistance(String node, long distance) {
            this.node = node;
            this.distance = distance;
        }

        private String getNode() {
            return node;
        }

        private long getDistance() {
            return distance;
        }
    }
}
