package com.zmx.study.interview.second.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {
    public static void main(String[] args) throws InterruptedException {
        MyCache myCache = new MyCache();

        for (int i = 1; i <= 5; i++) {
            final String tmp = String.valueOf(i);
            new Thread(() -> myCache.putUnLock(tmp, tmp), tmp).start();
        }

        for (int i = 1; i <= 5; i++) {
            final String tmp = String.valueOf(i);
            new Thread(() -> myCache.getUnLock(tmp), tmp).start();
        }

        TimeUnit.SECONDS.sleep(2);
        System.out.println();
        System.out.println();
        System.out.println();

        for (int i = 1; i <= 5; i++) {
            final String tmp = String.valueOf(i);
            new Thread(() -> myCache.putLock(tmp, tmp), tmp).start();
        }
        for (int i = 1; i <= 5; i++) {
            final String tmp = String.valueOf(i);
            new Thread(() -> myCache.getLock(tmp), tmp).start();
        }
    }
}

/**
 * 资源类
 */
class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    void putLock(String key, Object value) {
        lock.writeLock().lock();
        try {
            putUnLock(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    void getLock(String key) {
        lock.readLock().lock();
        try {
            getUnLock(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }

    void putUnLock(String key, Object value) {
        System.out.println(Thread.currentThread().getName() + "\t 正在写入：" + key);
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map.put(key, value);

        System.out.println(Thread.currentThread().getName() + "\t 写入完成");
    }

    void getUnLock(String key) {
        System.out.println(Thread.currentThread().getName() + "\t 正在读取");
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object result = map.get(key);
        System.out.println(Thread.currentThread().getName() + "\t 读取完成：" + result);
    }
}