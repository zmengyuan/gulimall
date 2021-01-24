package com.atguigu.gulimall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@Controller
public class LoginController {
    /*
    发送一个请求直接跳转到页面
    SpringMVC viewController 将请求和页面映射过来
     */

    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }
}
