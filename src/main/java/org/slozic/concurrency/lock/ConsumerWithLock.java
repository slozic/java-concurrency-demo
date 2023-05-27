package org.slozic.concurrency.lock;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConsumerWithLock implements Runnable {

    private Lock lock;
    private Condition condition;
    private Queue queue;

    public ConsumerWithLock(final Lock lock, final Condition condition, final Queue queue) {
        this.lock = lock;
        this.condition = condition;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void consume() throws InterruptedException {
        lock.lock();
        if (queue.isEmpty()) {
            lock.unlock();
            return;
            //condition.await();
        }
        final Integer elementConsumed = (Integer) queue.remove();
        System.out.println(String.format("Consumed element %s by consumer %s ", elementConsumed, Thread.currentThread().getName()));
        if (queue.size() == 9) {
            condition.signalAll();
        }
        lock.unlock();
    }
}
