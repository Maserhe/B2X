package com.jpg6.gulimall.product.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jpg6.gulimall.product.entity.SkuImagesEntity;
import com.jpg6.gulimall.product.entity.SpuInfoDescEntity;
import com.jpg6.gulimall.product.service.*;
import com.jpg6.gulimall.product.vo.SkuItemSaleAttrVo;
import com.jpg6.gulimall.product.vo.SkuItemVo;
import com.jpg6.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.product.dao.SkuInfoDao;
import com.jpg6.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor  pool;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }


    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.ZERO) == 1) {
            queryWrapper.le("price", max);
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    /**
     * 查询所哟 sku 具体商品
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getSkusById(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skuInfoEntities;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemVo skuItemVo  = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1, sku 基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, pool);

        CompletableFuture<Void> saleFuture = infoFuture.thenAcceptAsync((res) -> {
            // 3, spu 销售属性
            Long spuId = res.getSpuId();
            List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }, pool);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            // 4， spu 介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfoDescEntity);
        }, pool);

        CompletableFuture<Void> attrFuture = infoFuture.thenAcceptAsync((res) -> {
            // 5， 规格参数
            List<SpuItemAttrGroupVo> spuItemAttrVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(spuItemAttrVos);
        }, pool);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            // 2, sku 图片 pms_sku_images
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, pool);

        CompletableFuture.allOf(saleFuture, descFuture, attrFuture, imagesFuture).get();

        return skuItemVo;
    }
}