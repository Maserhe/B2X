package com.jpg6.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void undateDetail(BrandEntity brand);
}

