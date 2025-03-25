package com.liuzhugu.study.geekbang.coupon.customer.event;

import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.constant.EventConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageBuilder;
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

    //使用延迟消息发送
    public void sendCouponInDelay(RequestCoupon coupon) {
        log.info("sent: {}",coupon);
        streamBridge.send(EventConstant.ADD_COUPON_DELAY_EVENT,
                MessageBuilder.withPayload(coupon)
                        //延迟十秒
                    .setHeader("x-delay", 10 * 1000)
                    .build());
    }
}
