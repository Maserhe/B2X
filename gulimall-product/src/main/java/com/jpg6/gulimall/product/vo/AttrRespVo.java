package com.jpg6.gulimall.product.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AttrRespVo extends AttrVo implements Serializable {
    private static final long serialVersionUID = 1L;


    private String catelogName;

    private String groupName;

    private Long[] catelogPath;

}
