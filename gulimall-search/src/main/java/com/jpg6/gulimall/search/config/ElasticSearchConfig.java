package com.jpg6.gulimall.search.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {


    @Bean
    public RestHighLevelClient esClient() {
        RestHighLevelClient restClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.10", 9200, "http"),
                        new HttpHost("192.168.56.10", 9201, "http"))
        );
        return restClient;
    }


    public static RequestOptions COMMON_OPTION;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTION = builder.build();
    }


}
