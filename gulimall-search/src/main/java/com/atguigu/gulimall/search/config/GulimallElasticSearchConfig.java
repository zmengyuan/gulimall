package com.atguigu.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1、导入相关依赖
 * 2、编写配置类，给容器中注入一个RestHighLevelClient
 * 3、操作es
 */
@Configuration
public class GulimallElasticSearchConfig {
    @Bean
    public RestHighLevelClient esRestCient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.9",9200,"http")
                )
        );
        return client;
    }
}

