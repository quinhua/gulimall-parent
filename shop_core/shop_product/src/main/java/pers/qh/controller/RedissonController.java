package pers.qh.controller;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.cloud.commons.util.IdUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.qh.exception.SleepUtils;
import pers.qh.result.ResultVo;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class RedissonController {
    private final RedissonClient redissonClient;

    /**源码重点信息解释
     *  a.锁的默认过期时间是30s  internalLockLeaseTime=lockWatchdogTimeout = 30 * 1000;
     *  b.每隔10s自动续期--看门狗机制  internalLockLeaseTime/3=10
     * @return
     */
    @GetMapping("/lock")
    public String lock(){
        RLock mylock = redissonClient.getLock("mylock");
        String uuid = UUID.randomUUID().toString();
        try {
            mylock.lock();
            System.out.println(Thread.currentThread().getName()+"执行业务"+uuid);
        } finally {
            mylock.unlock();
        }
        return Thread.currentThread().getName()+"执行业务"+uuid;
    }

    //信号量---semaphore
    @GetMapping("/left")
    public String left(){
        RSemaphore semaphore = redissonClient.getSemaphore("park_flag");
        //信号量减一
        semaphore.release(1);
        return Thread.currentThread().getName()+"离开车位";
    }

    @GetMapping("/park")
    public String park() throws Exception {
        RSemaphore semaphore = redissonClient.getSemaphore("park_flag");
        //信号量加一
        semaphore.acquire(1);
        return Thread.currentThread().getName()+"抢到车位";
    }

    String uuid="";
    //读写锁---写锁
    @GetMapping("/write")
    public String write() throws Exception {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        RLock writeLock = rwLock.writeLock();
        writeLock.lock();
        uuid= IdUtil.randomUUID();
        writeLock.unlock();
        return uuid;
    }

    //读写锁---读锁
    @GetMapping("/read")
    public String read() {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("rwLock");
        RLock readLock = rwLock.readLock();
        try {
            readLock.unlock();
            return uuid;
        } finally {
            readLock.unlock();
        }
    }

    //闭锁---CountDownLatch
    @GetMapping("/goCar")
    public String goCar() {
        RCountDownLatch count = redissonClient.getCountDownLatch("go_car");
        //有人上车，剩余座位减一
        count.countDown();
        return Thread.currentThread().getName()+"上车";
    }

    @GetMapping("/outCar")
    public String outCar() throws Exception {
        RCountDownLatch count = redissonClient.getCountDownLatch("go_car");
        //设定车上共有5个座位
        count.trySetCount(5);
        //当座位坐满就出发
        count.await();
        return "座位已满，请系好安全带，准备出发";
    }

    //公平锁
    @GetMapping("fairLock/{id}")
    public String fairLock(@PathVariable Long id){
        RLock fairLock = redissonClient.getFairLock("fair_lock");
        fairLock.lock();
        SleepUtils.sleep(8);
        System.out.println("公平锁==="+id);
        fairLock.unlock();
        return "公平锁 success==="+id;
    }

    @GetMapping("unFairLock/{id}")
    public String unFairLock(@PathVariable Long id){
        RLock unFairLock = redissonClient.getLock("unfair_lock");
        unFairLock.lock();
        SleepUtils.sleep(8);
        System.out.println("非公平锁==="+id);
        unFairLock.unlock();
        return "非公平锁 success==="+id;
    }

}
