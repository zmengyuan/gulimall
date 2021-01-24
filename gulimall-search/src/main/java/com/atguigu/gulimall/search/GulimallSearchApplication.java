package com.atguigu.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
ES中的数据是保存在内存中的，所以我们只保存有用的数据。
sku的title ,sku的价格，sku的价格
spu的品牌，spu的分类，spu的规格

（1）方便检索
 {skuId:
 spuId:
 skuTitle:
 price:
 saleCount:
 attrs[{尺寸：xx},{颜色:xx}]
 }
 这样attrs其实是冗余存储的
 冗余： 100万数据* 20kb  = 2000MB = 2G  内存

 （2）
 sku索引{
 skuId:
 spuId:
 xxxx
 }
 attr索引{
    spuId:
    attrs:[{尺寸：xx},{颜色:xx}]
 }

 其实它上面的可选项是根据选择的结果又分析聚合了的
搜索 小米 粮食  手机 电器
10000个，4000个spu
分布：4000个spu的所有可能属性
esClient spuId[4000个] * 8字节= 32kb

32kb * 10000 = 32mb
如果百万访问 32kb * 1000000 = 32GB

（3）es中数据结构
PUT product
{
    "mappings":{
        "properties": {
            "skuId":{
                "type": "long"
            },
            "spuId":{
                "type": "keyword"
            },
            "skuTitle": {
                "type": "text",
                "analyzer": "ik_smart"
            },
            "skuPrice": {
                "type": "keyword"
            },
            "skuImg":{
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "saleCount":{
                "type":"long"
            },
            "hasStock": {
                "type": "boolean"
            },
            "hotScore": {
                "type": "long"
            },
            "brandId": {
                "type": "long"
            },
            "catalogId": {
                "type": "long"
            },
            "brandName": {
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "brandImg":{
                "type": "keyword",
                 "index": false,
                "doc_values": false
            },
            "catalogName": {
                "type": "keyword",
                "index": false,
                "doc_values": false
            },
            "attrs": {
                "type": "nested",
                "properties": {
                    "attrId": {
                        "type": "long"
                    },
                    "attrName": {
                        "type": "keyword",
                        "index": false,
                        "doc_values": false
                    },
                    "attrValue": {
                        "type": "keyword"
                    }
                }
            }
        }
    }
}


 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSearchApplication.class, args);
    }

}
