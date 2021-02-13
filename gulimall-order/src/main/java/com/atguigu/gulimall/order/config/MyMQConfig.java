package com.atguigu.gulimall.order.config;

import com.atguigu.common.constant.RabbitConstant;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class MyMQConfig {
    @RabbitListener(queues = "order.release.queue")
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        log.info("收到过期的订单信息，准备关闭订单,{}",orderEntity.toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }


    //@Bean Binding\Queue\Exchange

    /**
     * 容器中的Binding\Queue\Exchange都会自动创建（前提是RabbitMQ没有的情况）
     * 一旦创建好了队列，即使属性发生变化也不会覆盖
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", RabbitConstant.ORDER_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitConstant.ORDER_RELEASE_ORDER);//死信路由
        arguments.put("x-message-ttl", 60000);
        Queue queue = new Queue(RabbitConstant.ORDER_DELAY_QUEUE, true, false, false,arguments);
        return queue;

    }
    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue(RabbitConstant.ORDER_RELEASE_ORDER_QUEUE, true, false, false);
        return queue;
    }

    /**
     * 一个微服务只使用一个交换机，用Topic交换机
     * @return
     * String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
     */
    @Bean
    public Exchange orderEventExchange() {
        TopicExchange topicExchange = new TopicExchange(RabbitConstant.ORDER_EVENT_EXCHANGE, true, false);
        return topicExchange;
    }

    /**
     * 绑定 order 交换机 和死信队列
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding(){
        return new Binding(RabbitConstant.ORDER_DELAY_QUEUE, Binding.DestinationType.QUEUE,RabbitConstant.ORDER_EVENT_EXCHANGE
            ,RabbitConstant.ORDER_CREATE_ORDER,null);
    }
    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding(RabbitConstant.ORDER_RELEASE_ORDER_QUEUE, Binding.DestinationType.QUEUE,RabbitConstant.ORDER_EVENT_EXCHANGE
                ,RabbitConstant.ORDER_RELEASE_ORDER,null);
    }

}
