package com.liuzhugu.study.geekbang.coupon.calculation.controller;

import com.alibaba.fastjson.JSON;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.calculation.service.intf.CouponCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/calculator")
public class CouponCalculationController {

    @Autowired
    private CouponCalculationService calculationService;

    //优惠券结算
    @PostMapping("/checkout")
    @ResponseBody
    public ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart settlement) {
        log.info("do calculation: {}", JSON.toJSONString(settlement));
        return calculationService.calculateOrderPrice(settlement);
    }

    //优惠券列表按个试算
    //给客户提示每个可用券的优惠额度  帮助挑选
    @PostMapping("/simulate")
    @ResponseBody
    public SimulationResponse simulate(@RequestBody SimulationOrder simulator) {
        log.info("do simulator: {}", JSON.toJSONString(simulator));
        return calculationService.simulateOrder(simulator);
    }
}
