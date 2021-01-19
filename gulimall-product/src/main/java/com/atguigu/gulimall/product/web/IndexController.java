package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    @Autowired
    StringRedisTemplate stringRedisTemplate;

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

    @GetMapping("/write")
    @ResponseBody
    public String writeValue(){
        RReadWriteLock writeLock=redisson.getReadWriteLock("rw-loc");
        String uuid = null;
        RLock lock = writeLock.writeLock();
        lock.lock();
        try {
            uuid = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeValue",uuid);
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return uuid;
    }

    @GetMapping("/read")
    @ResponseBody
    public String redValue(){
        String uuid = null;
        RReadWriteLock readLock=redisson.getReadWriteLock("rw-loc");
        RLock lock = readLock.readLock();
        lock.lock();
        try {
            uuid = stringRedisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return uuid;
    }

    /**
     * 放假，锁门
     * 1班没人了
     * 5个班全部走完，我们可以锁大门
     */
    @RequestMapping(value = "/lockDoor")
    @ResponseBody
    public String lockDoor() {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5L);
        try {
            door.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "放假了";
    }

    @RequestMapping(value = "/gogogo/{id}")
    @ResponseBody
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();
        return id+"班的人走了";
    }

    /**
     * 车库停车
     * 3车位
     *  信号量可以用tryAcquire做限流
     */
    @RequestMapping(value = "/park")
    @ResponseBody
    public String park() {
        RSemaphore park = redisson.getSemaphore("park");
        park.trySetPermits(3);
        boolean  b = park.tryAcquire();
        if (b) {
            //执行业务
        }else {
            return "error";
        }
//        try {
//            park.acquire();//获取一个信号，获取一个值
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return "ok";
    }

    @RequestMapping(value = "/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个车位

        return "ok go";
    }

}
