package com.jpg6.gulimall.product.vo;

import com.jpg6.gulimall.product.entity.SkuImagesEntity;
import com.jpg6.gulimall.product.entity.SkuInfoEntity;
import com.jpg6.gulimall.product.entity.SpuInfoDescEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuItemVo {


    SkuInfoEntity info;

    private boolean hasStock = true;

    List<SkuImagesEntity> images;


    SpuInfoDescEntity desc;


    List<SpuItemAttrGroupVo> groupAttrs;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;



}
