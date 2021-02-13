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

    //库存交换机
    public static final String STOCK_EVENT_EXCHANGE = "stock-event-exchange";
    //库存解锁队列
    public static final String STOCK_RELEASE_STOCK_QUEUE = "stock.release.stock.queue";
    //死信路由
    public static final String STOCK_RELEASE_STOCK = "stock.release.stock";
    //库存延迟队列
    public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
    //库存锁定路由
    public static final String STOCK_LOCKED="stock.locked";

}
