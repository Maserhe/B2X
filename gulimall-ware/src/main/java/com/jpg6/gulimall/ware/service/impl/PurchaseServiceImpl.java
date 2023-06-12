package com.jpg6.gulimall.ware.service.impl;

import com.jpg6.common.constant.WareConstant;
import com.jpg6.gulimall.ware.entity.PurchaseDetailEntity;
import com.jpg6.gulimall.ware.service.PurchaseDetailService;
import com.jpg6.gulimall.ware.service.WareSkuService;
import com.jpg6.gulimall.ware.vo.MergeVo;
import com.jpg6.gulimall.ware.vo.PurchaseDoneVo;
import com.jpg6.gulimall.ware.vo.PurchaseItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.ware.dao.PurchaseDao;
import com.jpg6.gulimall.ware.entity.PurchaseEntity;
import com.jpg6.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService detailService;


    @Autowired
    private WareSkuService wareSkuService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();

        /**
         * 用户没有选择，直接新建采购单
         */
        if (purchaseId == null) {

            PurchaseEntity purchaseEntity = new PurchaseEntity();
            Date date = new Date();
            purchaseEntity.setCreateTime(date);
            purchaseEntity.setUpdateTime(date);
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            // 获取采购单id
            purchaseId = purchaseEntity.getId();
        }

        List<Long> items = mergeVo.getItems();
        final Long finalPurchaseId = purchaseId;

        // 改掉采购的 satatus
        List<PurchaseDetailEntity> detailEntities = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setId(i);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());

        detailService.updateBatchById(detailEntities);

        /**
         * 设置更新时间
         */
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);
    }


    /**
     * 领取采购单
     * @param ids
     */
    @Override
    public void reveived(List<Long> ids) {
        // 1， 采购单为 新建 或者 已经分配状态
        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item-> item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                        item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .map(item-> {
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                    return item;
                } ).collect(Collectors.toList());

        // 2， 改变采购单状态
        this.updateBatchById(purchaseEntities);
        // 3， 自己的采购单
        purchaseEntities.forEach(item -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = detailService.listDetailByPurchaseId(item.getId());

            purchaseDetailEntities.forEach(purchaseDetailEntity -> {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            });
            detailService.updateBatchById(purchaseDetailEntities);
        });
    }


    @Override
    public void done(PurchaseDoneVo doneVo) {
        //1, 改变采购单状态
        Long id = doneVo.getId();
        List<PurchaseItemVo> items = doneVo.getItems();

        //2， 改变采购项的状态
        Boolean flag = true;

        List<PurchaseDetailEntity> updates = new ArrayList<>();
        // 只要有一个不成功
        for (PurchaseItemVo item: items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(item.getStatus());
            } {
                // 进行入库操作
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 查出采购项
                PurchaseDetailEntity detail = detailService.getById(item.getItemId());
                wareSkuService.addStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum());
            }

            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        detailService.updateBatchById(updates);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());

        this.updateById(purchaseEntity);
        //3， 成功采购的进行入库

    }
}