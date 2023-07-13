package com.jpg6.gulimall.search;

import com.jpg6.gulimall.search.config.ElasticSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {


    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
    public void contextLoads() throws IOException {

        System.out.println(restHighLevelClient);

        IndexRequest request = new IndexRequest("users");
        request.source("name", "123").id("1");

        IndexResponse response = restHighLevelClient.index(request, ElasticSearchConfig.COMMON_OPTION);

        System.out.println(response);
    }


    @Test
    public void searchData() throws IOException {

        SearchRequest request = new SearchRequest();
        request.indices("bank");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.termQuery("age", 30));
        sourceBuilder.from(10);
        sourceBuilder.size(10);


        request.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(request, ElasticSearchConfig.COMMON_OPTION);

        System.out.println(sourceBuilder.toString());
        System.out.println(searchResponse);
    }

}
