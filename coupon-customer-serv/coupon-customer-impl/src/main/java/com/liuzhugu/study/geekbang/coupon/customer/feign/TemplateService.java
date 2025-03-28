package com.liuzhugu.study.geekbang.coupon.customer.feign;

import com.liuzhugu.study.geekbang.coupon.customer.feign.callback.TemplateServiceFallback;
import com.liuzhugu.study.geekbang.coupon.customer.feign.callback.TemplateServiceFallbackFactory;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

//通过注解配置远程调用消息   对客户端代码屏蔽这部分信息  用起来像本地调用一样
//服务名作为内部网络调用的域名  根据Nacos提供的服务列表转变成对应实例的IP
@FeignClient(value = "coupon-template-serv",path = "/template",
        //降级逻辑
        //fallback = TemplateServiceFallback.class
        // 在降级方法里面获取异常的具体原因
        fallbackFactory = TemplateServiceFallbackFactory.class)
public interface TemplateService {
    //读取优惠券
    @GetMapping("/getTemplate")
    CouponTemplateInfo getTemplate(@RequestParam("id")Long id);

    //批量获取
    @GetMapping("/getBatch")
    Map<Long,CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids")Collection<Long> ids);

    //将券模板失效
    @DeleteMapping("/deleteTemplate")
    void deleteTemplate(@RequestParam("id")Long id);
}
