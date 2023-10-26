package it.uniba.dib.sms222334.Utils;

import java.util.List;

public final class SortingAlgo {
    private static <T> void swap(List<T> arr, int i, int j){
        T temp = arr.get(i);
        arr.set(i,arr.get(j));
        arr.set(j,temp);
    }

    private static <T extends Comparable<T>> int partition(List<T> arr, int low, int high) {
        T pivot = arr.get(arr.size()-1);

        int i = (low - 1);

        for (int j = low; j <= high - 1; j++) {
            if (arr.get(j).compareTo(pivot) > 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    public static <T extends Comparable<T>> void quickSort(List<T> arr, int low, int high){
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
}
