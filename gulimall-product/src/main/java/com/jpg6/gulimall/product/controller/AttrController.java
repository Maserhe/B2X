package com.jpg6.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.jpg6.gulimall.product.vo.AttrRespVo;
import com.jpg6.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jpg6.gulimall.product.entity.AttrEntity;
import com.jpg6.gulimall.product.service.AttrService;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.R;



/**
 * 商品属性
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-24 19:20:54
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;


//    /**
//     * 分页查询
//     * @param params
//     * @param catelogId
//     * @return
//     */
//    @GetMapping("/base/list/{catelogId}")
//    public R baseAttrList(@RequestParam Map<String,Object> params, @PathVariable Long catelogId) {
//
//        PageUtils page = attrService.queryBaseAttrPage(params, catelogId);
//        return R.ok().put("page", page);
//    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		// AttrEntity attr = attrService.getById(attrId);

        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     *  获取分类的 销售属性
     * @param params
     * @param type
     * @param catelogId
     * @return
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,
                          @PathVariable("attrType") String type,
                          @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);
        return R.ok().put("page", page);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		// attrService.save(attr);

        attrService.saveAttr(attrVo);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo){
		// attrService.updateById(attr);
        attrService.updateAttr(attrVo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
