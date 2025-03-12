package com.liuzhugu.study.geekbang.coupon.customer.service;

import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;

public class CouponConverter {
    public static CouponInfo coverToCoupon(Coupon coupon) {
        return CouponInfo.builder()
                .id(coupon.getId())
                .status(coupon.getStatus().getCode())
                .templateId(coupon.getTemplateId())
                .shopId(coupon.getShopId())
                .userId(coupon.getUserId())
                .templateId(coupon.getTemplateId())
                .build();
    }
}
