package org.slozic.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class MapReduceDemo implements Runnable {

    private final String input;
    private static final List<Map.Entry<String, Integer>> intermediateList = Collections.synchronizedList(new ArrayList<>());
    private static List<List<Map.Entry<String, Integer>>> reduceList;
    private static Map<String, Integer> finalResult;
    private static final CountDownLatch latch = new CountDownLatch(2);
    private static final Semaphore semaphore = new Semaphore(2);
    private static final Phaser phaser = new Phaser(1);

    public MapReduceDemo(final String input) {
        this.input = input;
    }

    public void run() {
        final List<String> inputAsList = Arrays.asList(input.split(" "));
        Thread mapper1 = new Thread(new Mapper(inputAsList.subList(0, inputAsList.size() / 2)));
        Thread mapper2 = new Thread(new Mapper(inputAsList.subList(inputAsList.size() / 2, inputAsList.size())));
        mapper1.start();
        mapper2.start();
        phaser.arriveAndAwaitAdvance();

        Thread partitioner = new Thread(new Partitioner());
        partitioner.start();
        phaser.arriveAndAwaitAdvance();

        Thread reducer = new Thread(new Reducer());
        reducer.start();
        phaser.arriveAndAwaitAdvance();
        /*try {
            partitioner.join();
            Thread reducer = new Thread(new Reducer());
            reducer.start();
            reducer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        System.out.println(finalResult);
    }

    static class Mapper implements Runnable {
        private List<String> inputList;

        Mapper(List<String> inputList) {
            this.inputList = inputList;
            phaser.register();
        }

        public void run() {
            //semaphore.acquire();
            inputList.forEach(input -> {
                intermediateList.add(Map.entry(input, 1));
            });
            System.out.println("Thread: " + Thread.currentThread().getName() + " finished mapping!");
            System.out.println("Phaser phase: " + phaser.getPhase());
            phaser.arriveAndDeregister();
            //semaphore.release();
            //latch.countDown();
        }
    }

    static class Partitioner implements Runnable {

        public Partitioner() {
            phaser.register();
        }

        @Override
        public void run() {
            /*while (true) {
                if (semaphore.tryAcquire(2)) {
                    break;
                }
            }*/
            reduceList = new ArrayList<>(intermediateList.stream()
                                                         .collect(Collectors.groupingBy(Map.Entry::getKey))
                                                         .values());
            System.out.println("Thread: " + Thread.currentThread().getName() + " finished partitioning!");
            System.out.println("Phaser phase: " + phaser.getPhase());
            phaser.arriveAndDeregister();
            //semaphore.release();
        }
    }

    static class Reducer implements Runnable {

        public Reducer() {
            phaser.register();
        }

        @Override
        public void run() {
            finalResult = reduceList.stream()
                                    .flatMap(List::stream)
                                    .collect(Collectors.groupingBy(
                                            Map.Entry::getKey,
                                            Collectors.summingInt(Map.Entry::getValue)
                                    ));
            System.out.println("Thread: " + Thread.currentThread().getName() + " finished reducing!");
            System.out.println("Phaser phase: " + phaser.getPhase());
            phaser.arriveAndDeregister();
        }
    }
}
