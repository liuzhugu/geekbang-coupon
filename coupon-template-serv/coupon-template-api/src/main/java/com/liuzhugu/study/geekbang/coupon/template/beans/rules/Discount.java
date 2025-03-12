package com.liuzhugu.study.geekbang.coupon.template.beans.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券规则
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    //对于满减券 - quota是减掉的钱数  单位是分
    //对于打折券 - quota是折扣(以100表示原价) 90就是打九折  95就是95折
    //对于随机立减券 - quota是最高的随机立减额
    //对于晚间特别优惠券 - quota是日间优惠额 晚间优惠翻倍
    private Long quota;

    //订单最低要达到多少钱才能使用优惠券，单位为分
    // 100就是100分   就是一块钱   比使用double到处转换BigDecimal
    // 但要避免直接使用  对外时必须转换为元
    private Long threshold;
}
