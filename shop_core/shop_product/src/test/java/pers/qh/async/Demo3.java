package pers.qh.async;

import cn.hutool.core.lang.Console;
import pers.qh.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Demo3 {
    public static void main(String[] args) throws Exception {
        System.out.println(Thread.currentThread().getName() + "======> mian开始");
        //t5();
        //t6();
        t7();
        SleepUtils.sleep(5);
        System.out.println(Thread.currentThread().getName() + "======> mian结束");
    }

    private static void t5() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            Console.log(Thread.currentThread().getName() + "======> t5-supplyAsync 执行");
            SleepUtils.sleep(4);
            return "t5 - supplyAsync";
        });
        future.thenAccept(s -> {
            SleepUtils.sleep(1);
            Console.log(Thread.currentThread().getName()+"_1_接收到的结果为======>",s);
        });
        future.thenAccept(s -> {
            SleepUtils.sleep(3);
            Console.log(Thread.currentThread().getName()+"_2_接收到的结果为======>",s);
        });
    }

    //thenApply - 同步
    private static void t6() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            Console.log(Thread.currentThread().getName() + "======> t5-supplyAsync 执行");
            SleepUtils.sleep(4);
            return "t5 - supplyAsync";
        });
        CompletableFuture<String> future1 = future.thenApply(s -> {
            SleepUtils.sleep(1);
            return Thread.currentThread().getName() + "_1_接收到的结果为======>" + s;
        });
        CompletableFuture<String> future2 = future.thenApply(s -> {
            SleepUtils.sleep(3);
            return Thread.currentThread().getName() + "_2_接收到的结果为======>" + s;
        });
        Console.log(future1.get());
        Console.log(future2.get());
    }

    //thenApplyAsync - 异步
    private static void t7() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            Console.log(Thread.currentThread().getName() + "======> t5-supplyAsync 执行");
            SleepUtils.sleep(4);
            return "t5 - supplyAsync";
        });
        CompletableFuture<String> future1 = future.thenApplyAsync(s -> {
            SleepUtils.sleep(1);
            return Thread.currentThread().getName() + "_1_接收到的结果为======>" + s;
        });
        CompletableFuture<String> future2 = future.thenApplyAsync(s -> {
            SleepUtils.sleep(3);
            return Thread.currentThread().getName() + "_2_接收到的结果为======>" + s;
        });
        Console.log(future1.get());
        Console.log(future2.get());
    }

}
