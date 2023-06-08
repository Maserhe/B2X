package com.jpg6.gulimall.product.service.impl;

import com.jpg6.gulimall.product.entity.CategoryBrandRelationEntity;
import com.jpg6.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.product.dao.CategoryDao;
import com.jpg6.gulimall.product.entity.CategoryEntity;
import com.jpg6.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }




    /**
     * 删除所有选中
     * @param ids
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        //TODO 1, 检查当前删除的菜单, 是否被别的地方引用
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        // 1,  查询出所有分类
        List<CategoryEntity> entites = baseMapper.selectList(null);
        // 2， 组装出父子树形结构

        // 1 级 分类
        List<CategoryEntity> level1Menus = entites.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map(menu-> {
            menu.setChildren(getChildren(menu, entites));

            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null?0: menu1.getSort()) - (menu2.getSort() == null?0: menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    /**
     * 更新 自己包括相关的 表
     * @param category
     */
    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        updateById(category);
        // 更新关联表
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     *
     * @param catelogId
     */
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        findParentPath(catelogId, paths);
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * 递归查找，所有
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return root.getCatId() == categoryEntity.getParentCid();
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null?0: menu1.getSort()) - (menu2.getSort() == null?0: menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

    private List<Long> findParentPath(Long categoryId, List<Long> paths) {
        CategoryEntity nowNode = this.getById(categoryId);
        paths.add(categoryId);
        if (nowNode.getParentCid() != 0) {
            findParentPath(nowNode.getParentCid(), paths);
        }
        return paths;
    }
}