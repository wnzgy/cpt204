package cpt204.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BfsSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public BfsSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "BFS";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> previous = new HashMap<>();
        Map<String, Integer> hops = new HashMap<>();

        queue.add(start);
        hops.put(start, 0);
        previous.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(destination)) {
                break;
            }
            for (WeightedGraph.Edge edge : graph.neighborsOf(current)) {
                String neighbor = edge.getTo();
                if (!hops.containsKey(neighbor)) {
                    hops.put(neighbor, hops.get(current) + 1);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!hops.containsKey(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        List<String> path = reconstructPath(previous, start, destination);
        long weightAlongPath = sumEdgeWeights(path);
        return new PathResult(true, weightAlongPath, path);
    }

    private List<String> reconstructPath(Map<String, String> previous, String start, String destination) {
        List<String> reversed = new ArrayList<>();
        String node = destination;
        while (node != null) {
            reversed.add(node);
            node = previous.get(node);
        }
        List<String> path = new ArrayList<>(reversed.size());
        for (int i = reversed.size() - 1; i >= 0; i--) {
            path.add(reversed.get(i));
        }
        return path;
    }

    private long sumEdgeWeights(List<String> path) {
        long total = 0L;
        for (int i = 0; i < path.size() - 1; i++) {
            int weight = graph.edgeWeight(path.get(i), path.get(i + 1));
            if (weight < 0) {
                return Long.MAX_VALUE;
            }
            total += weight;
        }
        return total;
    }
}
