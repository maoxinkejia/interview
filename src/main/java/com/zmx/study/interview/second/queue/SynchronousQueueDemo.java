package com.zmx.study.interview.second.queue;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {
    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "\t a准备放进去了");
                blockingQueue.put("a");
                System.out.println(Thread.currentThread().getName() + "\t a放进去了");

                System.out.println(Thread.currentThread().getName() + "\t b准备放进去了");
                blockingQueue.put("b");
                System.out.println(Thread.currentThread().getName() + "\t b放进去了");

                System.out.println(Thread.currentThread().getName() + "\t c准备放进去了");
                blockingQueue.put("c");
                System.out.println(Thread.currentThread().getName() + "\t c放进去了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "AAA").start();


        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "\t 准备取出。。");
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + "\t 取出了：" + blockingQueue.take());

                System.out.println(Thread.currentThread().getName() + "\t 准备取出。。");
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + "\t 取出了：" + blockingQueue.take());

                System.out.println(Thread.currentThread().getName() + "\t 准备取出。。");
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + "\t 取出了：" + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "BBB").start();
    }
}
