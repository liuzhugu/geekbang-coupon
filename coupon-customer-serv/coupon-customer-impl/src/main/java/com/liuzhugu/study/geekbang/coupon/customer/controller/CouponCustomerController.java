package com.liuzhugu.study.geekbang.coupon.customer.controller;

import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import com.liuzhugu.study.geekbang.coupon.customer.service.intf.CouponCustomerService;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CouponCustomerController {

    @Autowired
    private CouponCustomerService customerService;

    //用户领取优惠券
    @PostMapping("/requestCoupon")
    @ResponseBody
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) {
        return customerService.requestCoupon(request);
    }

    //用户删除优惠券 - 非物理删除
    @DeleteMapping("/deleteCoupon")
    public void deleteCoupon(@RequestParam("userId") Long userId,@RequestParam("couponId") Long couponId) {
        customerService.deleteCoupon(userId, couponId);
    }

    //用户模拟计算每个优惠券的优惠价格
    @PostMapping("/simulateOrder")
    @ResponseBody
    public SimulationResponse simulate(@Valid @RequestBody SimulationOrder order) {
        return customerService.simulateOrderPrice(order);
    }

    //下单核销优惠券
    @PostMapping("/checkout")
    @ResponseBody
    public ShoppingCart checkout(@Valid @RequestBody ShoppingCart info) {
        return customerService.placeOrder(info);
    }

    //查找优惠券
    @PostMapping("/findCoupon")
    @ResponseBody
    public List<CouponInfo> findCoupon(@Valid @RequestBody SearchCoupon request) {
        return customerService.findCoupon(request);
    }
}
