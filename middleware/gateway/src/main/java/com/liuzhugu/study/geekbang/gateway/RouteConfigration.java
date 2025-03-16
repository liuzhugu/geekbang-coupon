package com.liuzhugu.study.geekbang.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class RouteConfigration {

    @Autowired
    private KeyResolver hostAddrKeyResolver;

    @Autowired
    @Qualifier("customerRateLimiter")
    private RateLimiter customerRateLimiter;

    @Autowired
    @Qualifier("templateRateLimiter")
    private RateLimiter templateRateLimiter;


    @Bean
    public RouteLocator declare(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(route -> route
                        .path("/gateway/customer/**")
                        //path匹配外界进来的请求   但其是对外的 给用户看的
                        // 用stripPrefix去掉/gateway  剩下的才是内部用的
                        .filters(f -> f.stripPrefix(1)
                                //设置限流器配置
                            .requestRateLimiter(limiter -> {
                                //以host addr为维度
                                limiter.setKeyResolver(hostAddrKeyResolver);
                                //令牌桶
                                limiter.setRateLimiter(customerRateLimiter);
                                //限流失败后返回的HTTP status code
                                limiter.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                            })
                        )
                        .uri("lb://coupon-customer-serv"))
                .route(route -> route
                        //如果一个请求命中了多个路由  order越小路由优先级越高
                        .path("/gateway/template/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(limiter -> {
                                    limiter.setKeyResolver(hostAddrKeyResolver);
                                    limiter.setRateLimiter(templateRateLimiter);
                                    limiter.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                                })
                        )
                        .uri("lb://coupon-template-serv"))
                .route(route -> route
                        .path("/gateway/calculator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coupon-calculation-serv"))
                .build();
    }
}
