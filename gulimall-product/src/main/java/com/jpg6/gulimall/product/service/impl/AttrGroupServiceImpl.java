package com.jpg6.gulimall.product.service.impl;

import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.aliyuncs.utils.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.product.dao.AttrGroupDao;
import com.jpg6.gulimall.product.entity.AttrGroupEntity;
import com.jpg6.gulimall.product.service.AttrGroupService;
import org.springframework.validation.annotation.Validated;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        } else {
            String key = (String) params.get("key");
            // 三级分类 查询
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", String.valueOf(catelogId));
            if (!StringUtils.isEmpty(key)) {
                wrapper.and((obj)-> {
                    obj.like("attr_group_id", key).like("attr_group_name", key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

}