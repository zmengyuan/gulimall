package com.atguigu.gulimall.ware.exception;

import lombok.NoArgsConstructor;

public class NoStockException extends RuntimeException {
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品skuId:"+skuId+"，没有足够的库存了");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}

