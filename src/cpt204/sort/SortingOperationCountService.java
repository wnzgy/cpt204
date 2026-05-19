package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingOperationCountService {
    public List<SortingOperationCountResult> countOperations(
            String datasetName,
            List<CandidateLocation> originalCandidates
    ) {
        List<SortingOperationCountResult> results = new ArrayList<>();
        results.add(countBubbleSort(datasetName, originalCandidates));
        results.add(countQuickSort(datasetName, originalCandidates));
        results.add(countMergeSort(datasetName, originalCandidates));
        return results;
    }

    private SortingOperationCountResult countBubbleSort(
            String datasetName,
            List<CandidateLocation> originalCandidates
    ) {
        List<CandidateLocation> data = new ArrayList<>(originalCandidates);
        Counter counter = new Counter();
        int n = data.size();

        for (int i = 0; i < n - 1; i++) {
            counter.passes++;
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                counter.comparisons++;
                if (CandidateLocation.RANKING_RULE.compare(data.get(j), data.get(j + 1)) > 0) {
                    Collections.swap(data, j, j + 1);
                    counter.swaps++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        return counter.toSortingResult(datasetName, "Bubble Sort", n);
    }

    private SortingOperationCountResult countQuickSort(
            String datasetName,
            List<CandidateLocation> originalCandidates
    ) {
        List<CandidateLocation> data = new ArrayList<>(originalCandidates);
        Counter counter = new Counter();
        quickSort(data, CandidateLocation.RANKING_RULE, 0, data.size() - 1, counter);
        return counter.toSortingResult(datasetName, "Quick Sort", data.size());
    }

    private void quickSort(
            List<CandidateLocation> data,
            Comparator<CandidateLocation> comparator,
            int low,
            int high,
            Counter counter
    ) {
        counter.recursiveCalls++;
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(data, comparator, low, high, counter);
        quickSort(data, comparator, low, pivotIndex - 1, counter);
        quickSort(data, comparator, pivotIndex + 1, high, counter);
    }

    private int partition(
            List<CandidateLocation> data,
            Comparator<CandidateLocation> comparator,
            int low,
            int high,
            Counter counter
    ) {
        counter.partitions++;
        CandidateLocation pivot = data.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            counter.comparisons++;
            if (comparator.compare(data.get(j), pivot) <= 0) {
                i++;
                Collections.swap(data, i, j);
                counter.swaps++;
            }
        }

        Collections.swap(data, i + 1, high);
        counter.swaps++;
        return i + 1;
    }

    private SortingOperationCountResult countMergeSort(
            String datasetName,
            List<CandidateLocation> originalCandidates
    ) {
        List<CandidateLocation> data = new ArrayList<>(originalCandidates);
        List<CandidateLocation> buffer = new ArrayList<>(data);
        Counter counter = new Counter();
        mergeSort(data, buffer, CandidateLocation.RANKING_RULE, 0, data.size() - 1, counter);
        return counter.toSortingResult(datasetName, "Merge Sort", data.size());
    }

    private void mergeSort(
            List<CandidateLocation> data,
            List<CandidateLocation> buffer,
            Comparator<CandidateLocation> comparator,
            int left,
            int right,
            Counter counter
    ) {
        counter.recursiveCalls++;
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        mergeSort(data, buffer, comparator, left, mid, counter);
        mergeSort(data, buffer, comparator, mid + 1, right, counter);
        merge(data, buffer, comparator, left, mid, right, counter);
    }

    private void merge(
            List<CandidateLocation> data,
            List<CandidateLocation> buffer,
            Comparator<CandidateLocation> comparator,
            int left,
            int mid,
            int right,
            Counter counter
    ) {
        counter.merges++;
        for (int i = left; i <= right; i++) {
            buffer.set(i, data.get(i));
            counter.writes++;
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            counter.comparisons++;
            if (comparator.compare(buffer.get(i), buffer.get(j)) <= 0) {
                data.set(k++, buffer.get(i++));
            } else {
                data.set(k++, buffer.get(j++));
            }
            counter.writes++;
        }

        while (i <= mid) {
            data.set(k++, buffer.get(i++));
            counter.writes++;
        }
        while (j <= right) {
            data.set(k++, buffer.get(j++));
            counter.writes++;
        }
    }

    private static class Counter {
        private long comparisons;
        private long swaps;
        private long writes;
        private long passes;
        private long partitions;
        private long merges;
        private long recursiveCalls;

        private SortingOperationCountResult toSortingResult(String datasetName, String algorithmName, int rowCount) {
            return new SortingOperationCountResult(
                    datasetName,
                    algorithmName,
                    rowCount,
                    comparisons,
                    swaps,
                    writes,
                    passes,
                    partitions,
                    merges,
                    recursiveCalls
            );
        }
    }
}
