package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatasetCharacteristicsService {
    public DatasetCharacteristicsResult analyze(String datasetName, List<CandidateLocation> candidates) {
        long totalInversions = countTotalInversions(candidates);
        BubbleCheckResult bubbleCheckResult = simulateBubbleSort(candidates);
        PivotCheckResult pivotCheckResult = checkLastPivot(candidates);
        String dataCharacteristic = describeDataset(candidates, totalInversions);

        return new DatasetCharacteristicsResult(
                datasetName,
                dataCharacteristic,
                candidates.size(),
                totalInversions,
                bubbleCheckResult.getPasses(),
                bubbleCheckResult.getSwaps(),
                pivotCheckResult.getPivotId(),
                pivotCheckResult.getPivotRank(),
                pivotCheckResult.getLeftCount(),
                pivotCheckResult.getRightCount(),
                pivotCheckResult.getQuality()
        );
    }

    private long countTotalInversions(List<CandidateLocation> candidates) {
        long inversions = 0L;
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                if (CandidateLocation.RANKING_RULE.compare(candidates.get(i), candidates.get(j)) > 0) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    private BubbleCheckResult simulateBubbleSort(List<CandidateLocation> candidates) {
        List<CandidateLocation> data = new ArrayList<>(candidates);
        int passes = 0;
        long swaps = 0L;

        // This follows BubbleSortStrategy so the analysis matches the real code.
        for (int i = 0; i < data.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < data.size() - 1 - i; j++) {
                if (CandidateLocation.RANKING_RULE.compare(data.get(j), data.get(j + 1)) > 0) {
                    Collections.swap(data, j, j + 1);
                    swaps++;
                    swapped = true;
                }
            }
            passes++;
            if (!swapped) {
                break;
            }
        }
        return new BubbleCheckResult(passes, swaps);
    }

    private PivotCheckResult checkLastPivot(List<CandidateLocation> candidates) {
        CandidateLocation pivot = candidates.get(candidates.size() - 1);
        int leftCount = 0;
        int rightCount = 0;

        // QuickSortStrategy uses the last element as pivot.
        for (int i = 0; i < candidates.size() - 1; i++) {
            int order = CandidateLocation.RANKING_RULE.compare(candidates.get(i), pivot);
            if (order <= 0) {
                leftCount++;
            } else {
                rightCount++;
            }
        }

        int pivotRank = leftCount + 1;
        String quality = describePivotQuality(leftCount, rightCount);
        return new PivotCheckResult(pivot.getLocationId(), pivotRank, leftCount, rightCount, quality);
    }

    private String describeDataset(List<CandidateLocation> candidates, long totalInversions) {
        int uniqueScores = DatasetProfile.from(candidates).getUniqueScoreCount();
        if (totalInversions <= 100) {
            return "Nearly sorted";
        }
        if (uniqueScores < candidates.size() / 2) {
            return "Scores sorted and IDs shuffled within tied-score groups";
        }
        return "Random order";
    }

    private String describePivotQuality(int leftCount, int rightCount) {
        int smallerSide = Math.min(leftCount, rightCount);
        int largerSide = Math.max(leftCount, rightCount);
        if (smallerSide == 0 || largerSide > smallerSide * 10) {
            return "Highly unbalanced";
        }
        if (largerSide > smallerSide * 3) {
            return "Unbalanced";
        }
        return "Relatively balanced";
    }

    private static class BubbleCheckResult {
        private final int passes;
        private final long swaps;

        private BubbleCheckResult(int passes, long swaps) {
            this.passes = passes;
            this.swaps = swaps;
        }

        private int getPasses() {
            return passes;
        }

        private long getSwaps() {
            return swaps;
        }
    }

    private static class PivotCheckResult {
        private final String pivotId;
        private final int pivotRank;
        private final int leftCount;
        private final int rightCount;
        private final String quality;

        private PivotCheckResult(String pivotId, int pivotRank, int leftCount, int rightCount, String quality) {
            this.pivotId = pivotId;
            this.pivotRank = pivotRank;
            this.leftCount = leftCount;
            this.rightCount = rightCount;
            this.quality = quality;
        }

        private String getPivotId() {
            return pivotId;
        }

        private int getPivotRank() {
            return pivotRank;
        }

        private int getLeftCount() {
            return leftCount;
        }

        private int getRightCount() {
            return rightCount;
        }

        private String getQuality() {
            return quality;
        }
    }
}
