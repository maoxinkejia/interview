package com.zmx.study.interview.second.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadPrint {
    public static void main(String[] args) {
        Resource resource = new Resource();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                resource.print5();
            }
        }, "AA").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                resource.print10();
            }
        }, "BB").start();
        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                resource.print20();
            }
        }, "CC").start();
    }


    private static class Resource {
        private final Integer PRINT_5 = 0;
        private final Integer PRINT_10 = 1;
        private final Integer PRINT_20 = 2;
        private Lock lock = new ReentrantLock();
        private Condition c1 = lock.newCondition();
        private Condition c2 = lock.newCondition();
        private Condition c3 = lock.newCondition();
        volatile int flag = 0;

        private void print5() {
            lock.lock();
            try {
                while (flag != PRINT_5) {
                    c1.await();
                }

                for (int i = 1; i <= 5; i++) {
                    System.out.println(Thread.currentThread().getName() + "\t" + i);
                }
                flag = PRINT_10;
                c2.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        private void print10() {
            lock.lock();
            try {
                while (flag != PRINT_10) {
                    c2.await();
                }

                for (int i = 1; i <= 10; i++) {
                    System.out.println(Thread.currentThread().getName() + "\t" + i);
                }
                flag = PRINT_20;
                c3.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        private void print20() {
            lock.lock();
            try {
                while (flag != PRINT_20) {
                    c3.await();
                }

                for (int i = 1; i <= 20; i++) {
                    System.out.println(Thread.currentThread().getName() + "\t" + i);
                }
                flag = PRINT_5;
                c1.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}

