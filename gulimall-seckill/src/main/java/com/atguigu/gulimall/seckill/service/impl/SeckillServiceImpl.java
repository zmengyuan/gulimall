package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1、扫描最近三天需要参与秒杀的活动
        R session = couponFeignService.getLasts3DaySession();
        if (session.getCode() == 0){
            // 上架商品
            List<SeckillSessionWithSkus> data = session.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
            });
            // 缓存到redis
            // 1、缓存活动信息
            saveSessionInfos(data);
            // 2、缓存获得关联商品信息
            saveSessionSkuInfos(data);
        }
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkus> sessions) {
        if (!CollectionUtils.isEmpty(sessions)){
            sessions.stream().forEach(session -> {
                Long startTime = session.getStartTime().getTime();
                Long endTime = session.getEndTime().getTime();
                String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;//TODO 但是如果多场活动开始时间和结束时间一样呢？
                Boolean hasKey = redisTemplate.hasKey(key);
                if (!hasKey){
                    List<String> collect = session.getRelationSkus()
                            .stream()
                            .map(item -> item.getPromotionId().toString() +"_"+ item.getSkuId().toString())
                            .collect(Collectors.toList());
                    // 缓存活动信息
                    redisTemplate.opsForList().leftPushAll(key, collect);
                }

            });
        }
    }

    private void saveSessionInfos(List<SeckillSessionWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            //准备hash操作
            BoundHashOperations<String,Object,Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                /*
                缓存商品
                1、缓存sku基本数据
                2、缓存sku秒杀信息
                3、随机码
                 */
                SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();

                //秒杀信息
                BeanUtils.copyProperties(seckillSkuVo,seckillSkuRedisTo);

                String s = JSON.toJSONString(seckillSkuRedisTo);
                ops.put(seckillSkuVo.getId(),s);
            });
        });
    }
}
