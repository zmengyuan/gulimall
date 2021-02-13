package com.atguigu.gulimall.ware.listener;

import com.atguigu.common.constant.RabbitConstant;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.gulimall.ware.service.WareSkuService;
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
@RabbitListener(queues = {RabbitConstant.STOCK_RELEASE_STOCK_QUEUE})
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 解锁失败，一定要告诉服务器这个消息不能删除！！！
     * @param stockLockedTo
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        log.info("************************收到库存解锁的消息********************************");
        try {
            wareSkuService.unlock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);//消息拒绝之后重新放到队列里面
        }
    }
    //这个是订单关闭发过来的
    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo to, Message message, Channel channel) throws IOException {
        log.info("************************订单关闭准备解锁库存********************************");
        try {
            wareSkuService.unLockStockForOrder(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}