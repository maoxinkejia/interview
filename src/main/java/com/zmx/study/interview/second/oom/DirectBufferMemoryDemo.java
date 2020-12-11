package com.zmx.study.interview.second.oom;

import sun.misc.VM;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * 配置参数
 * -Xms5m -Xmx5m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5
 */
public class DirectBufferMemoryDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("MaxDirectMemory：" + (VM.maxDirectMemory() / (double) 1024 / 1024) + "MB");
        TimeUnit.SECONDS.sleep(2);
        ByteBuffer bb = ByteBuffer.allocateDirect(6 * 1024 * 1024);
    }
}
