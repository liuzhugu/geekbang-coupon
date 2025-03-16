package com.liuzhugu.study.geekbang.gateway.dynamic;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import com.alibaba.nacos.api.config.listener.Listener;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 监听Nacos配置变更
 * */
@Slf4j
@Component
public class DynamicRoutesListener implements Listener{

    @Autowired
    private GatewayService gatewayService;

    @Override
    public Executor getExecutor() {
        log.info("getExecutor");
        return null;
    }

    //当被监听的Nacos配置文件变更时  框架会自动调用该方法
    @Override
    public void receiveConfigInfo(String configInfo) {
        log.info("received routes changes {}",configInfo);

        //获取配置变更  并从plain text转变成RouteDefinition
        List<RouteDefinition> definitionList = JSON.parseArray(configInfo,RouteDefinition.class);
        //更新配置
        gatewayService.updateRoutes(definitionList);
    }
}
