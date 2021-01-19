package com.atguigu.gulimall.product;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 3、JSR303
 *   1）、给Bean添加校验注解:javax.validation.constraints，并定义自己的message提示
 *   2)、开启校验功能@Valid
 *      效果：校验错误以后会有默认的响应；
 *   3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
 *   4）、分组校验（多场景的复杂校验）
 *         1)、	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
 *          给校验注解标注什么情况需要进行校验
 *         2）、@Validated({AddGroup.class})
 *         3)、默认没有指定分组的校验注解@NotBlank，在分组校验情况@Validated({AddGroup.class})下不生效，只会在@Validated生效；
 *
 *   5）、自定义校验
 *      1）、编写一个自定义的校验注解
 *      2）、编写一个自定义的校验器 ConstraintValidator
 *      3）、关联自定义的校验器和自定义的校验注解
 *      @Documented
 * @Constraint(validatedBy = { ListValueConstraintValidator.class【可以指定多个不同的校验器，适配不同类型的校验】 })
 * @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 * @Retention(RUNTIME)
 * public @interface ListValue {
4、统一的异常处理
 * @ControllerAdvice
 *  1）、编写异常处理类，使用@ControllerAdvice。
 *  2）、使用@ExceptionHandler标注方法可以处理的异常。

5、模板引擎
    1） thymeleaf
    2)静态资源都放在static文件夹下就可以按照路径直接访问
    3）页面放在templates下，直接访问
        Springboot，访问项目的时候，默认会找index
        WebMvcAutoConfiguration
        ResourceProperties
    4) 项目不重启，thymeleaf更新
        引入dev
        ctrl+shift+F9”重新编译页面或“ctrl+F9”重新编译整个项目。

6、整合redis
    1) 引入data-redis-starter
    2) 简单配置redis的host等信息 application.yml
    3）使用SpringBoot自动配置好的StringRedisTemplate来操作redis

   7、整合redisson
   1）引入依赖pom
   2) 配置类 MyRedisConfig

     */
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@MapperScan("com.atguigu.gulimall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
