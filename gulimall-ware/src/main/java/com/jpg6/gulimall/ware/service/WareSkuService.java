package com.jpg6.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jpg6.common.to.OrderTo;
import com.jpg6.common.to.mq.StockLockedTo;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.gulimall.ware.entity.WareSkuEntity;
import com.jpg6.gulimall.ware.vo.SkuHasStockVo;
import com.jpg6.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-25 13:08:14
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * 锁定库存
     * @param vo
     * @return
     */
    boolean orderLockStock(WareSkuLockVo vo);


    /**
     * 解锁库存
     * @param to
     */
    void unlockStock(StockLockedTo to);

    /**
     * 解锁订单
     * @param orderTo
     */
    void unlockStock(OrderTo orderTo);
}

