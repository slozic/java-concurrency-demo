package org.slozic.concurrency;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class QuickSort implements Runnable {

    private int[] array;

    public QuickSort(final int[] array) {
        this.array = array;
    }

    @Override
    public void run() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        final ForkJoinTask<Void> forkJoinTask = forkJoinPool.submit(new QuickSortTask(0, array.length - 1));
        try {
            forkJoinTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //quicksort(0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }

    private void quicksort(int left, int right) {
        if (left < right) {
            int mid = partition(left, right);
            quicksort(left, mid - 1);
            quicksort(mid + 1, right);
        }
    }

    private int partition(final int left, final int right) {
        int pivot = array[right];
        int swapIndex = left - 1;
        for (int i = left; i < right; i++) {
            if (array[i] < pivot) {
                swapIndex++;
                int temp = array[swapIndex];
                array[swapIndex] = array[i];
                array[i] = temp;
            }
        }
        swapIndex++;
        int temp = array[swapIndex];
        array[swapIndex] = array[right];
        array[right] = temp;
        return swapIndex;
    }

    class QuickSortTask extends RecursiveAction {
        int left;
        int right;

        public QuickSortTask(final int left, final int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left < right) {
                int mid = partition(left, right);
                invokeAll(new QuickSortTask(left, mid - 1), new QuickSortTask(mid + 1, right));
            }
        }
    }
}
