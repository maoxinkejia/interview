package com.zmx.study.interview.second.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueDemo {
    public static void main(String[] args) throws Exception {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);

    }

    /**
     * 设置超时时间，若超出时间则自动退出，若未超出时间，则阻塞等待
     */
    private static void offerAndPollByTime(BlockingQueue<String> blockingQueue) throws InterruptedException {
        System.out.println(blockingQueue.peek());

        System.out.println(blockingQueue.offer("a", 2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("b", 2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("c", 2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("x", 2, TimeUnit.SECONDS));

        System.out.println(blockingQueue.peek());

        System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
    }

    /**
     * put/take 当队列满或空时，再执行put/take操作时会一直阻塞生产线程，知道成功或响应中断退出
     */
    private static void putAndTake(BlockingQueue<String> blockingQueue) throws InterruptedException {
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");
//        blockingQueue.put("x");

        System.out.println("======================");

        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
//        System.out.println(blockingQueue.take());
    }

    /**
     * offer/poll 添加/删除元素时 返回true/false
     * peek 若有元素则返回元素，若无元素则返回null
     * FIFO
     */
    private static void offerAndPoll(BlockingQueue<String> blockingQueue) {

        System.out.println(blockingQueue.peek());

        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));
        System.out.println(blockingQueue.offer("x"));

        System.out.println(blockingQueue.peek());

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
    }

    /**
     * add/remove 在队列已满或没有元素时，直接抛出异常
     * element查看队首元素，若没有元素时抛出异常
     * FIFO
     */
    private static void addAndRemove(BlockingQueue<String> blockingQueue) {
//        System.out.println(blockingQueue.element());

        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));
//        System.out.println(blockingQueue.add("x"));

        System.out.println(blockingQueue.element());

        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
//        System.out.println(blockingQueue.remove());
    }
}
