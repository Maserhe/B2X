package com.jpg6.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {

    // 1级父分类
    private String catalog1Id;

    // 3级子分类
    private List<Catalog3Vo> catalog3List;
    private String id;
    private String name;

    /**
     * 三级分类 vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo {
        private String catalog2Id;

        private String id;

        private String name;


    }

}
