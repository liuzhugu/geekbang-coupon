package com.liuzhugu.study.geekbang.coupon.customer.service.intf;

import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;

import java.util.List;

//用户对接服务
public interface CouponCustomerService {

    //领券接口
    Coupon requestCoupon(RequestCoupon request);

    //核销优惠券
    ShoppingCart placeOrder(ShoppingCart info);

    //优惠券金额试算
    SimulationResponse simulateOrderPrice(SimulationOrder order);

    void deleteCoupon(Long userId,Long couponId);

    void deleteCouponTemplate(Long templateId);

    //查询用户优惠券
    List<CouponInfo> findCoupon(SearchCoupon request);
}
