package com.zmx.study.interview.third.spring.depend;

public class MainClass {
    public static void main(String[] args) {

    }

    private static void circularDepend() {
        ServiceA serviceA = new ServiceA();
        ServiceB serviceB = new ServiceB();

        serviceA.setB(serviceB);
        serviceB.setA(serviceA);
    }
}
