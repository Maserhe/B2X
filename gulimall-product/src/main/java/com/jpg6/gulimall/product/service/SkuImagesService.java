package com.jpg6.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);


    List<SkuImagesEntity> getImagesBySkuId(Long skuId);
}

