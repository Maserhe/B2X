package com.jpg6.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuHasStockVo {

    private Long skuId;
    private Boolean hasStock;

}
