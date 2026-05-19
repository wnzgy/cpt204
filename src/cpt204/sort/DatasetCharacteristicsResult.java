package cpt204.sort;

public final class DatasetCharacteristicsResult {
    private final String datasetName;
    private final String dataCharacteristic;
    private final int totalRows;
    private final long totalInversions;
    private final int bubblePasses;
    private final long bubbleSwaps;
    private final String pivotId;
    private final int pivotRank;
    private final int firstPartitionLeft;
    private final int firstPartitionRight;
    private final String pivotQuality;

    public DatasetCharacteristicsResult(
            String datasetName,
            String dataCharacteristic,
            int totalRows,
            long totalInversions,
            int bubblePasses,
            long bubbleSwaps,
            String pivotId,
            int pivotRank,
            int firstPartitionLeft,
            int firstPartitionRight,
            String pivotQuality
    ) {
        this.datasetName = datasetName;
        this.dataCharacteristic = dataCharacteristic;
        this.totalRows = totalRows;
        this.totalInversions = totalInversions;
        this.bubblePasses = bubblePasses;
        this.bubbleSwaps = bubbleSwaps;
        this.pivotId = pivotId;
        this.pivotRank = pivotRank;
        this.firstPartitionLeft = firstPartitionLeft;
        this.firstPartitionRight = firstPartitionRight;
        this.pivotQuality = pivotQuality;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getDataCharacteristic() {
        return dataCharacteristic;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public long getTotalInversions() {
        return totalInversions;
    }

    public int getBubblePasses() {
        return bubblePasses;
    }

    public long getBubbleSwaps() {
        return bubbleSwaps;
    }

    public String getPivotId() {
        return pivotId;
    }

    public int getPivotRank() {
        return pivotRank;
    }

    public int getFirstPartitionLeft() {
        return firstPartitionLeft;
    }

    public int getFirstPartitionRight() {
        return firstPartitionRight;
    }

    public String getPivotQuality() {
        return pivotQuality;
    }
}
