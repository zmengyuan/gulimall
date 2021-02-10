package com.atguigu.gulimall.order.enume;

public enum OrderStatusEnum {
    CREATE_NEW(0),
    CANCLED(4),
    ;

    Integer code;

    OrderStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
