package com.atguigu.common.exception;

public class NoStockException extends RuntimeException {
    private Long skuId;
    public NoStockException(String msg){
        super(msg);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}

