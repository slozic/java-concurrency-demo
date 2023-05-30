package org.slozic.concurrency;

import org.slozic.concurrency.lock.ConsumerWithLock;
import org.slozic.concurrency.lock.ProducerWithLock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JavaConcurrencyDemo {

    public static Queue<Integer> queue = new LinkedList<>();
    public static Lock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();


    public static void main(String[] args) throws InterruptedException {
        new Thread(new SleepingBarberDemo()).start();
        //new Thread(new ReaderWriterSynchronisation()).start();
        //new Thread(new DiningPhilosophers()).start();
        //quickSort();
        //new Thread(new MapReduceDemo("a friend in need is a friend indeed")).start();
        //new Thread(new VolatileDemo()).start();
        //setupDirectoryWorkers();
        //setupAndStartConsumersAndProducers();
    }

    private static void quickSort() throws InterruptedException {
        final Thread thread = new Thread(new QuickSort(new int[]{5, 1, 10, 3, 20, 18, 1, 15, 5}));
        thread.start();
        thread.join();
    }

    private static void setupDirectoryWorkers() {
        Thread directoryWatcher = new Thread(new DirectoryWatcher());
        directoryWatcher.start();
    }

    private static void setupAndStartConsumersAndProducers() {
        Thread producer = new Thread(new ProducerWithLock(lock, condition, queue));
        Thread consumer = new Thread(new ConsumerWithLock(lock, condition, queue));
        Thread consumer2 = new Thread(new ConsumerWithLock(lock, condition, queue));
        Thread consumer3 = new Thread(new ConsumerWithLock(lock, condition, queue));
        consumer.setPriority(Thread.MAX_PRIORITY);
        consumer2.setPriority(Thread.MAX_PRIORITY);
        consumer3.setPriority(Thread.MAX_PRIORITY);
        producer.setPriority(Thread.MIN_PRIORITY);
        producer.start();
        consumer.start();
        consumer2.start();
        consumer3.start();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
