package com.zmx.study.interview.second.lock;

import java.util.concurrent.*;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);

        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName()+ "\t semaphore.getQueueLength() = " + semaphore.getQueueLength());
//                    System.out.println("semaphore.hasQueuedThreads() = " + semaphore.hasQueuedThreads());
//                    System.out.println("semaphore.drainPermits() = " + semaphore.drainPermits());
//                    System.out.println("semaphore.availablePermits() = " + semaphore.availablePermits());
                    semaphore.acquire();
//                    boolean b = semaphore.tryAcquire();
//                    if (b) {
//                    }
                    System.out.println(Thread.currentThread().getName() + "\t 抢到车位");
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName() + "\t 离开车位。。");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void cyclicBarrier() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> System.out.println("aaaa"));

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cyclicBarrier.getNumberWaiting() = " + cyclicBarrier.getNumberWaiting());
            System.out.println("cyclicBarrier.getParties() = " + cyclicBarrier.getParties());
        }, "aa").start();

        for (int i = 0; i < 10; i++) {
            final int tmp = i;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(200 * tmp);
                    System.out.println(Thread.currentThread().getName() + "\t 调用一次。。。");
                    System.out.println("cyclicBarrier.getNumberWaiting() = " + cyclicBarrier.getNumberWaiting());
                    System.out.println("cyclicBarrier.getParties() = " + cyclicBarrier.getParties());
                    cyclicBarrier.await();

                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }

    private static void countDownLatch() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 进入了");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + "\t ***************");
    }
}
