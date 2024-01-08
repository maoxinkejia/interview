package com.zmx.study.interview.second.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public class MyThreadPoolDemo {


    public static void main(String[] args) throws InterruptedException {
        Set<Integer> set = new CopyOnWriteArraySet<>();

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,
                5,
                600L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
//                new ThreadPoolExecutor.CallerRunsPolicy());
//                new ThreadPoolExecutor.DiscardOldestPolicy());
//                new ThreadPoolExecutor.DiscardPolicy());


        try {
            for (int i = 1; i <= 10; i++) {
                set.add(i);
                threadPool.execute(new MyTask(i, set));
            }
        } catch (Exception e) {
            log.error("", e);
        }



        while (!set.isEmpty()) {
            log.info("flag={}", threadPool.isTerminated());
            TimeUnit.SECONDS.sleep(1L);
        }

        log.info("shutdown threadPool...");
        threadPool.shutdown();
        log.info("finished");
    }
}


@Slf4j
class MyTask implements Runnable {

    private final int tmp;
    private final Set<Integer> set;


    public MyTask(int tmp, Set<Integer> set) {
        this.tmp = tmp;
        this.set = set;
    }

    @Override
    public void run() {
        log.info("\t进入了，准备执行：" + tmp);

        log.info("before set={}", Arrays.toString(set.toArray()));

        try {
            int sleep = new Random().nextInt(2) + 2;
            log.info("sleep time={}s", sleep);

            TimeUnit.SECONDS.sleep(sleep);

            boolean removed = set.remove(tmp);
            log.info("after set={}, removed={}, element={}", set, removed, tmp);
        } catch (InterruptedException e) {
            log.error("", e);
        }
        log.info("\t 执行完成：" + tmp);
    }
}