package org.slozic.concurrency.lock;

import org.slozic.concurrency.JavaConcurrencyDemo;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ProducerWithLock implements Runnable {

    private Lock lock;
    private Condition condition;
    private Queue queue;

    public ProducerWithLock(final Lock lock, final Condition condition, final Queue queue) {
        this.lock = lock;
        this.condition = condition;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void produce() throws InterruptedException {
        lock.lock();
        if (queue.size() == 10) {
            condition.await();
            System.out.println("Producer awaiting: " + Thread.currentThread().getName());
        }
        JavaConcurrencyDemo.sleep(1000);
        System.out.println("Producing element: " + queue.size());
        queue.add(queue.size());
        if (queue.size() == 1) {
            condition.signalAll();
        }
        lock.unlock();
    }
}
