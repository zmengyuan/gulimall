package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues:声明需要监听的所有队列
     * 参数可以写以下内容
     *      * 1、Message message：原生消息详细信息。头+体
     *      * 2、T<发送的消息类型> OrderReturnApplyEntity content
     *      * 3、Channel channel 当前传输数据的通道
     *
     *      Queue:可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     *      * 场景：
     *      *       1）、订单服务启动多个；同一个消息，只能有一个客户端收到
     *      *       2)、只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     */
    @RabbitListener(queues = {"hello-java-queue"})
    public void receiverMessage(Message message, OrderReturnApplyEntity content,
                                Channel channel) {
        //消息体
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties properties = message.getMessageProperties();
        System.out.println("接收到消息...内容:" + content);
//        Thread.sleep(3000);
        System.out.println("消息处理完成=》"+content.getReturnName());
        System.out.println("接受到消息。。。。。内容："+message+"--类型："+message.getClass());//打印出来发现是org.springframework.amqp.core.Message类型
    }

}