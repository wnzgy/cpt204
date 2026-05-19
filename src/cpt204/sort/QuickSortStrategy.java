package cpt204.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuickSortStrategy implements SortStrategy {
    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public <T> void sort(List<T> data, Comparator<T> comparator) {
        quickSort(data, comparator, 0, data.size() - 1);
    }

    private <T> void quickSort(List<T> data, Comparator<T> comparator, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(data, comparator, low, high);
        quickSort(data, comparator, low, pivotIndex - 1);
        quickSort(data, comparator, pivotIndex + 1, high);
    }

    private <T> int partition(List<T> data, Comparator<T> comparator, int low, int high) {
        T pivot = data.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(data.get(j), pivot) <= 0) {
                i++;
                Collections.swap(data, i, j);
            }
        }
        Collections.swap(data, i + 1, high);
        return i + 1;
    }
}
