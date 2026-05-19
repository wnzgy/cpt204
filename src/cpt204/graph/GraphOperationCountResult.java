package cpt204.graph;

public final class GraphOperationCountResult {
    private final String algorithmName;
    private final String caseName;
    private final int segmentCount;
    private final boolean reachable;
    private final long totalCost;
    private final long queuePolls;
    private final long stalePolls;
    private final long queuePushes;
    private final long edgeChecks;
    private final long successfulRelaxations;
    private final long visitedNodes;

    public GraphOperationCountResult(
            String algorithmName,
            String caseName,
            int segmentCount,
            boolean reachable,
            long totalCost,
            long queuePolls,
            long stalePolls,
            long queuePushes,
            long edgeChecks,
            long successfulRelaxations,
            long visitedNodes
    ) {
        this.algorithmName = algorithmName;
        this.caseName = caseName;
        this.segmentCount = segmentCount;
        this.reachable = reachable;
        this.totalCost = totalCost;
        this.queuePolls = queuePolls;
        this.stalePolls = stalePolls;
        this.queuePushes = queuePushes;
        this.edgeChecks = edgeChecks;
        this.successfulRelaxations = successfulRelaxations;
        this.visitedNodes = visitedNodes;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getCaseName() {
        return caseName;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public boolean isReachable() {
        return reachable;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public long getQueuePolls() {
        return queuePolls;
    }

    public long getStalePolls() {
        return stalePolls;
    }

    public long getQueuePushes() {
        return queuePushes;
    }

    public long getEdgeChecks() {
        return edgeChecks;
    }

    public long getSuccessfulRelaxations() {
        return successfulRelaxations;
    }

    public long getVisitedNodes() {
        return visitedNodes;
    }
}
