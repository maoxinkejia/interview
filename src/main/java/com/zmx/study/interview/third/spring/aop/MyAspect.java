package com.zmx.study.interview.third.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

    @Before("execution(public int  com.zmx.study.interview.third.spring.aop.CalServiceImpl.*(..))")
    public void beforeNotify() {
        System.out.println("*********** @Before 我是前置通知************");
    }

    @After("execution(public int  com.zmx.study.interview.third.spring.aop.CalServiceImpl.*(..))")
    public void afterNotify() {
        System.out.println("*******@After我是后置通知");
    }

    @AfterReturning("execution(public int  com.zmx.study.interview.third.spring.aop.CalServiceImpl.*(..))")
    public void afterReturningNotify() {
        System.out.println("*****@AfterReturning我是返回后通知");
    }

    @AfterThrowing("execution(public int  com.zmx.study.interview.third.spring.aop.CalServiceImpl.*(..))")
    public void afterThrowingNotify() {
        System.out.println("******@AfterThrowing我是异常通知");
    }

    @Around("execution(public int  com.zmx.study.interview.third.spring.aop.CalServiceImpl.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("我是环绕通知之前AAA");
        Object retVal = pjp.proceed();
        System.out.println("我是环绕通知之后BBB");
        return retVal;
    }
}
