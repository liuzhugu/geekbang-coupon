package com.liuzhugu.study.geekbang.coupon.calculation.api.beans;

import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 购物车
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @NotEmpty
    private List<Product> products;

    private Long couponId;

    private long cost;

    //目前只支持单张优惠券
    //但是为了以后的扩展考虑  可以添加多个优惠券的计算逻辑
    private List<CouponInfo> couponInfos;

    @NotNull
    private long userId;
}
