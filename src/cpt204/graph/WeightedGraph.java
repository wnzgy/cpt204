package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeightedGraph {
    public static final class Edge {
        private final String to;
        private final int weight;

        public Edge(String to, int weight) {
            this.to = to;
            this.weight = weight;
        }

        public String getTo() {
            return to;
        }

        public int getWeight() {
            return weight;
        }
    }

    private final Map<String, List<Edge>> adjacency = new HashMap<>();
    private int undirectedEdgeCount = 0;

    public void addUndirectedEdge(String from, String to, int weight) {
        // The graph is undirected, so one CSV edge is stored in both directions.
        addDirectedEdge(from, to, weight);
        addDirectedEdge(to, from, weight);
        undirectedEdgeCount++;
    }

    private void addDirectedEdge(String from, String to, int weight) {
        adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, weight));
        // Make sure the destination node exists even if it has no outgoing edge yet.
        adjacency.computeIfAbsent(to, k -> new ArrayList<>());
    }

    public List<Edge> neighborsOf(String node) {
        if (!adjacency.containsKey(node)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(adjacency.get(node));
    }

    public Set<String> nodes() {
        return Collections.unmodifiableSet(adjacency.keySet());
    }

    public int getUndirectedEdgeCount() {
        return undirectedEdgeCount;
    }
}
