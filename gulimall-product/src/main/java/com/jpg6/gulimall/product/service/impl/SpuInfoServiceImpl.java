package com.jpg6.gulimall.product.service.impl;

import com.jpg6.common.constant.ProductConstant;
import com.jpg6.common.to.SkuReductionTo;
import com.jpg6.common.to.SpuBoundTo;
import com.jpg6.common.to.es.SkuEsModel;
import com.jpg6.common.utils.FeignUtil;
import com.jpg6.common.utils.R;
import com.jpg6.common.vo.SkuHasStockVo;
import com.jpg6.gulimall.product.entity.*;
import com.jpg6.gulimall.product.feign.CouponFeignService;
import com.jpg6.gulimall.product.feign.SearchFeignService;
import com.jpg6.gulimall.product.feign.WareFeignService;
import com.jpg6.gulimall.product.service.*;
import com.jpg6.gulimall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.FeignUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    private SearchFeignService searchFeignService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;


    @Autowired
    private SpuImagesService imagesService;

    @Autowired
    private AttrService attrService;


    @Autowired
    private ProductAttrValueService attrValueService;


    @Autowired
    private ProductAttrValueService productAttrValueService;


    @Autowired
    private SkuInfoService skuInfoService;


    @Autowired
    private SkuImagesService skuImagesService;


    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;


    @Autowired
    private CouponFeignService couponFeignService;


    @Autowired
    private WareFeignService wareFeignService;


    @Autowired
    private BrandService brandService;


    @Autowired
    private CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    //TODO 分布式事务，失败回滚
    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {

        //1, 保存商品的基本信息 spu pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, infoEntity);
        Date date = new Date();
        infoEntity.setCreateTime(date);
        infoEntity.setUpdateTime(date);

        this.saveBaseSpuInfo(infoEntity);

        //2，保存spu的描述图片 pms_spu_info_desc
        List<String> descript = spuSaveVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", descript));

        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3， 保存spu的图片集合 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        imagesService.saveImages(infoEntity.getId(), images);

        //4， 保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();

        List<ProductAttrValueEntity> valueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());

            AttrEntity attrEntity = attrService.getById(attr.getAttrId());

            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttr(valueEntities);
        // 保存积分信息
        //5， spu对应的sku信息 gullimall_sms -> sms_spu——bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());

        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu 优惠信息失败！！！！！");
        }

        // spu 对应的sku信息
        // 1. sku基本信息 pms_sku_info
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {

            skus.forEach(item -> {
                String defaultImg = item.getImages().stream().filter(img -> img.getDefaultImg() == 1).findFirst().orElseThrow(() -> new NullPointerException()).getImgUrl();

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();


                // 2. sku的图片信息 pms_sku_images
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream()
                        .filter(entiry-> !StringUtils.isEmpty(entiry.getImgUrl()))
                        .map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());

                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);


                // 3, sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();

                List<SkuSaleAttrValueEntity> attrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(attrValueEntities);

                // 4, sku的优惠卷，满减 gulimall_sms_sms_sku_ladder\sms_sku_full_reduction\
                SkuReductionTo skuReductionTo = new SkuReductionTo();

                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(infoEntity.getId());
                skuReductionTo.setMemberPrice(item.getMemberPrice());
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku 优惠信息失败！！！！！");
                    }
                }
            });
        }


    }

    /**
     * 保存 商品的基本信息
     * @param infoEntity
     */
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }


    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {

        // 需要上架多个商品
        // 1, 组装我们需要的数据
        // 2, 查询当前spuId对应的sku信息，
        List<SkuInfoEntity> skus = skuInfoService.getSkusById(spuId);
        // 3， 封装每一个 skuEsModel
        // 查询一次attr 就行,规格属性， 所有的sku 规格属性是一样的
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrListForSpu(spuId);
        // 选出检索信息 sku 的 attr 规格属性
        List<Long> attrIds = baseAttrs.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        // 指定的所有属性集合中，选出检索属性
        List<Long> searAttrIds = attrService.selectAttrIds(attrIds);

        final List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            Long attrId = item.getAttrId();
            return searAttrIds.contains(attrId);
        }).map(item -> {
            SkuEsModel.Attrs newAttr = new SkuEsModel.Attrs();
            newAttr.setAttrId(item.getAttrId());
            newAttr.setAttrName(item.getAttrName());
            newAttr.setAttrValue(item.getAttrValue());
            return newAttr;
        }).collect(Collectors.toList());


        Map<Long, Boolean> stockMap = null;

        try {
            // 远程服务调用
            R r = wareFeignService.getSkuHasStock(skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));

            List<SkuHasStockVo> vos = FeignUtil.formatListClass(r, SkuHasStockVo.class);

            // List<SkuHasStockVo> vos = (List<SkuHasStockVo>) r.get("data");
            stockMap = vos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        } catch (Exception e) {
            log.error("库存服务远程调用失败！！！");
        }

        Map<Long, Boolean> finalStockMap = stockMap;

        List<SkuEsModel> esModels = skus.stream().map(item -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(item, esModel);
            esModel.setSkuPrice(item.getPrice());
            esModel.setSkuImg(item.getSkuDefaultImg());

            //TODO 设置库存,库存服务处理，只查询是否有

            esModel.setHasStock(finalStockMap == null? false: finalStockMap.get(item.getSkuId()));
            //TODO 热度评分
            esModel.setHotScore(0L);
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            esModel.setBrandImg(brandEntity.getLogo());
            esModel.setBrandName(brandEntity.getName());
            // 设置分类
            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());
            // 设置规格属性
            esModel.setAttrs(attrsList);

            return esModel;
        }).collect(Collectors.toList());

        // TODO 5 将数据发送给es 服务
        // System.out.println(esModels);
        R r = searchFeignService.productStatusUp(esModels);

        if (r.getCode() == 0) {
            //  远程调用成功
            //TODO 6 修改spu 状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程 调用失败
            //TODO 7 接口幂等性， 重试机制。
        }

    }

    /**
     * 根据skuId查询spu的信息
     * @param skuId
     * @return
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        //先查询sku表里的数据
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);

        //获得spuId
        Long spuId = skuInfoEntity.getSpuId();

        //再通过spuId查询spuInfo信息表里的数据
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);

        //查询品牌表的数据获取品牌名
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());
        spuInfoEntity.setBrandName(brandEntity.getName());

        return spuInfoEntity;
    }
}