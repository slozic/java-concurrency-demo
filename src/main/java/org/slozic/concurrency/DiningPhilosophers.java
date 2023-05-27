package org.slozic.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers implements Runnable {

    private List<Lock> forks = new ArrayList<>() {{
        add(new ReentrantLock());
        add(new ReentrantLock());
        add(new ReentrantLock());
        add(new ReentrantLock());
        add(new ReentrantLock());
    }};

    private Semaphore semaphore = new Semaphore(4);

    @Override
    public void run() {
        Thread p0 = new Thread(new Philosopher(0));
        Thread p1 = new Thread(new Philosopher(1));
        Thread p2 = new Thread(new Philosopher(2));
        Thread p3 = new Thread(new Philosopher(3));
        Thread p4 = new Thread(new Philosopher(4));
        p0.start();
        p1.start();
        p2.start();
        p3.start();
        p4.start();
    }

    private class Philosopher implements Runnable {
        private int id;

        private Philosopher(final int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                think();
                pickForks();
                eat();
                putForks();
            }
        }

        private void think() {
            System.out.println("Philosopher: " + this.id + " thinks.");
        }

        private void pickForks() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Philosopher: " + id + " tries to locks forks: " + id + " " + ((id + 1) % 5));
            forks.get(id).lock();
            forks.get((id + 1) % 5).lock();
        }

        private void eat() {
            System.out.println("Philosopher: " + this.id + " eats.");
        }

        private void putForks() {
            forks.get(id).unlock();
            forks.get((id + 1) % 5).unlock();
            semaphore.release();
        }
    }
}



    /*public static volatile boolean[] forks = new boolean[]{true, true, true, true, true};

    @Override
    public void run() {
        Lock lock = new ReentrantLock();
        Thread p0 = new Thread(new Philosopher(lock, 0, 1, 0));
        Thread p1 = new Thread(new Philosopher(lock, 1, 2, 1));
        Thread p2 = new Thread(new Philosopher(lock, 2, 3, 2));
        Thread p3 = new Thread(new Philosopher(lock, 3, 4, 3));
        Thread p4 = new Thread(new Philosopher(lock, 4, 0, 4));
        p0.start();
        p1.start();
        p2.start();
        p3.start();
        p4.start();
    }

    static class Philosopher implements Runnable {
        private Lock lock;
        private int id;
        private int left, right;

        public Philosopher(final Lock lock, final int id, final int left, final int right) {
            this.lock = lock;
            this.id = id;
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            while (true) {
                think();
                final boolean hasForks = pickUpForks();
                if (hasForks) {
                    eat();
                    putForksBackOnTable();
                }
            }
        }

        void putForksBackOnTable() {
            forks[left] = true;
            forks[right] = true;
            System.out.println("Philosopher: " + this.id + " put forks back.");
        }

        private void eat() {
            System.out.println("Philosopher: " + this.id + " eats.");
        }

        boolean pickUpForks() {
            boolean hasForks = false;
            lock.lock();
            if (forks[left] && forks[right]) {
                forks[left] = false;
                forks[right] = false;
                hasForks = true;
                System.out.println("Philosopher: " + this.id + " got forks.");
            }
            {
                System.out.println("Philosopher: " + this.id + " goes back to think as forks are occupied.");
            }
            lock.unlock();
            return hasForks;
        }

        void think() {
            System.out.println("Philosopher: " + this.id + " thinks.");
        }
    }
}*/
