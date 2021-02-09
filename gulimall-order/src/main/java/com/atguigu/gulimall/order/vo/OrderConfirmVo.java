package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//订单确认页需要用的数据
public class OrderConfirmVo {

    //收获地址，ums_member_receive_address表
    @Getter
    @Setter
    List<MemberAddressVo> address;

    //所有选中的购物项
    @Getter
    @Setter
    List<OrderItemVo> items;

    //发票。。。

    //优惠券信息。。。
    //积分
    @Getter
    @Setter
    Integer integration;

    Integer count;

    //总件数
    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }

    //订单总额
    BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                total = total.add(multiply);
            }
        }

        return total;
    }

    //应付价格
    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    @Getter
    @Setter
    Map<Long,Boolean> stocks;
    @Getter@Setter
    private String orderToken;
}
