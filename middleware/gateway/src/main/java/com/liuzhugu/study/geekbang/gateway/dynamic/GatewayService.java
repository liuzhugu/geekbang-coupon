package com.liuzhugu.study.geekbang.gateway.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 路由规则编辑
 * 将变化后的路由信息添加到网关上下文
 * */
@Slf4j
@Service
public class GatewayService {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private ApplicationEventPublisher publisher;

    //接受到路由规则的更新 来更新路由
    public void updateRoutes(List<RouteDefinition> route) {
        if (CollectionUtils.isEmpty(route)) {
            log.info("No routes found");
            return;
        }

        route.forEach(r -> {
            try {
                //更新路由规则
                routeDefinitionWriter.save(Mono.just(r)).subscribe();
                //发布路由规则刷新事件
                publisher.publishEvent(new RefreshRoutesEvent(this));
            } catch (Exception e) {
                log.error("cannot update route,id = {}",r.getId());
            }
        });
    }
}
