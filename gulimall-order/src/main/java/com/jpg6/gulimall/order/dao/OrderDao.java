package com.jpg6.gulimall.order.dao;

import com.jpg6.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Maserhe
 * @email maserhelinux@gmail.com
 * @date 2023-05-25 13:00:37
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(String orderSn, Integer code, Integer payType);
}
