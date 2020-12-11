package com.zmx.study.interview.second.oom;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class MetaspaceDemo {
    public static void main(String[] args) {
        int i = 0;
        try {
            while (true) {
                i++;
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(OOMTest.class);
                enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> methodProxy.invokeSuper(o, objects));
                enhancer.create();
            }
        } catch (Exception e) {
            System.out.println("************:" + i);
            e.printStackTrace();
        }
    }

    static class OOMTest {
    }
}
