package com.liuzhugu.study.geekbang.coupon.template.converter;

import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
import com.liuzhugu.study.geekbang.coupon.template.dao.entity.CouponTemplate;

/**
 * 转换类
 * */
public class CouponTemplateConverter {
    public static CouponTemplateInfo converterToTemplateInfo(CouponTemplate template) {
        return CouponTemplateInfo.builder()
                .id(template.getId())
                .name(template.getName())
                .desc(template.getDescription())
                .type(template.getCategory().getCode())
                .shopId(template.getShopId())
                .available(template.getAvailable())
                .rule(template.getRule())
                .build();
    }
}
