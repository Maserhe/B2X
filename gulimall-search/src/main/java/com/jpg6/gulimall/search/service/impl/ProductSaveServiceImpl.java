package com.jpg6.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.jpg6.common.to.es.SkuEsModel;
import com.jpg6.gulimall.search.config.ElasticSearchConfig;
import com.jpg6.gulimall.search.constant.EsConstant;
import com.jpg6.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {


    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        // 保存到 es
        // 1, 给 es 中建立索引 product， 建立好映射关系

        BulkRequest bulkRequest = new BulkRequest();

        // 循环添加数据
        for (SkuEsModel model: skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            // 数据, json 格式
            indexRequest.source(JSON.toJSONString(model), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkItemResponses = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTION);

        // 可能会有商品上架失败
        //TODO 如果有批量上架错误
        boolean hasFailures = bulkItemResponses.hasFailures();

        List<String> collect = Arrays.stream(bulkItemResponses.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成： {}", collect);

        return hasFailures;
    }
}
