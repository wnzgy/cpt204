package cpt204.sort;

public final class SortingOperationCountResult {
    private final String datasetName;
    private final String algorithmName;
    private final int rowCount;
    private final long comparisons;
    private final long swaps;
    private final long writes;
    private final long passes;
    private final long partitions;
    private final long merges;
    private final long recursiveCalls;

    public SortingOperationCountResult(
            String datasetName,
            String algorithmName,
            int rowCount,
            long comparisons,
            long swaps,
            long writes,
            long passes,
            long partitions,
            long merges,
            long recursiveCalls
    ) {
        this.datasetName = datasetName;
        this.algorithmName = algorithmName;
        this.rowCount = rowCount;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.writes = writes;
        this.passes = passes;
        this.partitions = partitions;
        this.merges = merges;
        this.recursiveCalls = recursiveCalls;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getWrites() {
        return writes;
    }

    public long getPasses() {
        return passes;
    }

    public long getPartitions() {
        return partitions;
    }

    public long getMerges() {
        return merges;
    }

    public long getRecursiveCalls() {
        return recursiveCalls;
    }
}
