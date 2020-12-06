package com.zmx.study;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VolatileDemo {

    public static void main(String[] args) throws InterruptedException {
        MyData myData = new MyData();

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5000; j++) {
                    myData.add1();
                    myData.add1_();
                }
            }, String.valueOf(i)).start();
        }

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

        System.out.println(myData.number);
        System.out.println(myData.num);
    }

    private static void seeOkByVolatile() {
        MyData myData = new MyData();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            myData.addTo60();
            System.out.println(Thread.currentThread().getName() + "\t update number value : " + myData.number);
        }, "aaa").start();

        while (myData.number == 0) {
        }

        System.out.println("1111111111111");
    }
}

class MyData {
    volatile int number = 0;
    AtomicInteger num = new AtomicInteger();

    public void addTo60() {
        this.number = 60;
    }

    public void add1() {
        number++;
    }

    public void add1_() {
        num.getAndIncrement();
    }


}
