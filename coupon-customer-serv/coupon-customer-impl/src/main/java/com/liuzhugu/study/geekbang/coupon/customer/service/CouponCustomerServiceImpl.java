package com.liuzhugu.study.geekbang.coupon.customer.service;

import com.google.common.collect.Lists;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.calculation.service.intf.CouponCalculationService;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.enums.CouponStatus;
import com.liuzhugu.study.geekbang.coupon.customer.dao.CouponDao;
import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import com.liuzhugu.study.geekbang.coupon.customer.service.intf.CouponCustomerService;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
import com.liuzhugu.study.geekbang.coupon.template.service.intf.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private CouponCalculationService calculationService;

    @Autowired
    private CouponTemplateService templateService;

    /**
     * 用户领取优惠券
     * */
    @Override
    public Coupon requestCoupon(RequestCoupon request) {
        CouponTemplateInfo templateInfo = templateService.loadTemplateInfo(request.getCouponTemplateId());

        //模板不存在则报错
        if (templateInfo == null) {
            log.error("invalid template id = {}",request.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }

        //模板不能过期
        long now = Calendar.getInstance().getTimeInMillis();
        Long expTime = templateInfo.getRule().getDeadline();
        if (expTime != null && now >= expTime || BooleanUtils.isFalse(templateInfo.isAvailable())) {
            log.error("template is not avaliable id = {}",request.getCouponTemplateId());
            throw new IllegalArgumentException("template is unavaliable");
        }

        //用户领券数量超过上限
        Long count = couponDao.countByUserIdAndTemplateId(request.getUserId(),request.getCouponTemplateId());
        if (count >= templateInfo.getRule().getLimitation()) {
            log.error("exceeds maximum number");
            throw new IllegalArgumentException("exceeds maximum number");
        }

        Coupon coupon = Coupon.builder()
                .templateId(request.getCouponTemplateId())
                .userId(request.getUserId())
                .shopId(request.getUserId())
                .status(CouponStatus.AVAILABLE)
                .build();
        couponDao.save(coupon);
        return coupon;
    }

    //核销优惠券
    @Override
    @Transactional
    public ShoppingCart placeOrder(ShoppingCart order) {
        if(CollectionUtils.isEmpty(order.getProducts())) {
            log.error("invalid check out request, order={}", order);
            throw new IllegalArgumentException("cart if empty");
        }

        Coupon coupon = null;
        if (order.getCouponId() != null) {
            //如果有优惠券  验证是否可用  并且是当前用户的
            Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(order.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();
            coupon = couponDao.findAll(Example.of(example))
                .stream()
                .findFirst()
                    //找不到券就抛出异常
            .orElseThrow(() -> new RuntimeException("Coupon not found"));

            CouponInfo couponInfo = CouponConverter.coverToCoupon(coupon);
            couponInfo.setTemplate(templateService.loadTemplateInfo(coupon.getTemplateId()));
            order.setCouponInfos(Lists.newArrayList(couponInfo));
        }

        //order清算
        ShoppingCart checkoutInfo = calculationService.calculateOrderPrice(order);

        return null;
    }

    /**
     * 用户试算优惠
     * */
    @Override
    public SimulationResponse simulateOrderPrice(SimulationOrder order) {
        //层层传递参数  逐层对参数进行加工

        //1.组装优惠券信息
        List<CouponInfo> couponInfos = Lists.newArrayList();
        //挨个循环 把优惠券信息加载出来 但高并发场景下不能这么一个个循环  更好的做法是批量查询
        //而且券模板一旦创建不会修改内容  所以在创建端做数据异构放到缓存里  使用端从缓存捞template信息
        for (Long couponId : order.getCouponIDs()) {
            Coupon exmaple = Coupon.builder()
                    .userId(order.getUserId())
                    .id(couponId)
                    .status(CouponStatus.AVAILABLE)
                    .build();
            Optional<Coupon> couponOptional = couponDao.findAll(Example.of(exmaple))
                    .stream()
                    .findFirst();
            //加载优惠券模板信息
            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                CouponInfo couponInfo = CouponConverter.coverToCoupon(coupon);
                couponInfo.setTemplate(templateService.loadTemplateInfo(coupon.getTemplateId()));
                couponInfos.add(couponInfo);
            }
            order.setCouponInfos(couponInfos);
        }

        //2.利用优惠券信息进行试算
        return calculationService.simulateOrder(order);
    }

    @Override
    public void deleteCoupon(Long userId, Long couponId) {

    }

    /**
     * 用户查询优惠券
     * */
    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {
        //在真正的生产环境  这个接口需要做分页查询  并且查询条件封装成一个类

        Coupon example = Coupon.builder()
                .userId(request.getUserId())
                .status(CouponStatus.convert(request.getCouponStatus()))
                .shopId(request.getShopId())
                .build();

        //这里可以尝试实现分页查询
        List<Coupon> coupons = couponDao.findAll(Example.of(example));
        if (coupons.isEmpty()) {
            return Lists.newArrayList();
        }

        List<Long> templateIds = coupons.stream()
                .map(Coupon::getTemplateId)
                .collect(Collectors.toList());
        Map<Long, CouponTemplateInfo> templateMap = templateService.getTemplateInfoMap(templateIds);
        //挨个组装
        coupons.stream()
                .forEach(e -> e.setTemplateInfo(templateMap.get(e.getTemplateId())));
        //遍历转换状态枚举值
        return coupons.stream()
                .map(CouponConverter::coverToCoupon)
                .collect(Collectors.toList());
    }
}
