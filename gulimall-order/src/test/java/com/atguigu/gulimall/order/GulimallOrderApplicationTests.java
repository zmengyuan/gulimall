package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;
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

import java.util.Date;

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
        OrderReturnApplyEntity orderReturnApplyEntity = new OrderReturnApplyEntity();
        orderReturnApplyEntity.setId(1L);
        orderReturnApplyEntity.setCreateTime(new Date());
        orderReturnApplyEntity.setReturnName("哈哈哈");
        //1、发送消息，如果发送的消息是个对象，我们会使用序列化机制，将对象写出去。对象必须实现Serializable
        String msg = "hello word";

        //2、配置MyRabbitConfig，让发送的对象类型的消息，可以是一个json
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnApplyEntity);
        log.info("消息发送完成{}",msg);
    }

}
