package cpt204.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BubbleSortStrategy implements SortStrategy {
    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public <T> void sort(List<T> data, Comparator<T> comparator) {
        int n = data.size();

        // Repeatedly move the largest remaining element to the back.
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (comparator.compare(data.get(j), data.get(j + 1)) > 0) {
                    Collections.swap(data, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                // If no swap happens, the list is already sorted.
                break;
            }
        }
    }
}
