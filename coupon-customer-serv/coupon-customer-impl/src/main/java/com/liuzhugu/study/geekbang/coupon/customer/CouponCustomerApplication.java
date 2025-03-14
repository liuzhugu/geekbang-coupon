package com.liuzhugu.study.geekbang.coupon.customer;

import com.liuzhugu.study.geekbang.coupon.customer.loadbalance.CanaryRuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.Entity;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.liuzhugu.study.geekbang"})
@EnableTransactionManagement
//用于扫描DAO @Repository
@EnableJpaRepositories(basePackages = {"com.liuzhugu.study.geekbang"})
//用于扫描JPA实体类 @Entity，默认扫本包当下路径
@EntityScan(basePackages = {"com.liuzhugu.study.geekbang"})
//发到coupon-template-serv的调用  使用金丝雀策略做负载均衡
//@LoadBalancerClient(value = "coupon-template-serv",configuration = CanaryRuleConfiguration.class)
//开启OpenFeign  对相应接口生成代理类   通过接口上的注解里的远程调用信息的将本地调用转变成远程调用
@EnableFeignClients(basePackages = {"com.liuzhugu.study.geekbang"})
public class CouponCustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponCustomerApplication.class, args);
    }
}
