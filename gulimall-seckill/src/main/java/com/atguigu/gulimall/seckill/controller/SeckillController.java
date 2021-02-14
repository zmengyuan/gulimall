package com.atguigu.gulimall.seckill.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SeckillController {
    @Autowired
    SeckillService seckillService;
    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    @GetMapping(value = "/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {
        //获取到当前可以参加秒杀商品的信息
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }

    /*
    获取skuId的秒杀信息
     */
    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSecKillInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRedisTo to = seckillService.getSkuSecKillInfo(skuId);
        return R.ok().setData(to);
    }

    /*
    TODO 上架秒杀商品的时候，每一个数据都有过期时间
    秒杀后续的流程，简化了收获地址等信息
    秒杀结束之后，应该给之前扣减库存的加上还剩下的。
     */
    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num, Model model){
        // 1、判断是否登录(登录拦截器已经自动处理)

        String orderSn = seckillService.kill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }

}
