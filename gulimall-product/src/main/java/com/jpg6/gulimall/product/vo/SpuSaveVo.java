/**
  * Copyright 2023 bejson.com 
  */
package com.jpg6.gulimall.product.vo;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2023-06-08 20:33:24
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long brandId;
    private Long catalogId;


    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;

}