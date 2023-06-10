package com.jpg6.gulimall.coupon.service.impl;

import com.jpg6.common.to.MemberPrice;
import com.jpg6.common.to.SkuReductionTo;
import com.jpg6.gulimall.coupon.entity.MemberPriceEntity;
import com.jpg6.gulimall.coupon.entity.SkuLadderEntity;
import com.jpg6.gulimall.coupon.service.MemberPriceService;
import com.jpg6.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.coupon.dao.SkuFullReductionDao;
import com.jpg6.gulimall.coupon.entity.SkuFullReductionEntity;
import com.jpg6.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {


    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }



    @Override
    public void saveReduction(SkuReductionTo skuReductionTo) {
        // 1, 保存满减打折 满几件打几折
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        ladderEntity.setSkuId(skuReductionTo.getSkuId());
        ladderEntity.setFullCount(skuReductionTo.getFullCount());
        ladderEntity.setDiscount(skuReductionTo.getDiscount());
        ladderEntity.setAddOther(skuReductionTo.getCountStatus());

        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(ladderEntity);
        }

        // 2, 满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuFullReductionEntity.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            this.save(skuFullReductionEntity);
        }

        // 3, 会员价格

        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();

        List<MemberPriceEntity> priceEntities = memberPrice.stream().filter(entity-> entity.getPrice().compareTo(BigDecimal.ZERO) == 1)
                .map(t -> {
            MemberPriceEntity priceEntity = new MemberPriceEntity();
            priceEntity.setSkuId(skuReductionTo.getSkuId());
            priceEntity.setMemberLevelId(t.getId());
            priceEntity.setMemberPrice(t.getPrice());
            priceEntity.setMemberLevelName(t.getName());
            // 默认有其他优惠
            priceEntity.setAddOther(1);

            return priceEntity;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(priceEntities);

    }
}