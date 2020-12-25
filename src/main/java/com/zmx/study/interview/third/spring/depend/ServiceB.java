package com.zmx.study.interview.third.spring.depend;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ServiceB {
    private ServiceA a;

    public ServiceA getA() {
        return a;
    }

    public void setA(ServiceA a) {
        this.a = a;
    }
}
