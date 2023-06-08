package com.jpg6.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.gulimall.product.entity.AttrEntity;
import com.jpg6.gulimall.product.vo.AttrRespVo;
import com.jpg6.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    List<AttrEntity> getRealtionAttr(Long attrgroupId);

    PageUtils noAttrRelation(Map<String, Object> params, Long attrgroupId);
}

