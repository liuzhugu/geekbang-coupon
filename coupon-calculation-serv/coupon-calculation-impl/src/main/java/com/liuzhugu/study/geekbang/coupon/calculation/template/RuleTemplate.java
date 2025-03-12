package com.liuzhugu.study.geekbang.coupon.calculation.template;

import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;

public interface RuleTemplate {
    //计算优惠券
    ShoppingCart calculate(ShoppingCart settlement);
}
