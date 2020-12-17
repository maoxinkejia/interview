package com.zmx.study.interview.third.juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReEntryLockDemo {
    private static final Object lockA = new Object();
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println("===外层");
                lock.lock();
                try {
                    System.out.println("===内层");
                } finally {
                    lock.unlock();

                }
            } finally {
                lock.unlock();

            }
        }, "t2").start();
    }

    public static void syncBlock() {
        new Thread(() -> {
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + "\t--------外层调用");
                synchronized (lockA) {
                    System.out.println(Thread.currentThread().getName() + "\t--------中层调用");
                    synchronized (lockA) {
                        System.out.println(Thread.currentThread().getName() + "\t--------内层调用");
                    }
                }
            }
        }, "t1").start();
    }

    public synchronized void syncMethod1() {
        System.out.println(Thread.currentThread().getName() + "\t ====外层");
        syncMethod2();
    }

    public synchronized void syncMethod2() {
        System.out.println(Thread.currentThread().getName() + "\t ====中层");
        syncMethod3();
    }

    public synchronized void syncMethod3() {
        System.out.println(Thread.currentThread().getName() + "\t ====内层");
    }
}
