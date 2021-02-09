package com.atguigu.gulimall.order.vo;

import java.math.BigDecimal;
import java.util.List;

//订单确认页需要用的数据
public class OrderConfirmVo {

    //收获地址，ums_member_receive_address表
    List<MemberAddressVo> address;

    //所有选中的购物项
    List<OrderItemVo> items;

    //发票。。。

    //优惠券信息。。。
    //积分
    Integer integration;

    //订单总额
    BigDecimal total;

    //应付价格
    BigDecimal payPrice;

}
