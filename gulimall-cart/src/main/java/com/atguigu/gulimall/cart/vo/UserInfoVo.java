package com.atguigu.gulimall.cart.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfoVo {
    private Long userId;
    private String userKey; //一定封装
    private boolean tempUser = false;  //判断是否有临时用户
}
