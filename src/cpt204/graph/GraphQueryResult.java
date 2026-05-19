package cpt204.graph;

import java.util.Collections;
import java.util.List;

public final class GraphQueryResult {
    private final String caseName;
    private final String start;
    private final String destination;
    private final List<String> waypointsInOrder;
    private final boolean reachable;
    private final long totalCost;
    private final List<String> finalPath;

    public GraphQueryResult(
            String caseName,
            String start,
            String destination,
            List<String> waypointsInOrder,
            boolean reachable,
            long totalCost,
            List<String> finalPath
    ) {
        this.caseName = caseName;
        this.start = start;
        this.destination = destination;
        this.waypointsInOrder = waypointsInOrder;
        this.reachable = reachable;
        this.totalCost = totalCost;
        this.finalPath = finalPath;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getStart() {
        return start;
    }

    public String getDestination() {
        return destination;
    }

    public List<String> getWaypointsInOrder() {
        return Collections.unmodifiableList(waypointsInOrder);
    }

    public boolean isReachable() {
        return reachable;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public List<String> getFinalPath() {
        return Collections.unmodifiableList(finalPath);
    }

    public String formatPath() {
        if (!reachable) {
            return "UNREACHABLE";
        }
        return String.join(" -> ", finalPath);
    }
}
