package com.liuzhugu.study.geekbang.coupon.template.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券信息
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponInfo {
    private Long id;

    private Long templateId;

    private Long userId; //领券用户ID

    private Long shopId; //优惠券使用门店 - 若无则为全店通用券

    private Integer status; //优惠券状态

    private CouponTemplateInfo template; //生成优惠券的模板
}
