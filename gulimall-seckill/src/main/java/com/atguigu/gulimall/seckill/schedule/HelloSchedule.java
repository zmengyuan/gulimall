package com.atguigu.gulimall.seckill.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class HelloSchedule {
    /**
     * 1、Spring中6位组成，不允许7位d的年
     * 2、周几的位置，1-7代表周一到周日
     * 3、定时任务不应该阻塞。默认是阻塞的
     *      1）、可以让业务运行以异步的方式，自己提交到线程池
     *          CompletableFuture.runAsync(() -> {
     *              xxxService.hello();
     *          },executor);
     *      2）、支持定时任务线程池；TaskSchedulingAutoConfiguration,线程池默认只有1个，设置TaskSchedulingProperties;
     *              spring.task.scheduling.pool.size=5
     *      3)、让定时任务异步执行
     *          异步任务 自动配置TaskExecutionAutoConfiguration，默认大小是8
     *
     *      解决：使用异步任务来完成定时任务不阻塞的功能
     */
    @Async
    @Scheduled(cron = "*/5 * * * * ?")
    public void hello() throws InterruptedException {
        log.info("hello......");
        Thread.sleep(3000);
    }
}
