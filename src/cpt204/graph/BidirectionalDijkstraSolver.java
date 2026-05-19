package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class BidirectionalDijkstraSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public BidirectionalDijkstraSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "Bidirectional Dijkstra";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Map<String, Long> distanceFromStart = new HashMap<>();
        Map<String, Long> distanceFromEnd = new HashMap<>();
        Map<String, String> previousFromStart = new HashMap<>();
        Map<String, String> nextToEnd = new HashMap<>();

        PriorityQueue<NodeDistance> startQueue = createQueue();
        PriorityQueue<NodeDistance> endQueue = createQueue();

        distanceFromStart.put(start, 0L);
        distanceFromEnd.put(destination, 0L);
        startQueue.add(new NodeDistance(start, 0L));
        endQueue.add(new NodeDistance(destination, 0L));

        long bestCost = Long.MAX_VALUE;
        String meetingNode = null;

        // Search from both ends and stop when no shorter connection is possible.
        while (!startQueue.isEmpty() && !endQueue.isEmpty()) {
            if (bestCost != Long.MAX_VALUE
                    && startQueue.peek().getDistance() + endQueue.peek().getDistance() >= bestCost) {
                break;
            }

            SearchResult startResult = expandOneSide(startQueue, distanceFromStart, previousFromStart, distanceFromEnd);
            if (startResult.getCost() < bestCost) {
                bestCost = startResult.getCost();
                meetingNode = startResult.getMeetingNode();
            }

            SearchResult endResult = expandOneSide(endQueue, distanceFromEnd, nextToEnd, distanceFromStart);
            if (endResult.getCost() < bestCost) {
                bestCost = endResult.getCost();
                meetingNode = endResult.getMeetingNode();
            }
        }

        if (meetingNode == null) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }
        return new PathResult(true, bestCost, buildPath(start, destination, meetingNode, previousFromStart, nextToEnd));
    }

    private PriorityQueue<NodeDistance> createQueue() {
        return new PriorityQueue<>(new Comparator<NodeDistance>() {
            @Override
            public int compare(NodeDistance first, NodeDistance second) {
                return Long.compare(first.getDistance(), second.getDistance());
            }
        });
    }

    private SearchResult expandOneSide(
            PriorityQueue<NodeDistance> queue,
            Map<String, Long> thisDistance,
            Map<String, String> thisPrevious,
            Map<String, Long> otherDistance
    ) {
        NodeDistance current = queue.poll();
        long knownDistance = thisDistance.getOrDefault(current.getNode(), Long.MAX_VALUE);
        if (current.getDistance() > knownDistance) {
            return new SearchResult(null, Long.MAX_VALUE);
        }

        String bestMeetingNode = null;
        long bestCost = Long.MAX_VALUE;

        // If this side reaches a node already reached by the other side, paths can meet here.
        if (otherDistance.containsKey(current.getNode())) {
            bestMeetingNode = current.getNode();
            bestCost = current.getDistance() + otherDistance.get(current.getNode());
        }

        for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
            long newDistance = current.getDistance() + edge.getWeight();
            long oldDistance = thisDistance.getOrDefault(edge.getTo(), Long.MAX_VALUE);
            if (newDistance < oldDistance) {
                thisDistance.put(edge.getTo(), newDistance);
                thisPrevious.put(edge.getTo(), current.getNode());
                queue.add(new NodeDistance(edge.getTo(), newDistance));
            }

            if (otherDistance.containsKey(edge.getTo())) {
                long fullCost = newDistance + otherDistance.get(edge.getTo());
                if (fullCost < bestCost) {
                    bestCost = fullCost;
                    bestMeetingNode = edge.getTo();
                }
            }
        }
        return new SearchResult(bestMeetingNode, bestCost);
    }

    private List<String> buildPath(
            String start,
            String destination,
            String meetingNode,
            Map<String, String> previousFromStart,
            Map<String, String> nextToEnd
    ) {
        List<String> leftPart = new ArrayList<>();
        String node = meetingNode;
        leftPart.add(node);
        while (!node.equals(start)) {
            // Move backwards through the start-side search tree.
            node = previousFromStart.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            leftPart.add(node);
        }
        Collections.reverse(leftPart);

        List<String> fullPath = new ArrayList<>(leftPart);
        node = meetingNode;
        while (!node.equals(destination)) {
            // Move forwards through the end-side search tree.
            node = nextToEnd.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            fullPath.add(node);
        }
        return fullPath;
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

    private static class SearchResult {
        private final String meetingNode;
        private final long cost;

        private SearchResult(String meetingNode, long cost) {
            this.meetingNode = meetingNode;
            this.cost = cost;
        }

        private String getMeetingNode() {
            return meetingNode;
        }

        private long getCost() {
            return cost;
        }
    }
}
