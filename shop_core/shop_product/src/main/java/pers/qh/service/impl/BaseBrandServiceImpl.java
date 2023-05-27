package pers.qh.service.impl;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import pers.qh.config.RedissonConfig;
import pers.qh.entity.BaseBrand;
import pers.qh.exception.SleepUtils;
import pers.qh.mapper.BaseBrandDao;
import pers.qh.service.BaseBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-16
 */
@Service
@RequiredArgsConstructor
public class BaseBrandServiceImpl extends ServiceImpl<BaseBrandDao, BaseBrand> implements BaseBrandService {
    private final RedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    private void doBusiness() {
        String num =(String) redisTemplate.opsForValue().get("num");
        if(StringUtils.isEmpty(num)){
            redisTemplate.opsForValue().set("num","1");
        }else{
            int value = Integer.parseInt(num);
            redisTemplate.opsForValue().set("num",String.valueOf(++value));
        }
    }

    //最初
    public void setNum01() {
        doBusiness();
    }

    //加synchronized
    public synchronized void setNum02() {
        doBusiness();
    }

    //分布式锁案例一
    public synchronized void setNum03() {
        //利用reids中的setnx命令
        Boolean redisLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok");
        if(redisLock){
            //拿到锁执行业务,如果 doBusiness 出现异常或错误 导致锁无法删除无法释放
            doBusiness();
            //做完业务之后删除锁
            redisTemplate.delete("lock");
        }else{
            //如果没有拿到，递归
            setNum();
        }
    }

    //分布式锁案例二
    public synchronized void setNum04() {
        //利用reids中的setnx命令
        Boolean redisLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok", 3, TimeUnit.SECONDS);
        if(redisLock){
            //拿到锁执行业务,如果 doBusiness 出现异常或错误 导致锁无法删除无法释放
            doBusiness();
            //做完业务之后删除锁
            redisTemplate.delete("lock");
        }else{
            //如果没有拿到，递归
            setNum();
        }
    }

    //分布式锁案例三
    public synchronized void setNum05() {
        //放一个标记
        String token = UUID.randomUUID().toString();
        //利用reids中的setnx命令
        Boolean redisLock = redisTemplate.opsForValue().setIfAbsent("lock",token, 3, TimeUnit.SECONDS);
        if(redisLock){
            //拿到锁执行业务,如果 doBusiness 出现异常或错误 导致锁无法删除无法释放
            doBusiness();
            String redisToken = (String) redisTemplate.opsForValue().get("lock");
            if(token.equals(redisToken)){
                //做完业务之后删除锁
                redisTemplate.delete("lock");
            }
        }else{
            //如果没有拿到，递归
            setNum();
        }
    }

    //分布式锁案例四
    public void setNum06() {
        //放一个标记
        String token = UUID.randomUUID().toString();
        //利用redis的setnx命令
        boolean accquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.SECONDS);
        if(accquireLock){
            System.out.println(1);
            //拿到了锁 就可以执行业务
            doBusiness();
            //这句话相当于具备了原子性
            String luaScript="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //把脚本放到script对象当中
            redisScript.setScriptText(luaScript);
            //设置脚本返回类型
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"),token);
        }else {
            //如果没有拿到 递归
            setNum();
        }
    }

    //分布式锁案例五
    public void setNum07() {
        //还有很多业务逻辑需要处理 查询其他业务 调用其他微服务 1000行代码
        //放一个标记
        String token = UUID.randomUUID().toString();
        //利用redis的setnx命令
        boolean accquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.SECONDS);
        if(accquireLock){
            //拿到了锁 就可以执行业务
            doBusiness();
            //这句话相当于具备了原子性
            String luaScript="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //把脚本放到script对象当中
            redisScript.setScriptText(luaScript);
            //设置脚本返回类型
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"),token);
        }else {
            //自旋 目的是为了去拿锁
            while(true){
                SleepUtils.sleep(50);
                boolean retryAccquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.SECONDS);
                if(retryAccquireLock){
                    break;
                }
            }
            setNum();
        }
    }

    //分布式锁案例六-不具备可重入性
    public void setNum08() {
        String token = UUID.randomUUID().toString();
        boolean accquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.HOURS);
        if(accquireLock){
            doBusiness();
            String luaScript="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"),token);
        }else {
            //自旋 目的是为了去拿锁
            while(true){
                SleepUtils.sleep(50);
                boolean retryAccquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.HOURS);
                if(retryAccquireLock){
                    break;
                }
            }
            setNum();
        }
    }

    //分布式锁案例七-具备可重入性-锁没有被删除
    Map<Thread,Boolean> threadMap=new HashMap();
    public void setNum09() {
        Boolean flag = threadMap.get(Thread.currentThread());
        String token =null;
        boolean accquireLock=false;
        if(flag!=null&&flag){
            //已经拿到过锁了
            accquireLock=true;
        }else{
            token = UUID.randomUUID().toString();
            accquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.HOURS);
        }

        if(accquireLock){
            doBusiness();
            String luaScript="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"),token);
        }else {
            //自旋 目的是为了去拿锁
            while(true){
                SleepUtils.sleep(50);
                boolean retryAccquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,3, TimeUnit.HOURS);
                if(retryAccquireLock){
                    threadMap.put(Thread.currentThread(),true);
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * 分布式锁案例八
     *  你们先上都遇到过那些问题 你是如何解决的
     *      长此以往会出现内存溢出的问题
     *      a.通过线上日志 抓取当前应用的内存模型
     *      b.通过jvisualvm连上程序 发现有个对象内存空间在不断的上涨
     * 具备了自动续期的功能
     *
     */
    //Map<Thread,String> threadMap1=new HashMap();
    ThreadLocal<String> threadLocal=new ThreadLocal<>();
    public void setNum10() {
        String token = threadLocal.get();
        boolean accquireLock=false;
        if(!StringUtils.isEmpty(token)){
            //已经拿到过锁了
            accquireLock=true;
        }else{
            token = UUID.randomUUID().toString();
            accquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,5, TimeUnit.SECONDS);
        }
        if(accquireLock){
            Thread thread = new Thread(() -> {
                //每隔3s 续期10秒
                while (true){
                    SleepUtils.sleep(3);
                    redisTemplate.expire("lock",10,TimeUnit.SECONDS);
                    System.out.println("续期成功");
                }
            });
            thread.setDaemon(true);
            thread.start();
            SleepUtils.sleep(15);
            doBusiness();
            String luaScript="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList("lock"),token);
            //擦屁股
            threadLocal.remove();
        }else {
            //自旋 目的是为了去拿锁
            while(true){
                SleepUtils.sleep(50);
                boolean retryAccquireLock=redisTemplate.opsForValue().setIfAbsent("lock",token,5, TimeUnit.SECONDS);
                if(retryAccquireLock){
                    threadLocal.set(token);
                    break;
                }
            }
            setNum();
        }
    }

    @Override
    public void setNum(){
        RLock redissonLock = redissonClient.getLock("lock-key");
        redissonLock.lock();
        doBusiness();
        redissonLock.unlock();
    }

}
