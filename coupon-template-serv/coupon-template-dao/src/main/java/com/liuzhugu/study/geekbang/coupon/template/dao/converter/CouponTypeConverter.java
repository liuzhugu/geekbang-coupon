package com.liuzhugu.study.geekbang.coupon.template.dao.converter;

import com.liuzhugu.study.geekbang.coupon.template.enums.CouponType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券类型枚举转换
 * */
@Converter
public class CouponTypeConverter implements AttributeConverter<CouponType,String> {
    @Override
    public String convertToDatabaseColumn(CouponType couponType) {
        return couponType.getCode();
    }

    @Override
    public CouponType convertToEntityAttribute(String code) {
        return CouponType.convert(code);
    }
}
