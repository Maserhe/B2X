package com.jpg6.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jpg6.common.utils.PageUtils;
import com.jpg6.common.utils.Query;

import com.jpg6.gulimall.order.dao.OrderItemDao;
import com.jpg6.gulimall.order.entity.OrderItemEntity;
import com.jpg6.gulimall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

//    @RabbitListener(queues = {"mytestQueue"})
//    public void receiveMessage(Message message, Channel channel) {
//        System.out.println("接收到的 消息" + message + " === type: " + message);
//
//        System.out.println(new String(message.getBody(), StandardCharsets.UTF_8));
//        try {
//            channel.basicAck(message.getMessageProperties().getDeliveryTag()
//                    , true);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}