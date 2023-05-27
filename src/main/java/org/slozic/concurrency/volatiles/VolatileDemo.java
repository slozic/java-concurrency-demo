package org.slozic.concurrency.volatiles;

import org.slozic.concurrency.JavaConcurrencyDemo;

public class VolatileDemo implements Runnable {

    private volatile int counter = 0;

    @Override
    public void run() {

        Thread thread = new Thread(() -> {
            int localCounter = counter;
            while (localCounter < 10) {
                if (localCounter != counter) {
                    System.out.println("Counter value changed to: " + counter);
                    localCounter = counter;
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            while (counter < 10) {
                System.out.println("Increasing the counter value to: " + (counter + 1));
                counter = counter + 1;
                JavaConcurrencyDemo.sleep(500);
            }
        });

        thread.start();
        thread2.start();
    }
}
