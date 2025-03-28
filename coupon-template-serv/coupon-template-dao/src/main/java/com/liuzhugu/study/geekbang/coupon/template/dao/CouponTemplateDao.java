package com.liuzhugu.study.geekbang.coupon.template.dao;

import com.liuzhugu.study.geekbang.coupon.template.dao.entity.CouponTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Long> {

    //通过方法名来声明查询语句  因为jpa约定大于配置
    //构造方法名的过程需要遵循 < 起手式 >By< 查询字段 >< 连接词 > 的结构  一般用于检查查询

    //根据Shop ID查询出所有的优惠券模板
    List<CouponTemplate> findAllByShopId(Long shopId);

    //IN 查询 + 分页支持语法
    Page<CouponTemplate> findAllByIdIn(List<Long> Id,Pageable page);

    //根据shop ID + 可用状态查询店铺有多少优惠券模板
    Integer countByShopIdAndAvailable(Long shopId,boolean available);

    //将优惠券模板设为不可用
    @Modifying
    @Query("update CouponTemplate c set c.available = 0 where c.id = :id")
    int makeCouponUnavailable(@Param("id") Long id);
}
