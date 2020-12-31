package com.zmx.study.interview.third.spring.depend;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainClass {
    public static void main(String[] args) {
        // 缺少spring.xml的配置文件，有的话可以运行验证
        ApplicationContext context = new ClassPathXmlApplicationContext("");
        context.getBean("a");
        context.getBean("b");
    }

    private static void circularDepend() {
        ServiceA serviceA = new ServiceA();
        ServiceB serviceB = new ServiceB();

        serviceA.setB(serviceB);
        serviceB.setA(serviceA);
    }
}
