package com.jpg6.gulimall.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDoneVo {

    /**
     * 采购单id
     */
    private Long id;

    private List<PurchaseItemVo> items;


}
