package com.atguigu.gulimall.order.listener;

import com.atguigu.common.constant.RabbitConstant;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
@RabbitListener(queues = RabbitConstant.ORDER_RELEASE_ORDER_QUEUE)
public class OrderCloseListener {
    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        log.info("收到过期的订单信息，准备关闭订单,{}",orderEntity.toString());
        try {
            orderService.closeOrder(orderEntity);
            //TODO 手动调用支付宝收单功能
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 修改失败 拒绝消息 使消息重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
