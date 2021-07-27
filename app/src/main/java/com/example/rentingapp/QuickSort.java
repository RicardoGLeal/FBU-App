package com.example.rentingapp;

import com.example.rentingapp.Models.Item;

import java.util.Collections;
import java.util.List;

// Class that implements the QuickSort algorithm.
public class QuickSort {
    private String property;

    public QuickSort(String property) {
        this.property = property;
        }

    /**
     * This function takes last element as pivot, places the pivot element at its correct
     * position in sorted array, and places all smaller (smaller than pivot) to left of
     * pivot and all greater elements to right of pivot
     * @param arr Array to be sorted
     * @param low smallest pivot
     * @param high highest pivot
     * @return
     */
    int partition(List<Item> arr, int low, int high) {
        double pivot = getValue(arr.get(high));
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (getValue(arr.get(j)) <= pivot) {
                i++;

                // swap arr[i] and arr[j]
                Collections.swap(arr, i, j);
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        Collections.swap(arr, i+1, high);
        return i + 1;
    }

    private double getValue(Item item) {
        switch (property) {
            case "price":
                return item.getPrice();
            case "distance":
                return item.getDistance();
        }
        return 0;
    }

    /**
     * The main function that implements QuickSort()
     * @param arr Array to be sorted
     * @param low Starting index
     * @param high Ending index
     */
    public void sort(List<Item> arr, int low, int high) {
        if (low < high) {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }
}