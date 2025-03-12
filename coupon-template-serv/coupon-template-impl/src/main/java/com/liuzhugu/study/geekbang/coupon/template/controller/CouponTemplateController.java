package com.liuzhugu.study.geekbang.coupon.template.controller;

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
    @PostMapping("/getTemplate")
    public CouponTemplateInfo getTemplate(@RequestParam("id") Long id) {
        log.info("load template: id={}",id);
        return couponTemplateService.loadTemplateInfo(id);
    }

    //批量获取
    @PostMapping("/getBatch")
    public Map<Long,CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        log.info("getTemplateInBatch: {}",JSON.toJSONString(ids));
        return couponTemplateService.getTemplateInfoMap(ids);
    }

    //搜索模板
    @PostMapping("/search")
    public PagedCouponTemplateInfo search(@Valid @RequestBody TemplateSearchParams request) {
        log.info("search templates,payload:{}",request);
        return couponTemplateService.search(request);
    }

    //优惠券无效化
    @PostMapping("/deleteTemplate")
    public void deleteTemplate(@RequestParam("id") Long id) {
        log.info("delete template,id:{}",id);
        couponTemplateService.deleteTemplate(id);
    }
}
