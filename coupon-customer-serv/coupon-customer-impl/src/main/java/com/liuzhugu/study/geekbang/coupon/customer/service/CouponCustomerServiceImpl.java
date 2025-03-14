package com.liuzhugu.study.geekbang.coupon.customer.service;

import com.google.common.collect.Lists;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.ShoppingCart;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationOrder;
import com.liuzhugu.study.geekbang.coupon.calculation.api.beans.SimulationResponse;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.RequestCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.beans.SearchCoupon;
import com.liuzhugu.study.geekbang.coupon.customer.api.enums.CouponStatus;
import com.liuzhugu.study.geekbang.coupon.customer.dao.CouponDao;
import com.liuzhugu.study.geekbang.coupon.customer.dao.entity.Coupon;
import com.liuzhugu.study.geekbang.coupon.customer.feign.CalculationService;
import com.liuzhugu.study.geekbang.coupon.customer.feign.TemplateService;
import com.liuzhugu.study.geekbang.coupon.customer.service.intf.CouponCustomerService;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponInfo;
import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
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

    //将调用信息通过注解声明在接口上   这样就避免调用信息侵入客户端代码  做好隔离让客户端像本地调用一样
    //OpenFeign生成代理类  根据注解声明的远程调用信息  当调用该接口方法时生成相应远程调用的request发起远程调用
    @Autowired
    private TemplateService templateService;
    @Autowired
    private CalculationService calculationService;

    //通过远程调用避免引入实现逻辑  划分服务边界   除非交互的接口标准变更  否则对方实现逻辑的变更不会影响到该系统
//    @Autowired
//    private WebClient.Builder webClientBuilder;

    //直接导入实现逻辑的话   当对方发生变动   本项目也需要 引入最新的然后重新打包发布
//    @Autowired
//    private CouponCalculationService calculationService;
//
//    @Autowired
//    private CouponTemplateService templateService;

    /**
     * 用户领取优惠券
     * */
    @Override
    public Coupon requestCoupon(RequestCoupon request) {
        //改为远程调用
        CouponTemplateInfo templateInfo = templateService.getTemplate(request.getCouponTemplateId());

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
                .templateInfo(templateInfo)
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
            couponInfo.setTemplate(templateService.getTemplate(coupon.getTemplateId()));
            order.setCouponInfos(Lists.newArrayList(couponInfo));
        }

        //order清算
        ShoppingCart checkoutInfo = calculationService.checkout(order);

        if(coupon != null) {
            //如果优惠券没有被结算掉  而用户传递了优惠券  报错提示该订单满足不了优惠条件
            if (CollectionUtils.isEmpty(checkoutInfo.getCouponInfos())) {
                log.error("cannot apply coupon to order, couponId={}", coupon.getId());
                throw new IllegalArgumentException("coupon is not applicable to this order");
            }

            log.info("update coupon status to used, couponId={}", coupon.getId());
            coupon.setStatus(CouponStatus.USED);
            couponDao.save(coupon);
        }

        return checkoutInfo;
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
                couponInfo.setTemplate(templateService.getTemplate(coupon.getTemplateId()));
                couponInfos.add(couponInfo);
            }
            order.setCouponInfos(couponInfos);
        }

        //2.利用优惠券信息进行试算
        return calculationService.simulate(order);
    }

    /**
     * 逻辑删除优惠券
     * */
    @Override
    public void deleteCoupon(Long userId, Long couponId) {
        Coupon example = Coupon.builder()
                .userId(userId)
                .id(couponId)
                .status(CouponStatus.AVAILABLE)
                .build();

        Coupon coupon = couponDao.findAll(Example.of(example))
                .stream()
                .findFirst()
                // 如果找不到券，就抛出异常
                .orElseThrow(() -> new RuntimeException("Could not find available coupon"));

        coupon.setStatus(CouponStatus.INACTIVE);
        couponDao.save(coupon);
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
        //发起请求批量查询券模板
        Map<Long, CouponTemplateInfo> templateMap = templateService.getTemplateInBatch(templateIds);
        //挨个组装
        coupons.stream()
                .forEach(e -> e.setTemplateInfo(templateMap.get(e.getTemplateId())));
        //遍历转换状态枚举值
        return coupons.stream()
                .map(CouponConverter::coverToCoupon)
                .collect(Collectors.toList());
    }
}
