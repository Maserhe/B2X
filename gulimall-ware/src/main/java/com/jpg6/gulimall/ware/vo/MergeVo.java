package com.jpg6.gulimall.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeVo {

    /**
     * 整单id
     */
    private Long purchaseId;

    /**
     * 需要合并的采购项
     */
    private List<Long> items;

}
