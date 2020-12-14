package com.zmx.study.interview.second;

/**
 * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseSerialGC
 *
 * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseParNewGC
 */
public class GCDemo {
    public static void main(String[] args) {
        StringBuilder str = new StringBuilder("zmx_hhhhhh");
        while (true) {
            str.append("*****");
        }
    }
}
