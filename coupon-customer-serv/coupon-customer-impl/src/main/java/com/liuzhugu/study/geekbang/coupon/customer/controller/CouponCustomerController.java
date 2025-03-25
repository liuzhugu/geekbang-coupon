package com.liuzhugu.study.geekbang.coupon.customer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import com.liuzhugu.study.geekbang.coupon.customer.event.CouponProducer;
import com.liuzhugu.study.geekbang.coupon.customer.service.intf.CouponCustomerService;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customer")
//开启才能让Nacos Config将变动的属性同步进来  否则不会刷新
@RefreshScope
public class CouponCustomerController {
    @Autowired
    private CouponCustomerService customerService;

    @Autowired
    private CouponProducer  couponProducer;

    //业务开关  通过配置修改值而动态开闭  控制业务逻辑  设置默认值
    @Value("${disableCouponRequest:false}")
    private boolean disableCoupon;

    //用户领取优惠券
    @PostMapping("/requestCoupon")
    @ResponseBody
    @SentinelResource(value = "customer-requestCoupon")
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) {
        if(disableCoupon) {
            log.info("暂停领取优惠券");
            return null;
        }
        return customerService.requestCoupon(request);
    }

    //查找优惠券
    @PostMapping("/findCoupon")
    @ResponseBody
    @SentinelResource(value = "customer-findCoupon")
    public List<CouponInfo> findCoupon(@Valid @RequestBody SearchCoupon request) {
        return customerService.findCoupon(request);
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

    @PostMapping("requestCouponEvent")
    public void requestCouponEvent(@Valid @RequestBody RequestCoupon request) {
        couponProducer.sendCoupon(request);
    }

    @PostMapping("requestCouponDelayEvent")
    public void requestCouponDelayEvent(@Valid @RequestBody RequestCoupon request) {
        couponProducer.sendCouponInDelay(request);
    }

    // 用户删除优惠券
    @DeleteMapping("deleteCouponEvent")
    public void deleteCouponEvent(@RequestParam("userId") Long userId,
                                  @RequestParam("couponId") Long couponId) {
        couponProducer.deleteCoupon(userId, couponId);
    }
}
