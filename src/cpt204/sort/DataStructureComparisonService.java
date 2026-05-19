package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataStructureComparisonService {
    private static final int RUNS = 3;

    public List<DataStructureComparisonResult> compareQuickSort(String datasetName, List<CandidateLocation> candidates) {
        List<CandidateLocation> fullDataset = new ArrayList<>(candidates);
        List<DataStructureComparisonResult> results = new ArrayList<>();
        QuickSortStrategy quickSort = new QuickSortStrategy();

        long arrayListTime = measureAverageTime(fullDataset, "ArrayList", quickSort);
        long linkedListTime = measureAverageTime(fullDataset, "LinkedList", quickSort);

        results.add(new DataStructureComparisonResult(datasetName, "ArrayList", quickSort.getName(), fullDataset.size(), arrayListTime));
        results.add(new DataStructureComparisonResult(datasetName, "LinkedList", quickSort.getName(), fullDataset.size(), linkedListTime));
        return results;
    }

    private long measureAverageTime(List<CandidateLocation> originalData, String listType, QuickSortStrategy quickSort) {
        long totalTime = 0L;
        List<CandidateLocation> firstSortedResult = null;

        for (int run = 0; run < RUNS; run++) {
            List<CandidateLocation> data = createListByType(originalData, listType);
            long start = System.nanoTime();
            quickSort.sort(data, CandidateLocation.RANKING_RULE);
            long end = System.nanoTime();
            totalTime += end - start;

            if (firstSortedResult == null) {
                firstSortedResult = new ArrayList<>(data);
            } else if (!firstSortedResult.equals(data)) {
                throw new IllegalStateException("Data structure comparison produced different sorted results.");
            }
        }
        return totalTime / RUNS;
    }

    private List<CandidateLocation> createListByType(List<CandidateLocation> originalData, String listType) {
        if ("LinkedList".equals(listType)) {
            return new LinkedList<>(originalData);
        }
        return new ArrayList<>(originalData);
    }
}
