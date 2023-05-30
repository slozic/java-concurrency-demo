package org.slozic.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SleepingBarberDemo implements Runnable {

    private int customerCount = 0;
    private int MAX_CUSTOMERS_WAITING = 5;
    private Semaphore haircutStart = new Semaphore(0);
    private Semaphore haircutDone = new Semaphore(0);
    private Semaphore payForTheService = new Semaphore(0);
    private Lock barberShop = new ReentrantLock();

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            new Thread(new Customer()).start();
        }
        new Thread(new Barber()).start();
    }

    class Barber implements Runnable {

        @Override
        public void run() {
            while (true) {
                // sleep
                // do_haircut (only one customer at the time)
                if (customerCount > 0) {
                    haircutStart.release();
                    JavaConcurrencyDemo.sleep(2000);
                    haircutDone.release();
                    acquire(payForTheService);
                }
            }
        }
    }

    class Customer implements Runnable {

        @Override
        public void run() {
            // check waiting room (get in or leave)
            // get in queue
            // get haircut
            barberShop.lock();
            if (customerCount > MAX_CUSTOMERS_WAITING) {
                System.out.println("Customer left as waiting room is full: " + Thread.currentThread().getName());
                JavaConcurrencyDemo.sleep(500);
                barberShop.unlock();
                return;
            }
            customerCount++;
            barberShop.unlock();

            // virtual queue
            acquire(haircutStart);
            JavaConcurrencyDemo.sleep(500);
            System.out.println("Customer sat and is getting the haircut: " + Thread.currentThread().getName());
            acquire(haircutDone);

            payForTheService.release();

            barberShop.lock();
            JavaConcurrencyDemo.sleep(500);
            System.out.println("Customer got the haircut and leaves the shop: " + Thread.currentThread().getName());
            customerCount--;
            barberShop.unlock();
        }
    }

    private void acquire(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
