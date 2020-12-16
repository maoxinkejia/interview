package com.zmx.study.interview.second.gc;

/**
 * 1.-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseSerialGC                     (DefNew  +  Tenured)
 *
 * 2.-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseParNewGC                     (ParNew  +  Tenured)
 *
 * 3.-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseParallelGC                   (PSYoungGen  +  ParOldGen)
 *
 * 4.
 * 4.1-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseParallelOldGC                (PSYoungGen  +  ParOldGen)
 *
 * 4.2-Xms10m -Xmx10m -XX:+PrintGCDetails             不加默认ParallelGC
 *
 * 5.-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseConcMarkSweepGC                (par new generation  + concurrent mark sweep)
 *
 * 6.-Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UserG1GC
 *
 * 7.(理论知道即可，实际中已经被优化掉了)
 * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:+UseSerialOldGC
 */
public class GCDemo {
    public static void main(String[] args) {
        StringBuilder str = new StringBuilder("zmx_hhhhhh");
        while (true) {
            str.append("*****");
        }
    }
}
