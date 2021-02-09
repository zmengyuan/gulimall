package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
 * 5、监听消息，使用@RabbitListener 必须有@EnableRabbit 。其实不监听可以不用@EnableRabbit
 */
@EnableRabbit
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
