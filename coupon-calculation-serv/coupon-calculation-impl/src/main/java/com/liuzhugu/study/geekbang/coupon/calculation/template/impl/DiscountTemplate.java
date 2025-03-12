package com.liuzhugu.study.geekbang.coupon.calculation.template.impl;


import com.liuzhugu.study.geekbang.coupon.calculation.template.AbstractRuleTemplate;
import com.liuzhugu.study.geekbang.coupon.calculation.template.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 打折优惠券
 * */
@Slf4j
@Component
public class DiscountTemplate extends AbstractRuleTemplate implements RuleTemplate {
    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        Long newPrice = covertToDecimal(shopTotalAmount * (quota.doubleValue() / 100));
        log.debug("original price = {},new price  = {}",orderTotalAmount,newPrice);
        return orderTotalAmount - (shopTotalAmount - newPrice);
    }
}
