package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.vo.UserInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CartController {
    /**
     * 浏览器有一个cookie user-key 是一个月过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份
     * 浏览器以后保存，每次访问都会带上这个cookie
     *
     * 登录：session
     * 没登录： 使用user-key
     * 所以想到用拦截器来做这个功能
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(){

        //快速得到用户信息，id,user-key
        UserInfoVo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        return "cartList";
    }
}
