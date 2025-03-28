package com.liuzhugu.study.geekbang.coupon.customer;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Seata接管数据源   然后在里面实现开启分支事务、生成回滚语句等操作
 * 实现无侵入式接入
 * */
@Configuration
public class SeataConfiguration {

    //加载配置生成数据源
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }

    //对数据源进行Seata所需的加料
    @Bean("dataSource")
    //声明为DataSource默认的代理类  提高优先级
    @Primary
    public DataSource dataSourceDelegation(DruidDataSource druidDataSource) {
        return new DataSourceProxy(druidDataSource);
    }
}
