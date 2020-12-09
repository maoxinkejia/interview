package com.zmx.study.interview.second.queue;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumer_BlockQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<String>(10));

        new Thread(() -> {
            System.out.println("生产生启动了");
            try {
                myResource.myProducer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Producer").start();

        new Thread(() -> {
            System.out.println("消费者启动了");
            try {
                myResource.myConsumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        TimeUnit.SECONDS.sleep(7);

        System.out.println("主动停止生产");
        myResource.stop();

    }
}

class MyResource {

    private volatile boolean FLAG = true;
    private BlockingQueue<String> blockingQueue;
    private AtomicInteger atomicInteger = new AtomicInteger();

    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    public void myProducer() throws Exception {
        System.out.println();
        System.out.println();
        System.out.println();
        TimeUnit.SECONDS.sleep(1);
        while (FLAG) {
            String data = String.valueOf(atomicInteger.incrementAndGet());
            boolean result = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (result) {
                System.out.println(Thread.currentThread().getName() + "\t插入成功：" + data);
            } else {
                System.out.println(Thread.currentThread().getName() + "\t插入失败：" + data);
            }

            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println("生产者停止生产");
    }

    public void myConsumer() throws Exception {
        System.out.println();
        System.out.println();
        System.out.println();
        while (FLAG) {
            String data = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (StringUtils.isBlank(data)) {
                FLAG = false;
                System.out.println("超时，消费退出");
                System.out.println();
                System.out.println();
                System.out.println();
                return;
            }

            System.out.println(Thread.currentThread().getName() + "\t消费成功：" + data);
        }
    }

    public void stop() {
        FLAG = false;
    }
}
