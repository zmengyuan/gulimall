package com.atguigu.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GulimallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1、配置跨域
        corsConfiguration.addAllowedHeader("*");//允许哪些头
        corsConfiguration.addAllowedMethod("*");//允许哪些方法
        corsConfiguration.addAllowedOrigin("*");//允许哪个来源
        corsConfiguration.setAllowCredentials(true);  // 允许携带cookie


        source.registerCorsConfiguration("/**", corsConfiguration);
        // 只需要将跨域配置信息放入到该Filter就起作用了
        return new CorsWebFilter(source);
    }
}
