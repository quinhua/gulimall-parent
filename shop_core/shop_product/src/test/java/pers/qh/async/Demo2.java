package pers.qh.async;

import cn.hutool.core.lang.Console;
import pers.qh.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;

public class Demo2 {
    public static void main(String[] args) throws Exception {
        System.out.println(Thread.currentThread().getName() + "======> mian开始");
        t3();
        t4();
        System.out.println(Thread.currentThread().getName() + "======> mian结束");
    }

    //runAsync - whenComplete - exceptionally
    private static void t3() {
        CompletableFuture.runAsync(() -> {
                    //int num = 1 / 0;
                    Console.log(Thread.currentThread().getName() + "======> t3-runAsync 执行");
                })
                .whenComplete((unused, throwable) -> {
                    Console.log("whenComplete正常接收值===>", unused);
                    Console.log("whenComplete异常处理===>", throwable);
                }).exceptionally(throwable -> {
                    Console.log("exceptionally异常处理===>", throwable);
                    return null;
                });
    }

    //supplyAsync - whenComplete - exceptionally
    private static void t4() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    //int num = 1 / 0;
                    System.out.println(Thread.currentThread().getName() + "======> t4-runAsync 执行");
                    return "我是 t4-supplyAsync";
                })
                .whenComplete((unused, throwable) -> {
                    Console.log("whenComplete正常接收值===>", unused);
                    Console.log("whenComplete异常处理===>", throwable);
                }).exceptionally(throwable -> {
                    Console.log("exceptionally异常处理===>", throwable);
                    return null;
                });
        System.out.println(Thread.currentThread().getName() + "======> " + future.get());
    }

}
