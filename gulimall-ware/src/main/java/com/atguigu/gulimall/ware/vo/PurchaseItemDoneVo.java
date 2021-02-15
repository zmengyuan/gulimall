package com.atguigu.gulimall.ware.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {
    //{itemId:1,status:4,reason:""}
    private Long itemId;//采购需求的id
    private Integer status;//3-成功 4-失败
    private String reason;
}
