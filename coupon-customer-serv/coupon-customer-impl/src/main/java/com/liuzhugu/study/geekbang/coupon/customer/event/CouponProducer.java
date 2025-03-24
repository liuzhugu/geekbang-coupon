package com.liuzhugu.study.geekbang.coupon.customer.event;

import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.constant.EventConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CouponProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void sendCoupon(RequestCoupon coupon) {
        log.info("sent: {}",coupon);
        streamBridge.send(EventConstant.ADD_COUPON_EVENT,coupon);
    }

    public void deleteCoupon(Long userId,Long couponId) {
        log.info("sent delete coupon event: userId={}, couponId={}", userId, couponId);
        streamBridge.send(EventConstant.DELETE_COUPON_EVENT,userId + "," + couponId);
    }
}
