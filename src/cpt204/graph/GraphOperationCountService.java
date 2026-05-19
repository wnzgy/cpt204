package cpt204.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class GraphOperationCountService {
    private final WeightedGraph graph;

    public GraphOperationCountService(WeightedGraph graph) {
        this.graph = graph;
    }

    public List<GraphOperationCountResult> countOperations(List<GraphCaseDefinition> caseDefinitions) {
        List<GraphOperationCountResult> results = new ArrayList<>();
        for (GraphCaseDefinition caseDefinition : caseDefinitions) {
            results.add(countFullCase("Dijkstra", caseDefinition));
            results.add(countFullCase("BFS", caseDefinition));
        }
        return results;
    }

    private GraphOperationCountResult countFullCase(String algorithmName, GraphCaseDefinition caseDefinition) {
        List<String> nodesToVisit = new ArrayList<>();
        nodesToVisit.add(caseDefinition.getStart());
        nodesToVisit.addAll(caseDefinition.getWaypointsInOrder());
        nodesToVisit.add(caseDefinition.getDestination());

        GraphCounter totalCounter = new GraphCounter();
        long totalCost = 0L;

        for (int i = 0; i < nodesToVisit.size() - 1; i++) {
            SegmentCountResult segmentResult;
            if ("BFS".equals(algorithmName)) {
                segmentResult = countBfsSegment(nodesToVisit.get(i), nodesToVisit.get(i + 1));
            } else {
                segmentResult = countDijkstraSegment(nodesToVisit.get(i), nodesToVisit.get(i + 1));
            }

            totalCounter.add(segmentResult.getCounter());
            if (!segmentResult.isReachable()) {
                return totalCounter.toGraphResult(
                        algorithmName,
                        caseDefinition.getCaseName(),
                        nodesToVisit.size() - 1,
                        false,
                        Long.MAX_VALUE
                );
            }
            totalCost += segmentResult.getCost();
        }

        return totalCounter.toGraphResult(
                algorithmName,
                caseDefinition.getCaseName(),
                nodesToVisit.size() - 1,
                true,
                totalCost
        );
    }

    private SegmentCountResult countDijkstraSegment(String start, String destination) {
        GraphCounter counter = new GraphCounter();
        if (start.equals(destination)) {
            return new SegmentCountResult(true, 0L, counter);
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }

        Map<String, Long> shortestDistance = new HashMap<>();
        PriorityQueue<NodeDistance> waitingNodes = createDistanceQueue();

        shortestDistance.put(start, 0L);
        waitingNodes.add(new NodeDistance(start, 0L));
        counter.queuePushes++;

        while (!waitingNodes.isEmpty()) {
            NodeDistance current = waitingNodes.poll();
            counter.queuePolls++;
            long knownDistance = shortestDistance.getOrDefault(current.getNode(), Long.MAX_VALUE);
            if (current.getDistance() > knownDistance) {
                counter.stalePolls++;
                continue;
            }

            counter.visitedNodes++;
            if (current.getNode().equals(destination)) {
                break;
            }

            for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
                counter.edgeChecks++;
                long newDistance = current.getDistance() + edge.getWeight();
                long oldDistance = shortestDistance.getOrDefault(edge.getTo(), Long.MAX_VALUE);
                if (newDistance < oldDistance) {
                    shortestDistance.put(edge.getTo(), newDistance);
                    waitingNodes.add(new NodeDistance(edge.getTo(), newDistance));
                    counter.successfulRelaxations++;
                    counter.queuePushes++;
                }
            }
        }

        if (!shortestDistance.containsKey(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }
        return new SegmentCountResult(true, shortestDistance.get(destination), counter);
    }

    private SegmentCountResult countBfsSegment(String start, String destination) {
        GraphCounter counter = new GraphCounter();
        if (start.equals(destination)) {
            return new SegmentCountResult(true, 0L, counter);
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }

        Queue<String> queue = new ArrayDeque<>();
        Map<String, Boolean> visited = new HashMap<>();

        queue.add(start);
        counter.queuePushes++;
        visited.put(start, true);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            counter.queuePolls++;
            counter.visitedNodes++;
            if (current.equals(destination)) {
                break;
            }

            for (WeightedGraph.Edge edge : graph.neighborsOf(current)) {
                counter.edgeChecks++;
                String neighbor = edge.getTo();
                if (!visited.containsKey(neighbor)) {
                    visited.put(neighbor, true);
                    queue.add(neighbor);
                    counter.queuePushes++;
                }
            }
        }

        if (!visited.containsKey(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }

        PathResult pathResult = new BfsSolver(graph).shortestPath(start, destination);
        if (!pathResult.isReachable()) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }
        return new SegmentCountResult(true, pathResult.getTotalCost(), counter);
    }

    private PriorityQueue<NodeDistance> createDistanceQueue() {
        return new PriorityQueue<>(new Comparator<NodeDistance>() {
            @Override
            public int compare(NodeDistance first, NodeDistance second) {
                return Long.compare(first.getDistance(), second.getDistance());
            }
        });
    }

    private static class GraphCounter {
        private long queuePolls;
        private long stalePolls;
        private long queuePushes;
        private long edgeChecks;
        private long successfulRelaxations;
        private long visitedNodes;

        private void add(GraphCounter other) {
            queuePolls += other.queuePolls;
            stalePolls += other.stalePolls;
            queuePushes += other.queuePushes;
            edgeChecks += other.edgeChecks;
            successfulRelaxations += other.successfulRelaxations;
            visitedNodes += other.visitedNodes;
        }

        private GraphOperationCountResult toGraphResult(
                String algorithmName,
                String caseName,
                int segmentCount,
                boolean reachable,
                long totalCost
        ) {
            return new GraphOperationCountResult(
                    algorithmName,
                    caseName,
                    segmentCount,
                    reachable,
                    totalCost,
                    queuePolls,
                    stalePolls,
                    queuePushes,
                    edgeChecks,
                    successfulRelaxations,
                    visitedNodes
            );
        }
    }

    private static class SegmentCountResult {
        private final boolean reachable;
        private final long cost;
        private final GraphCounter counter;

        private SegmentCountResult(boolean reachable, long cost, GraphCounter counter) {
            this.reachable = reachable;
            this.cost = cost;
            this.counter = counter;
        }

        private boolean isReachable() {
            return reachable;
        }

        private long getCost() {
            return cost;
        }

        private GraphCounter getCounter() {
            return counter;
        }
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
