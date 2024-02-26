package com.jpg6.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemSaleAttrVo {

    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;

    private List<String> attrValues;

}