package com.zmx.study.interview.second.threadpool;

import java.util.concurrent.TimeUnit;

public class DeadLockDemo {
    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";
        MyResource myResource = new MyResource(lockA, lockB);
        new Thread(() -> {
            try {
                myResource.lockA();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "AAA").start();
        new Thread(() -> {
            try {
                myResource.lockB();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "BBB").start();
    }

    static class MyResource {
        private final String lockA;
        private final String lockB;

        public MyResource(String lockA, String lockB) {
            this.lockA = lockA;
            this.lockB = lockB;
        }

        public void lockA() throws InterruptedException {
            synchronized (lockA) {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + "\t获取了lockA锁，正在尝试获取lockB锁");
                synchronized (lockB) {
                    System.out.println(111);
                }
            }
        }

        public void lockB() throws InterruptedException {
            synchronized (lockB) {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + "\t获取了lockB锁，正在尝试获取lockA锁");
                synchronized (lockA) {
                    System.out.println(222);
                }
            }
        }
    }
}
