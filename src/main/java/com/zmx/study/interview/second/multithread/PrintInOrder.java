package com.zmx.study.interview.second.multithread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**
 * 交替打印
 */
public class PrintInOrder {
    public static void main(String[] args) {

    }
}

class ZeroEvenOdd {
    private final Integer ZERO = 0;
    private final Integer ODD = 1;
    private final Integer EVEN = 2;

    private volatile int flag = 0;
    private volatile boolean control = true;
    private int n;

    public ZeroEvenOdd(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            while (flag != ZERO) {
                Thread.yield();
            }

            printNumber.accept(0);
            if (control) {
                flag = ODD;
            } else {
                flag = EVEN;
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            while (flag != EVEN) {
                Thread.yield();
            }

            printNumber.accept(i);
            flag = ZERO;
            control = true;
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            while (flag != ODD) {
                Thread.yield();
            }

            printNumber.accept(i);
            flag = ZERO;
            control = false;
        }
    }
}

class ZeroEvenOdd3 {
    private final Integer ZERO = 0;
    private final Integer NOT_ZERO = 1;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();
    private volatile int flag = 0;
    private volatile boolean control = true;
    private int n;

    public ZeroEvenOdd3(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            lock.lock();

            try {
                while (flag != ZERO) {
                    c1.await();
                }

                printNumber.accept(0);
                if (control) {
                    c2.signal();
                } else {
                    c3.signal();
                }

                flag = NOT_ZERO;
            } finally {
                lock.unlock();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            lock.lock();

            try {
                while (flag != NOT_ZERO) {
                    c3.await();
                }

                printNumber.accept(i);
                flag = ZERO;
                c1.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            lock.lock();

            try {
                while (flag != ZERO) {
                    c2.await();
                }

                printNumber.accept(i);
                flag = ZERO;
                c1.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}

class ZeroEvenOdd2 {
    private int n;
    private Semaphore z = new Semaphore(1);
    private Semaphore o = new Semaphore(0);
    private Semaphore e = new Semaphore(0);

    public ZeroEvenOdd2(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            z.acquire();
            printNumber.accept(0);

            if (n % 2 != 0) {
                o.release();
            } else {
                e.release();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            e.acquire();
            printNumber.accept(i);
            z.release();
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            o.acquire();
            printNumber.accept(i);
            z.release();
        }
    }
}

class FooBar {
    private final Integer FOO = 1;
    private final Integer BAR = 2;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private int FLAG;
    private int n;

    public FooBar(int n) {
        this.n = n;
        this.FLAG = FOO;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            lock.lock();
            try {
                while (FLAG != FOO) {
                    c1.await();
                }

                // printFoo.run() outputs "foo". Do not change or remove this line.
                printFoo.run();
                FLAG = BAR;
                c2.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            lock.lock();
            try {
                while (FLAG != BAR) {
                    c2.await();
                }

                // printBar.run() outputs "bar". Do not change or remove this line.
                printBar.run();
                FLAG = FOO;
                c1.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}

/**
 * 输入: [1,2,3]
 * 输出: "firstsecondthird"
 * 解释:
 * 有三个线程会被异步启动。
 * 输入 [1,2,3] 表示线程 A 将会调用 first() 方法，线程 B 将会调用 second() 方法，线程 C 将会调用 third() 方法。
 * 正确的输出是 "firstsecondthird"。
 * <p>
 * 输入: [1,3,2]
 * 输出: "firstsecondthird"
 * 解释:
 * 输入 [1,3,2] 表示线程 A 将会调用 first() 方法，线程 B 将会调用 third() 方法，线程 C 将会调用 second() 方法。
 * 正确的输出是 "firstsecondthird"。
 */
class Foo {
    private final Integer FIRST = 1;
    private final Integer SECOND = 2;
    private final Integer THIRD = 3;

    private volatile int flag = FIRST;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();


    public Foo() {
    }

    public void first(Runnable printFirst) throws InterruptedException {
        lock.lock();
        try {
            while (flag != FIRST) {
                c1.await();
            }

            // printFirst.run() outputs "first". Do not change or remove this line.
            printFirst.run();
            flag = SECOND;
            c2.signal();
        } finally {
            lock.unlock();
        }
    }

    public void second(Runnable printSecond) throws InterruptedException {
        lock.lock();
        try {
            while (flag != SECOND) {
                c2.await();
            }

            // printSecond.run() outputs "second". Do not change or remove this line.
            printSecond.run();
            flag = THIRD;
            c3.signal();
        } finally {
            lock.unlock();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {
        lock.lock();
        try {
            while (flag != THIRD) {
                c3.await();
            }

            // printThird.run() outputs "third". Do not change or remove this line.
            printThird.run();
            flag = FIRST;
            c1.signal();
        } finally {
            lock.unlock();
        }
    }
}