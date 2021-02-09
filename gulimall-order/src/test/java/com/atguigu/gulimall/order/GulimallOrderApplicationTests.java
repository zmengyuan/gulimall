package com.atguigu.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GulimallOrderApplicationTests {
    @Autowired
    AmqpAdmin amqpAdmin;

    /**
     * 如何创建Exchange、queue、binding
     *  使用AmqpAdmin
     * 如何收发消息
     */
    @Test
    public void contextLoads() {
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功","hello-java-exchange");
    }

    @Test
    public void createQueue(){
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功","hello-java-queue");
    }

    @Test
    public void createBinding(){
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功","hello-java-binding");
    }

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     *  发送消息
     */
    @Test
    public void sendMessageTest(){
        //1、发送消息，
        String msg = "hello word";
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",msg);
        log.info("消息发送完成{}",msg);
    }

}
