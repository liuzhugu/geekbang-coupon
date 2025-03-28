package com.liuzhugu.study.geekbang.coupon.customer.dao;

import com.liuzhugu.study.geekbang.coupon.customer.api.enums.CouponStatus;
import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponDao extends JpaRepository<Coupon,Long> {

    // 根据用户ID和Template ID，统计用户从当前优惠券模板中领了多少张券
    long countByUserIdAndTemplateId(Long userId,Long templateId);

    //失效化某个券模板下的所有券
    @Modifying
    @Query("update Coupon c set c.status = :status where c.templateId = :templateId")
    int deleteCouponInBatch(@Param("templateId") Long templateId, @Param("status")CouponStatus status);
}
