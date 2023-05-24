package com.jpg6.gulimall.product;

import com.jpg6.gulimall.product.dao.BrandDao;
import com.jpg6.gulimall.product.entity.BrandEntity;
import com.jpg6.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {

        BrandEntity be = new BrandEntity();
        be.setName("123456");
        brandService.save(be);

        System.out.println(brandService.list());
    }

}
