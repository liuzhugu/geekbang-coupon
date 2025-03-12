package com.liuzhugu.study.geekbang.coupon.calculation.template.impl;

import com.liuzhugu.study.geekbang.coupon.calculation.template.AbstractRuleTemplate;
import com.liuzhugu.study.geekbang.coupon.calculation.template.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 满减优惠券计算规则
 * */
@Slf4j
@Component
public class MoneyOffTemplate extends AbstractRuleTemplate implements RuleTemplate {
    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        //benefitAmount是扣减的价格
        //如果当前门店的商品总价小于quota  那么最多只能扣减shopTotalAmount的钱数
        Long benefitAmount = shopTotalAmount < quota ? shopTotalAmount :quota;
        return orderTotalAmount - benefitAmount;
    }
}
