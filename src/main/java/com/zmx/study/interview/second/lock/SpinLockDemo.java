package com.zmx.study.interview.second.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 手写自旋锁
 */
public class SpinLockDemo {
    private AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public static void main(String[] args) throws InterruptedException {
        SpinLockDemo spinLockDemo = new SpinLockDemo();

        new Thread(() -> {
            spinLockDemo.myLock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spinLockDemo.myUnLock();
        }, "AA").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            spinLockDemo.myLock();
            spinLockDemo.myUnLock();
        }, "BB").start();
    }

    private void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "\t lock come in...");

        while (!atomicReference.compareAndSet(null, thread)) {

        }
        System.out.println(thread.getName() + "\t lock success");
    }

    private void myUnLock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + "\t unLock come in ...");
        atomicReference.compareAndSet(thread, null);

        System.out.println(thread.getName() + "\t unLock success");
    }
}
