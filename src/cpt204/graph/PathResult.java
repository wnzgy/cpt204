package cpt204.graph;

import java.util.Collections;
import java.util.List;

public final class PathResult {
    private final boolean reachable;
    private final long totalCost;
    private final List<String> path;

    public PathResult(boolean reachable, long totalCost, List<String> path) {
        this.reachable = reachable;
        this.totalCost = totalCost;
        this.path = path;
    }

    public boolean isReachable() {
        return reachable;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public List<String> getPath() {
        return Collections.unmodifiableList(path);
    }
}
