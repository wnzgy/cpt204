package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortingService {
    private final List<SortStrategy> strategies;
    private final int numberOfSelectedLocations;
    private final int benchmarkRuns;

    public SortingService(List<SortStrategy> strategies, int numberOfSelectedLocations, int benchmarkRuns) {
        this.strategies = strategies;
        this.numberOfSelectedLocations = numberOfSelectedLocations;
        this.benchmarkRuns = benchmarkRuns;
    }

    public DatasetSortingResult evaluateDataset(String datasetName, List<CandidateLocation> originalCandidates) {
        DatasetProfile profile = DatasetProfile.from(originalCandidates);
        Map<String, Long> averageTimeMap = new LinkedHashMap<>();
        List<CandidateLocation> firstSortedResult = null;

        for (SortStrategy strategy : strategies) {
            long totalTime = 0L;
            List<CandidateLocation> currentSortedResult = null;

            for (int run = 0; run < benchmarkRuns; run++) {
                List<CandidateLocation> candidatesForThisRun = new ArrayList<>(originalCandidates);
                long start = System.nanoTime();
                strategy.sort(candidatesForThisRun, CandidateLocation.RANKING_RULE);
                long end = System.nanoTime();
                totalTime += (end - start);
                currentSortedResult = candidatesForThisRun;
            }

            if (currentSortedResult == null) {
                throw new IllegalStateException("No sorted result produced for " + strategy.getName());
            }

            if (firstSortedResult == null) {
                firstSortedResult = currentSortedResult;
            } else if (!firstSortedResult.equals(currentSortedResult)) {
                throw new IllegalStateException(
                        "Sorting mismatch detected between algorithms in " + datasetName + "."
                );
            }

            long averageTime = totalTime / benchmarkRuns;
            averageTimeMap.put(strategy.getName(), averageTime);
        }

        if (firstSortedResult == null) {
            throw new IllegalStateException("No sorting result produced for " + datasetName);
        }

        List<CandidateLocation> selectedLocations = new ArrayList<>();
        int limit = Math.min(numberOfSelectedLocations, firstSortedResult.size());
        for (int i = 0; i < limit; i++) {
            selectedLocations.add(firstSortedResult.get(i));
        }

        return new DatasetSortingResult(datasetName, profile, averageTimeMap, selectedLocations);
    }
}
