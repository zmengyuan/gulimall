package com.atguigu.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * 分页类配置
 *
 * @doc: https://baomidou.com/guide/page.html
 *
 * @author: kaiyi
 * @create: 2020-08-23 01:36
 */
@Configuration
@EnableTransactionManagement // 开启事务
@MapperScan("com.atguigu.gulimall.product.dao")
public class MyBatisConfig {
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
    paginationInterceptor.setOverflow(true);
    // 设置最大单页限制数量，默认 500 条，-1 不受限制
    paginationInterceptor.setLimit(1000);
    // 开启 count 的 join 优化,只针对部分 left join
//    paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
    return paginationInterceptor;
  }

  @Autowired
  DataSourceProperties dataSourceProperties;

  @Bean
  public DataSource dataSource(DataSourceProperties dataSourceProperties){
    //得到数据源
    HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    if (StringUtils.hasText(dataSourceProperties.getName())){
      dataSource.setPoolName(dataSourceProperties.getName());
    }
    return new DataSourceProxy(dataSource);
  }

}
