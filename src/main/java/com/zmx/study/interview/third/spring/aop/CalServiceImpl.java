package com.zmx.study.interview.third.spring.aop;

import org.springframework.stereotype.Service;

@Service
public class CalServiceImpl implements CalService {

    @Override
    public int div(int x, int y) {
        int result = x / y;
        System.out.println("=============" + getClass().getName() + "被调用了，计算结果：" + result);

        return result;
    }

}
