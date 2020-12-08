package com.zmx.study.interview.second.cas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABADemo {

    private static AtomicReference<Integer> atomicInteger = new AtomicReference<>(100);
    private static AtomicStampedReference<Integer> atomicStampedInteger = new AtomicStampedReference<>(100, 1);

    public static void main(String[] args) {
//        new Thread(() -> {
//            atomicInteger.compareAndSet(100, 101);
//            atomicInteger.compareAndSet(101, 100);
//        }, "t1").start();
//
//        new Thread(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(atomicInteger.compareAndSet(100, 2020));
//        }, "t2").start();

        new Thread(() -> {
            int stamp = atomicStampedInteger.getStamp();
            System.out.println("t3 第一次版本号：" + stamp);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedInteger.compareAndSet(100, 101, stamp, stamp + 1);
            System.out.println("t3第二次版本号：" + atomicStampedInteger.getStamp());
            atomicStampedInteger.compareAndSet(101, 100, atomicStampedInteger.getStamp(), atomicStampedInteger.getStamp() + 1);
            System.out.println("t3第三次版本号：" + atomicStampedInteger.getStamp());
        }, "t3").start();

        new Thread(() -> {
            int stamp = atomicStampedInteger.getStamp();
            System.out.println("t4 第一次版本号：" + stamp);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicStampedInteger.compareAndSet(100, 101, stamp, stamp + 1));
            System.out.println("t4当前版本号：" + atomicStampedInteger.getStamp() + "\t当前值：" + atomicStampedInteger.getReference());

        }, "t4").start();

    }
}
