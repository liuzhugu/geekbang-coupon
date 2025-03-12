package com.liuzhugu.study.geekbang.coupon.calculation.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long productId;

    //商品的价格   单位为分
    private long price;

    //商品在购物车里的数量
    private Integer count;

    //商品销售的门店
    private Long shopId;
}
