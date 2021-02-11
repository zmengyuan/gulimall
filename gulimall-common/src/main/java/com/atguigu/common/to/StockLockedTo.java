package com.atguigu.common.to;

import lombok.Data;

import java.util.List;

/**
 * 库存锁定成功向MQ发送的信息
 */
@Data
public class StockLockedTo {
    private Long id;//库存工作单id
    private StockDetailTo detailTo;//工作单详情id
}
