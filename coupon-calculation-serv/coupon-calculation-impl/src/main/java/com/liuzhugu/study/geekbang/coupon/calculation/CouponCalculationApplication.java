package com.liuzhugu.study.geekbang.coupon.calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.liuzhugu.study.geekbang"})
public class CouponCalculationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponCalculationApplication.class, args);
    }
}
