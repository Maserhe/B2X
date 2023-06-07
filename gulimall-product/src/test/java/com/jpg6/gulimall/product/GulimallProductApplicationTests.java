package com.jpg6.gulimall.product;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.jpg6.gulimall.product.dao.BrandDao;
import com.jpg6.gulimall.product.entity.BrandEntity;
import com.jpg6.gulimall.product.entity.CategoryEntity;
import com.jpg6.gulimall.product.service.BrandService;
import com.jpg6.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;


@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    void contextLoads() {

        BrandEntity be = new BrandEntity();
        be.setName("123456");
        brandService.save(be);

        System.out.println(brandService.list());
    }


    @Autowired
    private OSS ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {

        ossClient.putObject("gulimall-maserhe", "谷粒商城-微服务架构图.jpg", new FileInputStream("C:\\Users\\DAYONE\\Documents\\谷粒商城\\课件和文档\\高级篇\\资料图片\\谷粒商城-微服务架构图.jpg"));
    }

    @Test
    public void getCategoryPath() {

        Long[] path = categoryService.findCategoryPath(225L);

        System.out.println(Arrays.asList(path));



    }
}
