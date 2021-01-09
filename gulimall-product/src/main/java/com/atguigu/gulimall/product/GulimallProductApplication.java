package com.atguigu.gulimall.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
1、整合mybatis-plus
    1)导入依赖
    <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatisplus.version}</version>
        </dependency>
    2）配置
        1、数据源
            1)数据库驱动
            <!-- 导入mysql 驱动-->
    <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>${mysql.version}</version>
    </dependency>
            2)配置application.yml中的数据库
        2、配置mybatis-plus
            1） 使用@MapperScan()
            2） 告诉mybatis-plus,xml在哪里
            mybatis-plus:
                mapper-location
* 2、逻辑删除
 *  1）、配置全局的逻辑删除规则（省略）默认1-启动 0-删除
 *  2）、配置逻辑删除的组件Bean（省略）3.1.1可以不配置了
 *  3）、给Bean加上逻辑删除注解@TableLogic
 */
@MapperScan("com.atguigu.gulimall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
