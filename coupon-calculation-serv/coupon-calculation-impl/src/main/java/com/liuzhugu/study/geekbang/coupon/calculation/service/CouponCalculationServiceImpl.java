package com.liuzhugu.study.geekbang.coupon.calculation.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.calculation.service.intf.CouponCalculationService;
import com.liuzhugu.study.geekbang.coupon.calculation.template.CouponTemplateFactory;
import com.liuzhugu.study.geekbang.coupon.calculation.template.RuleTemplate;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class CouponCalculationServiceImpl implements CouponCalculationService {

    @Autowired
    private CouponTemplateFactory couponProcessorFactory;

    //优惠券结算
    //这里通过Factory类决定使用哪个底层Rule  底层规则对上层透明
    @Override
    public ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart shoppingCart) {
        log.info("calculate order price: {}", JSON.toJSONString(shoppingCart));
        //获取相应的模板
        RuleTemplate ruleTemplate = couponProcessorFactory.getTemplate(shoppingCart);
        //使用相应的模板进行计算
        return ruleTemplate.calculate(shoppingCart);
    }

    //对所有优惠券进行试算  看看哪个最省钱
    @Override
    public SimulationResponse simulateOrder(@RequestBody SimulationOrder order) {
        SimulationResponse response = new SimulationResponse();
        Long minOrderPrice = Long.MAX_VALUE;

        //计算每一张优惠券的计算价格
        for (CouponInfo coupon : order.getCouponInfos()) {
            ShoppingCart cart = new ShoppingCart();
            cart.setProducts(order.getProducts());
            cart.setCouponInfos(Lists.newArrayList(coupon));
            //计算
            cart = couponProcessorFactory.getTemplate(cart).calculate(cart);

            Long couponId = coupon.getId();
            Long orderPrice = cart.getCost();

            //设置当前优惠券对应的订单价格
            response.getCouponToOrderPrice().put(couponId,orderPrice);

            //比较订单价格 设置当前最优优惠券的ID
            if (minOrderPrice > orderPrice) {
                response.setBestCouponId(couponId);
                minOrderPrice = orderPrice;
            }
        }
        return response;
    }
}
