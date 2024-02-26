package com.jpg6.gulimall.product.dao;

import com.jpg6.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpg6.gulimall.product.vo.SkuItemVo;
import com.jpg6.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {


    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId")  Long catalogId);
}
