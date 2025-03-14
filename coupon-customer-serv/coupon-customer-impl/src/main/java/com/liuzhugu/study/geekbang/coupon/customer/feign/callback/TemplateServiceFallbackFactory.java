package com.liuzhugu.study.geekbang.coupon.customer.feign.callback;

import com.google.common.collect.Maps;
import com.liuzhugu.study.geekbang.coupon.customer.feign.TemplateService;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 降级逻辑
 * */
@Slf4j
@Component
public class TemplateServiceFallbackFactory implements FallbackFactory<TemplateService> {

    @Override
    public TemplateService create(Throwable cause) {
        //使用这种方法可以捕捉到具体的异常

        return new TemplateService() {
            @Override
            public CouponTemplateInfo getTemplate(Long id) {
                log.info("fallback factory method test" + cause);
                return null;
            }

            @Override
            public Map<Long, CouponTemplateInfo> getTemplateInBatch(Collection<Long> ids) {
                log.info("fallback factory method test" + cause);
                return Maps.newHashMap();
            }
        };
    }
}
