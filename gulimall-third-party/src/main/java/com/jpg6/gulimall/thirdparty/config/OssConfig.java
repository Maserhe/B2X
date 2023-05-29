package com.jpg6.gulimall.thirdparty.config;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Value("${alicloud.oss.endpoint}")
    String endpoint;

    @Value("${alicloud.access-key}")
    String accessKeyId;

    @Value("${alicloud.secret-key}")
    String accessKeySecret;




    @Bean
    public OSS getOss() {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

}
