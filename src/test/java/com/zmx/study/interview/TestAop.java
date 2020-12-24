package com.zmx.study.interview;

import com.zmx.study.interview.third.spring.aop.CalService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestAop {

    @Autowired
    private CalService calService;

    @Test
    public void test() {
        System.out.println(SpringBootVersion.getVersion());
        calService.div(1, 0);
    }
}
