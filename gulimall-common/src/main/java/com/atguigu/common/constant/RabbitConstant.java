package com.atguigu.common.constant;

public class RabbitConstant {
    //订单交换机
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";

    //订单关闭队列
    public static final String ORDER_RELEASE_ORDER_QUEUE="order.release.order.queue";

    //订单延迟队列
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    //订单创建成功路由
    public static final String ORDER_CREATE_ORDER="order.create.order";

    //订单死信路由
    public static final String ORDER_RELEASE_ORDER = "order.release.order";

}
