package com.liuzhugu.study.geekbang.coupon.customer.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenfeignSentinelInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //将调用源的应用名加入Request中作为来源标记
        template.header("SentinelSource","coupon-customer-serv");
    }
}
