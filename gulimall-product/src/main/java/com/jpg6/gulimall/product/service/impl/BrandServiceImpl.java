package com.jpg6.gulimall.product.service.impl;

import com.jpg6.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.product.dao.BrandDao;
import com.jpg6.gulimall.product.entity.BrandEntity;
import com.jpg6.gulimall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {


        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }


    /**
     * 更新 自己的表，也要更新冗余存储
     * @param brand
     */
    @Override
    public void undateDetail(BrandEntity brand) {
        // 1， 更新自己
        this.updateById(brand);
        // 2， 更新冗余
        if (! StringUtils.isEmpty(brand.getName())) {
            // 同步更新 关联表中的 数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());

            //TODO 更新其他有关联关系的 表

        }

    }
}