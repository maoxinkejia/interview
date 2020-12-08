package com.zmx.study.interview.second.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();

        for (int i = 0; i < 5; i++) {
            new Thread(phone::sendSMS, String.valueOf(i)).start();
        }

        TimeUnit.SECONDS.sleep(2);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        new Thread(phone, "aa").start();
        new Thread(phone, "bb").start();
    }
}

class Phone implements Runnable {
    private Lock lock = new ReentrantLock();

    synchronized void sendSMS() {
        System.out.println(Thread.currentThread().getName() + "Send SMS");
        sendEmail();
    }

    private synchronized void sendEmail() {
        System.out.println(Thread.currentThread().getName() + "Send Email");
    }

    @Override
    public void run() {
        get();
    }

    private void get() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "invoke getUnLock()");
            set();
        } finally {
            lock.unlock();
        }
    }

    private void set() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "invoke set");
        } finally {
            lock.unlock();
        }
    }
}
