package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    RestHighLevelClient client;
    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest ("users");
        indexRequest.id("1");//数据的id
//        indexRequest.source("username","zhangsan","age",18,"gender","男");  //第一种方式

        User user = new User();
        user.setUserName("张三");
        user.setAge(20);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        //设置要保存的内容
        indexRequest.source(jsonString, XContentType.JSON);
        //执行创建索引和保存数据
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(index);

    }
    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.x/java-rest-high-search.html
    /*
    复杂检索:在bank中搜索address中包含mill的所有人的年龄分布以及平均年龄，平均薪资
     */
    @Test
    public void searchData() throws IOException{
//        1、创建检索请求
        SearchRequest searchRequest = new SearchRequest();
//        指定索引
        searchRequest.indices("bank");
//        指定DSL，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构造检索条件  DSL 根 可以写的，就是searchSourceBuilder可以写的
//        searchSourceBuilder.query();
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        sourceBuilder.query(QueryBuilders.matchQuery("address","Mill"));

        //1.2.1)按照年龄分布进行聚合
        TermsAggregationBuilder ageAgg= AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);

        //1.2.2)计算平均年龄
        AvgAggregationBuilder ageAvg = AggregationBuilders.avg("ageAvg").field("age");
        sourceBuilder.aggregation(ageAvg);
        //1.2.3)计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAvg);


        System.out.println("查询参数"+sourceBuilder.toString());


        searchRequest.source(sourceBuilder);

//        2、执行检索
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

//        3、分析结果
        System.out.println("结果是"+searchResponse.toString());


        //3. 将检索结果封装为Bean
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String sourceAsString = searchHit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);

        }


        //4. 获取聚合信息
        Aggregations aggregations = searchResponse.getAggregations();

        Terms ageAgg1 = aggregations.get("ageAgg");

        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+keyAsString+" ==> "+bucket.getDocCount());
        }
        Avg ageAvg1 = aggregations.get("ageAvg");
        System.out.println("平均年龄："+ageAvg1.getValue());

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资："+balanceAvg1.getValue());
    }
    @Data
    @ToString
    static class Account{
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private Integer age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }
}
