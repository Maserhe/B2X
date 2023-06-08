package com.jpg6.gulimall.product.dao;

import com.jpg6.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品三级分类
 * 
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    /**
     * 更新 分类的名字
     * @param catId
     * @param name
     */
    void updateCategory(@Param("catId") Long catId, @Param("name") String name);
	
}
