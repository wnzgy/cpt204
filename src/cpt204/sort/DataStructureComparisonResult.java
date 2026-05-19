package cpt204.sort;

public final class DataStructureComparisonResult {
    private final String datasetName;
    private final String listType;
    private final String algorithmName;
    private final int rowCount;
    private final long avgRuntimeNanos;

    public DataStructureComparisonResult(
            String datasetName,
            String listType,
            String algorithmName,
            int rowCount,
            long avgRuntimeNanos
    ) {
        this.datasetName = datasetName;
        this.listType = listType;
        this.algorithmName = algorithmName;
        this.rowCount = rowCount;
        this.avgRuntimeNanos = avgRuntimeNanos;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getListType() {
        return listType;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public long getAvgRuntimeNanos() {
        return avgRuntimeNanos;
    }

    public double getAvgRuntimeMillis() {
        return avgRuntimeNanos / 1_000_000.0;
    }
}
