package com.liuzhugu.study.geekbang.coupon.customer.feign;

import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "coupon-calculation-serv",path = "/calculator")
public interface CalculationService {

    //订单结算
    @PostMapping("/checkout")
    ShoppingCart checkout(ShoppingCart settlement);

    //优惠券试算
    @PostMapping("/simulate")
    SimulationResponse simulate(SimulationOrder simulator);
}
