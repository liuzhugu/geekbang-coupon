package com.liuzhugu.study.geekbang.coupon.template.dao.converter;

import com.alibaba.fastjson.JSON;
import com.liuzhugu.study.geekbang.coupon.template.beans.rules.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 优惠券模板规则枚举转换   Json格式
 * */
@Converter
public class TemplateRuleConverter implements AttributeConverter<TemplateRule,String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule,TemplateRule.class);
    }
}
