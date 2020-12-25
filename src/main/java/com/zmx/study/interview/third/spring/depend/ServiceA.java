package com.zmx.study.interview.third.spring.depend;

public class ServiceA {
    private ServiceB b;

    public ServiceB getB() {
        return b;
    }

    public void setB(ServiceB b) {
        this.b = b;
    }
}
