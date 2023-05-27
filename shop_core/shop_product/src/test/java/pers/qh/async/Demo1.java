package pers.qh.async;

import cn.hutool.core.lang.Console;
import pers.qh.exception.SleepUtils;

import java.util.concurrent.CompletableFuture;

public class Demo1 {
    /**
     * 只要多了一个Async就代表异步，异步就是启动另外线程去执行
     * 只要有XXpply就代表有返回值
     */
    public static void main(String[] args) throws Exception {
        System.out.println(Thread.currentThread().getName() + "======> mian开始");
        t1();
        t2();
        System.out.println(Thread.currentThread().getName() + "======> mian结束");
    }

    //runAsync - 无返回值
    public static void t1() {
        CompletableFuture.runAsync(() -> {
            Console.log(Thread.currentThread().getName() + "======> t1-runAsync 执行");
            SleepUtils.sleep(2);
        });
    }

    //supplyAsync - 有返回值
    private static void t2() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            Console.log(Thread.currentThread().getName() + "======> t2-supplyAsync 执行");
            SleepUtils.sleep(4);
            return "我是 t2-supplyAsync";
        });
        Console.log(Thread.currentThread().getName() + "======> " + future.get());
    }
}
