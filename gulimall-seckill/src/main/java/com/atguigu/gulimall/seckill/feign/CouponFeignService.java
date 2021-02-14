package com.atguigu.gulimall.seckill.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    //获取三天的秒杀
    @GetMapping("/coupon/seckillsession/lasts3DaySession")
    R getLasts3DaySession();
}