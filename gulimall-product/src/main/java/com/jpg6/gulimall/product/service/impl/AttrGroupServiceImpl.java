package com.jpg6.gulimall.product.service.impl;

import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.aliyuncs.utils.StringUtils;
import com.jpg6.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.jpg6.gulimall.product.dao.AttrDao;
import com.jpg6.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.jpg6.gulimall.product.entity.AttrEntity;
import com.jpg6.gulimall.product.service.AttrService;
import com.jpg6.gulimall.product.vo.AttrGroupRelationVo;
import com.jpg6.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.jpg6.gulimall.product.vo.SkuItemVo;
import com.jpg6.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private AttrAttrgroupRelationDao relationDao;


    @Autowired
    private AttrGroupDao attrGroupDao;


    @Autowired
    private AttrService attrService;


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

        String key = (String) params.get("key");
        // 三级分类 查询
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj)-> {
                obj.like("attr_group_id", key).or().like("attr_group_name", key);
            });
        }

        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", String.valueOf(catelogId));
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 批量删除 关联关系
     * @param relationVos
     */
    @Override
    public void deleRelation(AttrGroupRelationVo[] relationVos) {
        // new QueryWrapper<AttrGroupEntity>().eq("attr_id")
        List<AttrAttrgroupRelationEntity> groupEntities = Arrays.asList(relationVos).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrGroupEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrGroupEntity);
            return attrGroupEntity;
        }).collect(Collectors.toList());

        relationDao.deleBatchRelation(groupEntities);

    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId) {

        //1, 查出所有属性分组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = groupEntities.stream().map(t -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(t, attrGroupWithAttrsVo);

            //2， 查出每个属性分组的 所有属性
            List<AttrEntity> attrs = attrService.getRealtionAttr(t.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(attrs);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return attrGroupWithAttrsVos;
    }


    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        // 1, 查出当前spu 对应的所有属性的分组信息以及 当前分组下所有属性对应的值
        AttrGroupDao groupDao = this.baseMapper;
        List<SpuItemAttrGroupVo> res = groupDao.getAttrGroupWithAttrsBySpuId(spuId, catalogId);

        return res;
    }
}