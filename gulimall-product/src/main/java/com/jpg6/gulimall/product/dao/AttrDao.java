package com.jpg6.gulimall.product.dao;

import com.jpg6.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectAttrIds(@Param("attrIds") List<Long> attrIds);
}
