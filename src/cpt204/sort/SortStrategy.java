package cpt204.sort;

import java.util.Comparator;
import java.util.List;

public interface SortStrategy {
    String getName();

    <T> void sort(List<T> data, Comparator<T> comparator);
}
