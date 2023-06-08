package com.jpg6.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jpg6.gulimall.product.dao.BrandDao;
import com.jpg6.gulimall.product.dao.CategoryDao;
import com.jpg6.gulimall.product.entity.BrandEntity;
import com.jpg6.gulimall.product.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.product.dao.CategoryBrandRelationDao;
import com.jpg6.gulimall.product.entity.CategoryBrandRelationEntity;
import com.jpg6.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {


    @Autowired
    private CategoryDao categoryDao;


    @Autowired
    private BrandDao brandDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 级联更新 所有相关的表格
     * @param catId
     * @param name
     */
    @Override
    public void updateCategory(Long catId, String name) {
        categoryDao.updateCategory(catId, name);
    }

    /**
     * 更新brand 名
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();

        categoryBrandRelationEntity.setBrandName(name);
        categoryBrandRelationEntity.setBrandId(brandId);

        this.update(categoryBrandRelationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));

    }

    /**
     * 查询 品牌分类的 关联关系
     * @param categoryBrandRelation
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        // 查询出 品牌名， 和 分类名
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        // 保存
        baseMapper.insert(categoryBrandRelation);
    }
}