package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String getIndex(Model model) {
        //获取所有的一级分类
        List<CategoryEntity> categories = categoryService.getLevel1Catagories();
        model.addAttribute("categories", categories);
        // 视图解析器进行拼串
        // classpath:/templates/+返回值+   .html
        return "index";
    }

    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatelogJson(){
        Map<String, List<Catalog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }

    @Autowired
    RedissonClient redisson;

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1.获取一把锁，只要名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");
        //2.加锁和解锁
        try {
            /*
            如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
            如果我们未指定超时时间，就使用看门狗的超时时间30*1000.并且执行scheduleExpirationRenewal 定时方法 设置超时时间。只要占锁成功，就会启动一个定时任务【重新给锁设置看门狗过期时间】
            1/3的看门狗时间，重新续期
             */
            lock.lock(10, TimeUnit.SECONDS);
            log.info("加锁成功，执行业务方法..."+Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e){

        }finally {
            lock.unlock();
            System.out.println("释放锁..."+Thread.currentThread().getId());
        }
        return "hello";
    }
}
