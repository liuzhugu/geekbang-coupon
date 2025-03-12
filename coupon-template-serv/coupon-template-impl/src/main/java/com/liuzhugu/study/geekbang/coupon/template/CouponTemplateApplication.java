package com.liuzhugu.study.geekbang.coupon.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 优惠券模板应用
 * */
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.liuzhugu.study.geekbang"})
public class CouponTemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateApplication.class,args);
    }
}
