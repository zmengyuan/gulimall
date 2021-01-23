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
    一、创建和启动异步任务
    public static CompletableFuture<Void> runAsync(Runnable runnable)//默认线程池
    public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)//指定线程池
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)//有返回值
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)

    二、当前一个任务执行完成，可以继续后面写代码。或者使用CompletableFuture提供的以下方法
    //可以处理异常，无返回值
    public CompletableFuture<T> whenComplete(BiConsumer<? super T,? super Throwable> action)//使用前一个相同线程执行
    public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T,? super Throwable> action)//前一个成功以后，这个任务还是以异步的方式执行，交给线程池执行
    public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T,? super Throwable> action, Executor executor)
    //可以处理异常，有返回值
    public CompletableFuture<T> exceptionally(Function<Throwable,? extends T> fn)

    三、可以直接感知异常并处理
    public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
    public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
    public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn,Execut

    四、串行化
    先执行A再执行B
    public CompletableFuture<Void> thenRun(Runnable var1)
    public CompletableFuture<Void> thenRunAsync(Runnable var1)
    public CompletableFuture<Void> thenRunAsync(Runnable var1, Executor var2)
    先执行A再执行B，但是需要A的结果
    public CompletableFuture<Void> thenAccept(Consumer<? super T> var1)
    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> var1)
    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> var1, Executor var2)
    先执行A再执行B，需要A的结果并且本身也要返回值
    public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> var1)
    public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> var1)
    public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> var1, Executor var2)

     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("方法开始");
        /*CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("当前运行结果:" + i);
        }, service);*/

        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("当前运行结果:" + i);
            return i;
        }, service);
        Integer i = future.get();
        System.out.println(i);*/

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("当前运行结果:" + i);
//            return i;
//        }, service).whenComplete((t,v) -> {
//            //t 是结果  v是异常  虽然能知道异常信息，但是没法修改异常数据
//            System.out.println("打印异步任务成功后的t:{"+t+"}，v是：{"+v+"}");
//        }).exceptionally(throww -> {
//            //可以感知异常，并且处理异常返回
//            return 10;
//        });

        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 0;
            System.out.println("当前运行结果:" + i);
            return i;
        }, service).handle((result,ex) -> {
            if (result != null){
                return result*2;
            }
            if (ex != null){
                return 0;
            }
            return 0;
        });*/
        /*
        线程串行化
        1）thenRunAsync 不能获取上一步的结果
         */
        /*CompletableFuture<Void> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 5;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("当前运行结果:" + i);
            return i;
        }, service).thenRunAsync(() -> {
            System.out.println("任务2启动了");
        },service);*/

        /*CompletableFuture<Void> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 5;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("当前运行结果:" + i);
            return i;
        }, service).thenAcceptAsync((t) -> {
            System.out.println("上一步的结果："+t);
        },service);*/

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 5;
            System.out.println("当前运行结果:" + i);
            return i;
        }, service).thenApplyAsync(res -> {
            System.out.println("任务2启动了。。。。" + res);
            return "hello" + res;
        }, service);

        System.out.println("方法结束"+future.get());
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
