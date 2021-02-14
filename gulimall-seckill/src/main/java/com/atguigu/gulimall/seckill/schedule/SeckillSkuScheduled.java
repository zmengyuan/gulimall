package com.atguigu.gulimall.seckill.schedule;

import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 秒杀商品的定时上架
 *      每天晚上3点：上架最近三天需要秒杀的商品
 *      当天00：00：00-23：59：59
 *      明天
 *      后天
 */
@Component
@Slf4j
public class SeckillSkuScheduled {
    @Autowired
    SeckillService seckillService;
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days() {

        //重复上架无需处理
        log.info("上架秒杀的信息......");
        seckillService.uploadSeckillSkuLatest3Days();
    }
}
