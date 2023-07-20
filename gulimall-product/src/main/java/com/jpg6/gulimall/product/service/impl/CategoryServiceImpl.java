package com.jpg6.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jpg6.gulimall.product.entity.CategoryBrandRelationEntity;
import com.jpg6.gulimall.product.service.CategoryBrandRelationService;
import com.jpg6.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private RedissonClient redissonClient;


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
     * cacheEvict 删除下面所有分区
     * 更新 自己包括相关的 表 cacheEvict 删除缓存
     * @param category
     */
    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
            @CacheEvict(value = {"category"}, key = "'getCatalogJson'")})
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

    /**
     *
     * 查出所有1 级分类
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 从数据库查询
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

        Map<String, List<Catelog2Vo>> catalogJson = null;

        // 0, 加锁后，先查缓存
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return result;
        }

        System.out.println("从数据库查询数据 ！！！ ");

        // 1, 查出所有1级分类
        List<CategoryEntity> level1Categorys  = getLevel1Categorys();
        // 2, 封装数据
        catalogJson = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1, 每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2Vos = null;

            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    List<CategoryEntity> level3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));

                    if (level3Catelog != null) {
                        // 封装成指定格式
                        List<Catelog2Vo.Catalog3Vo> catalog3VoList = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(catalog3VoList);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));


        return catalogJson;
    }




    @Override
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = null;

        // 0, 加锁后，先查缓存
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return result;
        }

        System.out.println("从数据库查询数据 ！！！ ");

        // 1, 查出所有1级分类
        List<CategoryEntity> level1Categorys  = getLevel1Categorys();
        // 2, 封装数据
        catalogJson = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1, 每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2Vos = null;

            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    List<CategoryEntity> level3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));

                    if (level3Catelog != null) {
                        // 封装成指定格式
                        List<Catelog2Vo.Catalog3Vo> catalog3VoList = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(catalog3VoList);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return catalogJson;
    }
}