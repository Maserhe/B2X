package com.jpg6.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.jpg6.gulimall.product.entity.AttrEntity;
import com.jpg6.gulimall.product.service.AttrAttrgroupRelationService;
import com.jpg6.gulimall.product.service.AttrService;
import com.jpg6.gulimall.product.service.CategoryService;
import com.jpg6.gulimall.product.vo.AttrGroupRelationVo;
import com.jpg6.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.mysql.cj.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jpg6.gulimall.product.entity.AttrGroupEntity;
import com.jpg6.gulimall.product.service.AttrGroupService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.R;



/**
 * 属性分组
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    // /product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> entities = attrService.getRealtionAttr(attrgroupId);
        return R.ok().put("data", entities);
    }


    /**
     * 查询某个三级分类下 的所有 属性分组
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {

        List<AttrGroupWithAttrsVo> vos =  attrGroupService.getAttrGroupWithAttrs(catelogId);

        return R.ok().put("data", vos);
    }



    /**
     * 关联关系的删除
     * @param relationVos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] relationVos) {
        attrGroupService.deleRelation(relationVos);
        return R.ok();
    }

    /**
     *     获取属性分组没有关联的其他属性
     * @param params
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noAttrRelation(@RequestParam Map<String, Object> params, @PathVariable("attrgroupId") Long attrgroupId) {
        PageUtils page = attrService.noAttrRelation(params, attrgroupId);

        return R.ok().put("page", page);
    }


    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos) {
        relationService.saveBatch(vos);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId){
   //     PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] categoryPath = categoryService.findCategoryPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(categoryPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }

}
