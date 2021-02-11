package com.atguigu.gulimall.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用rabbitmq
 * 1、引入amqp场景，RabbitAutoConfiguration就会自动生效
 * 2、给容器中自动配置了RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate
 *      所有的属性都是通过
 @ConfigurationProperties(
 prefix = "spring.rabbitmq"
 )
 public class RabbitProperties绑定
 3、给配置文件中配置spring.rabbitmq
 * 3、@EnableRabbit
 *
 * 5、监听消息，使用@RabbitListener标在类和方法上 必须有@EnableRabbit 。其实不监听可以不用@EnableRabbit
 * @RabbitHandler标在方法上，通过重载接收不同的消息
 *
 * 6、本地事务失效问题
 *  同一个对象内事务方法互调默认失效，原因，绕过了代理对象，
 *  事务是使用代理对象来控制的，
 *  解决：使用代理对象来调用事务方法
 *      1） 引入aop-starter;spring-boot-starter-aop,引入了aspectj
 *      2)  开启功能 @EnableAspectJAutoProxy 不开启的话是默认使用jdk接口代理 以后所有的动态代理都是aspectj创建的，好处就是没有接口也可以创建动态代理
 *          exposeProxy = true对外暴露代理对象
 *      3） 用代理对象 比如订单服务 (OrderServiceImpl)AopContext.currentProxy().
*
 *  7、Seata控制分布式事务
 * * 1）、每一个微服务先必须创建undo_logo;
 * * 2）、安装事务协调器；seata-server: https://github.com/seata/seata/releases
 * * 3）、整合
 * *      1、导入依赖  spring-cloud-starter-alibaba-seata  要看里面引入的seata-all-1.0.0.jar是什么版本，然后seata-server就用什么版本
 * *      2、解压并启动seata-server
 * *          registry.conf注册中心相关的配置，修改registry type=nacos
 * *          file.conf 是seata的配置中心
 * *      3、所有想要用到分布式事务的微服务使用seata DatasourceProxy代理自己的数据源
 * *      4、每个微服务，都必须导入registry.cof
 * *      file.conf  vgroup_mapping.{application.name}-fescar-service-group = "default"
 * *      5、启动测试
 * *      6、给分布式大事务的路口标注@GlobalTransactional
 * *      7、每一个远程的小事务用 @Transactional
 *  但是高并发项目并不适合用seata。后台管理系统的商品保存上架到是很适合用
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableRabbit
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
