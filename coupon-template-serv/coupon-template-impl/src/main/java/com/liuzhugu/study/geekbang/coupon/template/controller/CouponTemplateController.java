package com.liuzhugu.study.geekbang.coupon.template.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.google.common.collect.Maps;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
import com.liuzhugu.study.geekbang.coupon.template.beans.PagedCouponTemplateInfo;
import com.liuzhugu.study.geekbang.coupon.template.beans.TemplateSearchParams;
import com.liuzhugu.study.geekbang.coupon.template.service.intf.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/template")
public class CouponTemplateController {

    @Autowired
    private CouponTemplateService couponTemplateService;

    //创建优惠券
    @PostMapping("/addTemplate")
    public CouponTemplateInfo addTemplate(@Valid @RequestBody CouponTemplateInfo request) {
        log.info("Create coupon template: data={}",request);
        return couponTemplateService.createTemplate(request);
    }

    //创建优惠券
    @PostMapping("/cloneTemplate")
    public CouponTemplateInfo cloneTemplate(@RequestParam("id") Long templateId) {
        log.info("Clone coupon template: templateId={}",templateId);
        return couponTemplateService.cloneTemplate(templateId);
    }

    //读取优惠券
    @GetMapping("/getTemplate")
    @SentinelResource(value = "getTemplate")
    public CouponTemplateInfo getTemplate(@RequestParam("id") Long id) {
        log.info("load template: id={}",id);
        return couponTemplateService.loadTemplateInfo(id);
    }

    //批量获取
    @GetMapping("/getBatch")
    @SentinelResource(value = "getTemplateInBatch",
            //只针对Sentinel拦截请求跑出来的BlockException
            blockHandler = "getTemplateInBatch_block",
            //处理运行时异常  通用
            fallback = "getTemplateInBatch_fallback")
    public Map<Long,CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        log.info("getTemplateInBatch: {}",JSON.toJSONString(ids));
        return couponTemplateService.getTemplateInfoMap(ids);
    }

    public Map<Long,CouponTemplateInfo> getTemplateInBatch_block(Collection<Long> ids, BlockException excetion) {
        log.info("接口被限流");
        return Maps.newHashMap();
    }

    public Map<Long,CouponTemplateInfo> getTemplateInBatch_fallback(Collection<Long> ids) {
        log.info("接口被降级");
        return Maps.newHashMap();
    }

    //搜索模板
    @PostMapping("/search")
    public PagedCouponTemplateInfo search(@Valid @RequestBody TemplateSearchParams request) {
        log.info("search templates,payload:{}",request);
        return couponTemplateService.search(request);
    }

    //优惠券无效化
    @DeleteMapping("/deleteTemplate")
    public void deleteTemplate(@RequestParam("id") Long id) {
        log.info("delete template,id:{}",id);
        couponTemplateService.deleteTemplate(id);
    }
}
