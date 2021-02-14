package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.atguigu.common.constant.RabbitConstant;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionWithSkus;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ProductFeignService productFeignService;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1、扫描最近三天需要参与秒杀的活动
        R session = couponFeignService.getLasts3DaySession();
        if (session.getCode() == 0){
            // 上架活动 ，每场活动很多商品
            List<SeckillSessionWithSkus> data = session.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
            });
            // 缓存到redis
            // 1、缓存活动信息
            saveSessionInfos(data);
            // 2、缓存获得关联商品信息
            saveSessionSkuInfos(data);
        }
    }

    private void saveSessionInfos(List<SeckillSessionWithSkus> sessions) {
        if (!CollectionUtils.isEmpty(sessions)){
            sessions.stream().forEach(session -> {
                Long startTime = session.getStartTime().getTime();
                Long endTime = session.getEndTime().getTime();
                String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;//TODO 但是如果多场活动开始时间和结束时间一样呢？  key-活动 value-所有skuIds
                Boolean hasKey = redisTemplate.hasKey(key);
                if (!hasKey){
                    List<String> collect = session.getRelationSkus().stream()
                            .map(item -> item.getPromotionSessionId()+"_"+item.getSkuId().toString())
                            .collect(Collectors.toList());
                    // 缓存活动信息
                    redisTemplate.opsForList().leftPushAll(key, collect);
                }

            });
        }
    }

    private void saveSessionSkuInfos(List<SeckillSessionWithSkus> sessions) {
        //准备hash操作
        BoundHashOperations<String,Object,Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        sessions.stream().forEach(session -> {
            // 4、随机码
            String token = UUID.randomUUID().toString().replace("_", "");
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString())){
                    /*
                    缓存商品
                    1、缓存sku基本数据
                    2、缓存sku秒杀信息
                    3、随机码
                     */
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    // 1、sku的基本信息
                    R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfoVo(info);
                    }
                    //秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);
                    // 3、设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());
                    redisTo.setRandomCode(token);

                    String s = JSON.toJSONString(redisTo);
                    ops.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), s);

                    // 如果当前这个场次的商品的库存信息已经上架就不需要上架
                    // 5、使用库存作为分布式信号量 限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            });
        });
    }


    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        long currentTime = System.currentTimeMillis();
        for (String key : keys) {
            String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] split = replace.split("_");
            long startTime = Long.parseLong(split[0]);
            long endTime = Long.parseLong(split[1]);
            // 当前秒杀活动处于有效期内
            if (currentTime > startTime && currentTime < endTime) {
                // 获取这个秒杀场次的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                assert range != null;
                List<String> strings = hashOps.multiGet(range);
                if (!CollectionUtils.isEmpty(strings)) {
                    return strings.stream().map(item -> JSON.parseObject(item, SeckillSkuRedisTo.class))
                            .collect(Collectors.toList());
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSecKillInfo(Long skuId) {
        // 1、找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (null != keys && keys.size()>0){
            //6_4
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)){//匹配正则 包含商品
                    String json = hashOps.get(key);
                    //获取到redis存储的信息
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);

                    //如果是合适的展示时间才展示  随机码
                    long current = new Date().getTime();
                    if (current >= skuRedisTo.getStartTime() && current <= skuRedisTo.getEndTime()){

                    }else {
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }

            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        long s1 = System.currentTimeMillis();
        // 从拦截器获取用户信息
        MemberRespVo repsVo = LoginUserInterceptor.loginUser.get();
        // 1、获取当前商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        SeckillSkuRedisTo redis = JSON.parseObject(json, SeckillSkuRedisTo.class);
        // 校验合法性
        Long startTime = redis.getStartTime();
        Long endTime = redis.getEndTime();
        long current = new Date().getTime();
        long ttl = endTime - startTime; //场次存活时间，存redis
        if (current >= startTime && current <= endTime){
            //TODO 秒杀商品存redis的时候应该把过期时间加上
            // 1.2校验随机码和商品id
            String randomCode = redis.getRandomCode();
            String skuId = redis.getPromotionSessionId() + "_" + redis.getSkuId();
            if (!randomCode.equals(key) || !skuId.equals(killId)){
                return null;
            }
            // 1.3、验证购物的数量是否合理
            if (num > redis.getSeckillLimit()){
                return null;
            }
            // 1.4、验证这个人是否购买过。幂等性处理。如果只要秒杀成功，就去占位  userId_sessionId_skillId
            // SETNX
            String redisKey = repsVo.getId() + "_" + skuId;
            // 1.4.1 自动过期--通过在redis中使用 用户id-skuId 来占位看是否买过
            Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
            if (ifAbsent){
                // 1.4.2 占位成功，说明该用户未秒杀过该商品，则继续尝试获取库存信号量
                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                boolean  b = semaphore.tryAcquire(num);//使用try 不要阻塞
                if (b){
                    // 秒杀成功
                    // 快速下单发送MQ消息 10ms
                    String timeId = IdWorker.getTimeId();
                    SeckillOrderTo orderTo = new SeckillOrderTo();
                    orderTo.setOrderSn(timeId);
                    orderTo.setMemberId(repsVo.getId());
                    orderTo.setNum(num);
                    orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                    orderTo.setSkuId(redis.getSkuId());
                    orderTo.setSeckillPrice(redis.getSeckillPrice());
                    rabbitTemplate.convertAndSend(RabbitConstant.ORDER_EVENT_EXCHANGE, RabbitConstant.ORDER_SECKILL_ORDER, orderTo);
                    long s2 = System.currentTimeMillis();
                    log.info("耗时..." + (s2-s1));
                    return timeId;
                }
                return null;
            }else {
                // 说明已经买过
                return null;
            }
        }else {
            return null;
        }
    }
}
