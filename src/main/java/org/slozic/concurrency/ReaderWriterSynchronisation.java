package org.slozic.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterSynchronisation implements Runnable {

    private int counter = 0;
    private int readers = 0;
    private Semaphore semaphore = new Semaphore(1);
    private Lock readerLock = new ReentrantLock();

    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            new Thread(new Reader()).start();
        }

        for (int i = 0; i < 2; i++) {
            new Thread(new Writer()).start();
        }

    }

    class Reader implements Runnable {

        @Override
        public void run() {
            while (true) {
                readerLock.lock();
                readers++;
                if (readers == 1) {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                readerLock.unlock();
                // Threads are "inside the semaphore" at this point and writers are blocked
                System.out.println("Read the counter: " + counter);

                readerLock.lock();
                readers--;
                if (readers == 0) {
                    semaphore.release();
                }
                readerLock.unlock();
                JavaConcurrencyDemo.sleep(2000);
            }
        }
    }

    class Writer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Incremented the counter: " + ++counter);
                semaphore.release();
                JavaConcurrencyDemo.sleep(2000);
            }
        }
    }
}
