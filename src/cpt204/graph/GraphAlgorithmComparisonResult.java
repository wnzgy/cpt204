package cpt204.graph;

public final class GraphAlgorithmComparisonResult {
    private final String algorithmName;
    private final GraphQueryResult queryResult;
    private final long runtimeNanos;

    public GraphAlgorithmComparisonResult(String algorithmName, GraphQueryResult queryResult, long runtimeNanos) {
        this.algorithmName = algorithmName;
        this.queryResult = queryResult;
        this.runtimeNanos = runtimeNanos;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public GraphQueryResult getQueryResult() {
        return queryResult;
    }

    public long getRuntimeNanos() {
        return runtimeNanos;
    }

    public double getRuntimeMillis() {
        return runtimeNanos / 1_000_000.0;
    }
}
