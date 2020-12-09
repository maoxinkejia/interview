package com.zmx.study.interview.second.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new MyThread());

        new Thread(futureTask, "AAA").start();
//        Integer result02 = futureTask.get();// get会阻塞main线程，早获取和晚获取效果不一样

        System.out.println(Thread.currentThread().getName() + "********");

        int result01 = 100;
        Integer result02 = futureTask.get();// get会阻塞main线程，早获取和晚获取效果不一样

        System.out.println("result = " + (result01 + result02));
    }
}

class MyThread implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("call......");
        TimeUnit.SECONDS.sleep(3);
        return 1024;
    }
}
