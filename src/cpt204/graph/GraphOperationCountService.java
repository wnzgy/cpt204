package cpt204.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class GraphOperationCountService {
    private final WeightedGraph graph;

    public GraphOperationCountService(WeightedGraph graph) {
        this.graph = graph;
    }

    public List<GraphOperationCountResult> countOperations(List<GraphCaseDefinition> caseDefinitions) {
        List<GraphOperationCountResult> results = new ArrayList<>();
        for (GraphCaseDefinition caseDefinition : caseDefinitions) {
            results.add(countFullCase("Dijkstra", caseDefinition));
            results.add(countFullCase("Bidirectional Dijkstra", caseDefinition));
            results.add(countFullCase("A* (h=0)", caseDefinition));
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
            if ("Bidirectional Dijkstra".equals(algorithmName)) {
                segmentResult = countBidirectionalDijkstraSegment(nodesToVisit.get(i), nodesToVisit.get(i + 1));
            } else if ("A* (h=0)".equals(algorithmName)) {
                segmentResult = countAStarSegment(nodesToVisit.get(i), nodesToVisit.get(i + 1));
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

    private SegmentCountResult countAStarSegment(String start, String destination) {
        GraphCounter counter = new GraphCounter();
        if (start.equals(destination)) {
            return new SegmentCountResult(true, 0L, counter);
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }

        Map<String, Long> costFromStart = new HashMap<>();
        PriorityQueue<NodeDistance> waitingNodes = createDistanceQueue();

        costFromStart.put(start, 0L);
        waitingNodes.add(new NodeDistance(start, 0L));
        counter.queuePushes++;

        while (!waitingNodes.isEmpty()) {
            NodeDistance current = waitingNodes.poll();
            counter.queuePolls++;
            long knownCost = costFromStart.getOrDefault(current.getNode(), Long.MAX_VALUE);
            if (current.getDistance() > knownCost) {
                counter.stalePolls++;
                continue;
            }

            counter.visitedNodes++;
            if (current.getNode().equals(destination)) {
                break;
            }

            for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
                counter.edgeChecks++;
                long newCost = current.getDistance() + edge.getWeight();
                long oldCost = costFromStart.getOrDefault(edge.getTo(), Long.MAX_VALUE);
                if (newCost < oldCost) {
                    costFromStart.put(edge.getTo(), newCost);
                    waitingNodes.add(new NodeDistance(edge.getTo(), newCost + heuristic(edge.getTo(), destination)));
                    counter.successfulRelaxations++;
                    counter.queuePushes++;
                }
            }
        }

        if (!costFromStart.containsKey(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }
        return new SegmentCountResult(true, costFromStart.get(destination), counter);
    }

    private SegmentCountResult countBidirectionalDijkstraSegment(String start, String destination) {
        GraphCounter counter = new GraphCounter();
        if (start.equals(destination)) {
            return new SegmentCountResult(true, 0L, counter);
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }

        Map<String, Long> distanceFromStart = new HashMap<>();
        Map<String, Long> distanceFromEnd = new HashMap<>();
        PriorityQueue<NodeDistance> startQueue = createDistanceQueue();
        PriorityQueue<NodeDistance> endQueue = createDistanceQueue();

        distanceFromStart.put(start, 0L);
        distanceFromEnd.put(destination, 0L);
        startQueue.add(new NodeDistance(start, 0L));
        endQueue.add(new NodeDistance(destination, 0L));
        counter.queuePushes += 2;

        long bestCost = Long.MAX_VALUE;

        while (!startQueue.isEmpty() && !endQueue.isEmpty()) {
            if (bestCost != Long.MAX_VALUE
                    && startQueue.peek().getDistance() + endQueue.peek().getDistance() >= bestCost) {
                break;
            }

            SearchCountResult startResult = expandOneSide(startQueue, distanceFromStart, distanceFromEnd, counter);
            if (startResult.getCost() < bestCost) {
                bestCost = startResult.getCost();
            }

            SearchCountResult endResult = expandOneSide(endQueue, distanceFromEnd, distanceFromStart, counter);
            if (endResult.getCost() < bestCost) {
                bestCost = endResult.getCost();
            }
        }

        if (bestCost == Long.MAX_VALUE) {
            return new SegmentCountResult(false, Long.MAX_VALUE, counter);
        }
        return new SegmentCountResult(true, bestCost, counter);
    }

    private SearchCountResult expandOneSide(
            PriorityQueue<NodeDistance> queue,
            Map<String, Long> thisDistance,
            Map<String, Long> otherDistance,
            GraphCounter counter
    ) {
        NodeDistance current = queue.poll();
        counter.queuePolls++;
        long knownDistance = thisDistance.getOrDefault(current.getNode(), Long.MAX_VALUE);
        if (current.getDistance() > knownDistance) {
            counter.stalePolls++;
            return new SearchCountResult(Long.MAX_VALUE);
        }

        counter.visitedNodes++;
        long bestCost = Long.MAX_VALUE;
        if (otherDistance.containsKey(current.getNode())) {
            bestCost = current.getDistance() + otherDistance.get(current.getNode());
        }

        for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
            counter.edgeChecks++;
            long newDistance = current.getDistance() + edge.getWeight();
            long oldDistance = thisDistance.getOrDefault(edge.getTo(), Long.MAX_VALUE);
            if (newDistance < oldDistance) {
                thisDistance.put(edge.getTo(), newDistance);
                queue.add(new NodeDistance(edge.getTo(), newDistance));
                counter.successfulRelaxations++;
                counter.queuePushes++;
            }

            if (otherDistance.containsKey(edge.getTo())) {
                long fullCost = newDistance + otherDistance.get(edge.getTo());
                if (fullCost < bestCost) {
                    bestCost = fullCost;
                }
            }
        }
        return new SearchCountResult(bestCost);
    }

    private PriorityQueue<NodeDistance> createDistanceQueue() {
        return new PriorityQueue<>(new Comparator<NodeDistance>() {
            @Override
            public int compare(NodeDistance first, NodeDistance second) {
                return Long.compare(first.getDistance(), second.getDistance());
            }
        });
    }

    private long heuristic(String current, String destination) {
        return 0L;
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

    private static class SearchCountResult {
        private final long cost;

        private SearchCountResult(long cost) {
            this.cost = cost;
        }

        private long getCost() {
            return cost;
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
