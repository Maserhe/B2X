package com.jpg6.gulimall.order;

import com.jpg6.gulimall.order.dao.OrderDao;
import com.jpg6.gulimall.order.entity.OrderEntity;
import com.jpg6.gulimall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    private OrderService orderService;

    @Test
    void contextLoads() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setGrowth(12);


        boolean save = orderService.save(orderEntity);
        System.out.println(save);

    }




}
