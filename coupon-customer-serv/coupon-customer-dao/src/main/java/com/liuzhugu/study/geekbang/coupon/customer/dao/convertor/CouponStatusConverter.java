package com.liuzhugu.study.geekbang.coupon.customer.dao.convertor;

import com.liuzhugu.study.geekbang.coupon.customer.api.enums.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class CouponStatusConverter implements AttributeConverter<CouponStatus,Integer> {

    //如果需要把DB里的值转换成enum对象 就采用这种方式
    //利用泛型模板基础AttributeConverter

    //enum转DB value
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    //DB value 转enum值
    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return  CouponStatus.convert(code);
    }
}
