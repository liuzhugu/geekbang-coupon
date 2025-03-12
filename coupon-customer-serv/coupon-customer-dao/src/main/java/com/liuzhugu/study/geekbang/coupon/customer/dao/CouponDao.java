package com.liuzhugu.study.geekbang.coupon.customer.dao;

import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponDao extends JpaRepository<Coupon,Long> {

    // 根据用户ID和Template ID，统计用户从当前优惠券模板中领了多少张券
    long countByUserIdAndTemplateId(Long userId,Long templateId);
}
