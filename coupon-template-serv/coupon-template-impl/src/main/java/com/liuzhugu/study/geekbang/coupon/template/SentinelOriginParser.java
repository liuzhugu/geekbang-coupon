package com.liuzhugu.study.geekbang.coupon.template;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class SentinelOriginParser implements RequestOriginParser {

    //取出来源标识符
    @Override
    public String parseOrigin(HttpServletRequest request) {
        log.info("request {},header = {}",request.getParameterMap(),request.getHeaderNames());
        return request.getHeader("SentinelSource");
    }
}
