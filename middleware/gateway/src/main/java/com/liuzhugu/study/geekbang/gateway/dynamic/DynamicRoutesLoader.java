package com.liuzhugu.study.geekbang.gateway.dynamic;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 动态路由加载器
 * 在项目启动时从Nacos读取配置文件来初始化路由表
 * */
@Slf4j
@Configuration
public class DynamicRoutesLoader  implements InitializingBean {

    @Autowired
    private NacosConfigManager configService;

    @Autowired
    private NacosConfigProperties configProps;

    @Autowired
    private  DynamicRoutesListener dynamicRoutesListener;

    private static final String ROUTES_CONFIG = "routes-config.json";

    //在当前类所有的属性加载完成后  执行该方法
    @Override
    public void afterPropertiesSet() throws Exception {
        //启动时从Nacos加载指定的配置文件
        String routes = configService.getConfigService().getConfig(
                ROUTES_CONFIG,configProps.getGroup(),10000
        );
        //解析设置路由规则
        dynamicRoutesListener.receiveConfigInfo(routes);

        //注册监听路由规则变化的监听器
        configService.getConfigService().addListener(ROUTES_CONFIG,
                configProps.getGroup(),
                dynamicRoutesListener);
    }
}
