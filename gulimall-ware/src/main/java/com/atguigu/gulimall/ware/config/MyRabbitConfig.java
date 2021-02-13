package com.atguigu.gulimall.ware.config;

import com.atguigu.common.constant.RabbitConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {
    //加上这个空方法是为了连上rabbitmq创建队列。
    //在测试库存解锁的时候把这个方法注释了，因为自己开启了一个listener监听了。
//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void handle(Message message){
//
//    }
    /**
     * 使用JSON序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange(RabbitConstant.STOCK_EVENT_EXCHANGE, true, false);
    }
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue(RabbitConstant.STOCK_RELEASE_STOCK_QUEUE, true, false, false);
    }
    @Bean
    public Queue stockDelayQueue() {
        // String name, boolean durable, boolean exclusive, boolean autoDelete,
        //			@Nullable Map<String, Object> arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", RabbitConstant.STOCK_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", RabbitConstant.STOCK_RELEASE_STOCK);
        arguments.put("x-message-ttl", 120000);
        return new Queue(RabbitConstant.STOCK_DELAY_QUEUE, true, false, false, arguments);
    }
    @Bean
    public Binding orderLockedBinding() {
        return new Binding(RabbitConstant.STOCK_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                RabbitConstant.STOCK_EVENT_EXCHANGE,
                RabbitConstant.STOCK_LOCKED,
                null);
    }
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding(RabbitConstant.STOCK_RELEASE_STOCK_QUEUE,
                Binding.DestinationType.QUEUE,
                RabbitConstant.STOCK_EVENT_EXCHANGE,
                "stock.release.#",
                null);
    }

}
