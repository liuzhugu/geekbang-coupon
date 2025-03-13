package com.liuzhugu.study.geekbang.coupon.template.service;

import com.liuzhugu.study.geekbang.coupon.template.beans.CouponTemplateInfo;
import com.liuzhugu.study.geekbang.coupon.template.beans.PagedCouponTemplateInfo;
import com.liuzhugu.study.geekbang.coupon.template.beans.TemplateSearchParams;
import com.liuzhugu.study.geekbang.coupon.template.converter.CouponTemplateConverter;
import com.liuzhugu.study.geekbang.coupon.template.dao.CouponTemplateDao;
import com.liuzhugu.study.geekbang.coupon.template.dao.entity.CouponTemplate;
import com.liuzhugu.study.geekbang.coupon.template.enums.CouponType;
import com.liuzhugu.study.geekbang.coupon.template.service.intf.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券模板类相关操作
 * */
@Slf4j
@Service
public class CouponTemplateServiceImpl implements CouponTemplateService {

    @Autowired
    private CouponTemplateDao templateDao;


    /**
     * 创建优惠券模板
     */
    @Override
    public CouponTemplateInfo createTemplate(CouponTemplateInfo request) {
        //单个门店最多创建100张优惠券模板
        if (request.getShopId() != null) {
            Integer count = templateDao.countByShopIdAndAvailable(request.getShopId(),true);
            if (count >= 100) {
                log.error("the total of coupon template exceeds maximum number");
                throw new UnsupportedOperationException("exceeded the maxinum of coupon templates that you can create");
            }
        }

        //创建优惠券
        CouponTemplate template = CouponTemplate.builder()
                .name(request.getName())
                .description(request.getDesc())
                .category(CouponType.convert(request.getType()))
                .available(true)
                .shopId(request.getShopId())
                .rule(request.getRule())
                .build();
        template = templateDao.save(template);

        return CouponTemplateConverter.converterToTemplateInfo(template);
    }

    //克隆优惠券
    @Override
    public CouponTemplateInfo cloneTemplate(Long templateId) {
        log.info("cloning template id {}",templateId);
        CouponTemplate source = templateDao.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("invalid template ID"));
        CouponTemplate target = new CouponTemplate();
        BeanUtils.copyProperties(source,target);

        target.setAvailable(true);
        target.setId(null);

        templateDao.save(target);
        return CouponTemplateConverter.converterToTemplateInfo(target);
    }

    /**
     * 分页查询模板
     * */
    @Override
    public PagedCouponTemplateInfo search(TemplateSearchParams request) {
        //组装查询参数
        CouponTemplate example = CouponTemplate.builder()
                .shopId(request.getShopId())
                .category(CouponType.convert(request.getType()))
                .available(request.getAvailable())
                .name(request.getName())
                .build();

        Pageable page = PageRequest.of(request.getPage(),request.getPageSize());
        Page<CouponTemplate> result = templateDao.findAll(Example.of(example),page);
        //遍历结果  从模板转换成模板信息
        List<CouponTemplateInfo> couponTemplateInfos = result.stream()
                .map(CouponTemplateConverter::converterToTemplateInfo)
                .collect(Collectors.toList());

        //组装返回结果
        PagedCouponTemplateInfo response = PagedCouponTemplateInfo.builder()
                .templates(couponTemplateInfos)
                .page(request.getPage())
                .total(result.getTotalElements())
                .build();
        return response;
    }

    /**
     * 通过ID查询优惠券模板
     * */
    @Override
    public CouponTemplateInfo loadTemplateInfo(Long id) {
        Optional<CouponTemplate> template = templateDao.findById(id);
        return template.isPresent() ? CouponTemplateConverter.converterToTemplateInfo(template.get()) : null;
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        int rows = templateDao.makeCouponUnavailable(id);
        if (rows == 0) {
            throw new IllegalArgumentException("Template Not Found: " + id);
        }
    }

    /**
     * 批量读取模板
     * */
    @Override
    public Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream()
                .map(CouponTemplateConverter::converterToTemplateInfo)
                .collect(Collectors.toMap(CouponTemplateInfo::getId, Function.identity()));
    }
}
