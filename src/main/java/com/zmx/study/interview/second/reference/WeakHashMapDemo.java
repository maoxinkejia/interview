package com.zmx.study.interview.second.reference;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class WeakHashMapDemo {
    public static void main(String[] args) throws InterruptedException {
        myHashMap();
        System.out.println("==================");
        myWeakHashMap();
    }

    private static void myHashMap() {
        Map<Integer, String> map = new HashMap<>();
        Integer key = new Integer(1);//此处不可拆箱，否则gc无法回收
        String value = "hashMap";

        map.put(key, value);
        System.out.println(map);

        key = null;
        System.out.println(map);

        System.gc();
        System.out.println(map + "\t" + map.size());
    }

    private static void myWeakHashMap() throws InterruptedException {
        WeakHashMap<Integer, String> map = new WeakHashMap<>();
        Integer key = new Integer(2);//此处不可拆箱，否则gc无法回收key对象，导致weakHashMap无法被gc
        String value = "weakHashMap";

        map.put(key, value);
        System.out.println(map);

        key = null;
        System.out.println(map);

        System.gc();
        System.out.println(map + "\t" + map.size());
    }
}
