package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class GuliFeignConfig {
    /*
    这个就是feign远程之前会执行这些拦截器
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //1、RequestContextHolder拿到刚进来的请求
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();//老请求
                //同步请求头数据。Cookie
                String cookie = request.getHeader("Cookie");
                //给新请求同步了老请求的cookie
                requestTemplate.header("Cookie",cookie);
                System.out.println("feign远程之前先执行RequestInterceptor.apply()");
            }
        };
    }
}
