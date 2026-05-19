package cpt204.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSortStrategy implements SortStrategy {
    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public <T> void sort(List<T> data, Comparator<T> comparator) {
        if (data.size() <= 1) {
            return;
        }
        List<T> buffer = new ArrayList<>(data);
        mergeSort(data, buffer, comparator, 0, data.size() - 1);
    }

    private <T> void mergeSort(List<T> data, List<T> buffer, Comparator<T> comparator, int left, int right) {
        if (left >= right) {
            return;
        }

        // Split the list into two halves and sort them separately.
        int mid = left + (right - left) / 2;
        mergeSort(data, buffer, comparator, left, mid);
        mergeSort(data, buffer, comparator, mid + 1, right);
        merge(data, buffer, comparator, left, mid, right);
    }

    private <T> void merge(List<T> data, List<T> buffer, Comparator<T> comparator, int left, int mid, int right) {
        // Copy the current range so that merging will not overwrite unread values.
        for (int i = left; i <= right; i++) {
            buffer.set(i, data.get(i));
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            // Always take the smaller ranked item from the two sorted halves.
            if (comparator.compare(buffer.get(i), buffer.get(j)) <= 0) {
                data.set(k++, buffer.get(i++));
            } else {
                data.set(k++, buffer.get(j++));
            }
        }

        while (i <= mid) {
            data.set(k++, buffer.get(i++));
        }
        while (j <= right) {
            data.set(k++, buffer.get(j++));
        }
    }
}
