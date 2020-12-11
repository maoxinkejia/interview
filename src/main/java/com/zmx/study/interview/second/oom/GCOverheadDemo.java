package com.zmx.study.interview.second.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * JVM参数配置：
 * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
 */
public class GCOverheadDemo {
    public static void main(String[] args) {
        int i = 0;
        List<String> list = new ArrayList<>();

        try {
            while (true) {
                list.add(String.valueOf(++i).intern());
            }
        } catch (Throwable e) {
            System.out.println("********************i: " + i);
            throw e;
        }
    }
}
