package com.atguigu.gulimall.search.thread;

import java.util.concurrent.*;

/*
1、2不能得到返回值，3可以获取返回值
1、2、3都不能控制资源
4可以控制资源，性能稳定。
 */
public class ThreadTest {
    public static ExecutorService service = Executors.newFixedThreadPool(10);

    /*
    创建和启动异步任务
    public static CompletableFuture<Void> runAsync(Runnable runnable)//默认线程池
    public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)//指定线程池
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)//有返回值
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("方法开始");
        /*CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("当前运行结果:" + i);
        }, service);*/

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("当前运行结果:" + i);
            return i;
        }, service);
        Integer i = future.get();
        System.out.println(i);
        System.out.println("方法结束");
    }


    public static void main1(String[] args) throws ExecutionException, InterruptedException {
//        Thread01 thread01 = new Thread01();
//        thread01.start();//启动线程

//        2、实现Runnable
//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();

//        3、实现Callable接口+FutureTask
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
        new Thread(futureTask).start();
        //阻塞等待整个线程执行完成，获取返回结果
        Integer integer = futureTask.get();

        /*
        FutureTask impl RunnableFuture extends extends Runnable, Future
        Future 可以获得返回值
         */
//       4、线程池
        service.execute(new Runnable01());//无返回值
        service.submit(new Callable01());//可以有返回值
        System.out.println("main.............end......."+integer);


    }
    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("当前运行结果:" + i);
        }
    }
    public static class Runnable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("当前运行结果:" + i);
        }
    }
    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("当前运行结果:" + i);
            return i;
        }
    }
}
