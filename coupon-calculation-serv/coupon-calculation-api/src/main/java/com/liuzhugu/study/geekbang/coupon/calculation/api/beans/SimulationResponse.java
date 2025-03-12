package com.liuzhugu.study.geekbang.coupon.calculation.api.beans;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 计算结果
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponse {

    //最省钱的coupon
    private Long bestCouponId;

    //每一个coupon对应的order价格
    private Map<Long,Long> couponToOrderPrice = Maps.newHashMap();
}

