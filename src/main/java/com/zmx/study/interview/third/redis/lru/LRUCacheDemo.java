package com.zmx.study.interview.third.redis.lru;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCacheDemo<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    public LRUCacheDemo(int capacity) {
        /*
         * accessOrder: true时无论是put还是get都会刷新该key的热点，将其放到链表的最尾端
         * false时只有第一次put进队列时的顺序，后面再put或get不会改变其位置，导致即使是热点数据也会被干掉，不符合使用要求
         */
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

    public static void main(String[] args) {
        LRUCacheDemo<Integer, Integer> demo = new LRUCacheDemo<>(3);
        demo.put(1, 1);
        demo.put(2, 2);
        demo.put(3, 3);
        System.out.println(demo.keySet());

        demo.put(4, 4);
        System.out.println(demo.keySet());

        demo.put(3, 3);
        System.out.println(demo.keySet());
        demo.put(3, 3);
        System.out.println(demo.keySet());

        demo.put(5, 5);
        System.out.println(demo.keySet());

        Integer integer = demo.get(3);
        System.out.println(demo.keySet());
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return super.size() > capacity;
    }

    @Override
    public V get(Object key) {
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        return super.put(key, value);
    }
}
