package com.jpg6.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.jpg6.gulimall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);
}

