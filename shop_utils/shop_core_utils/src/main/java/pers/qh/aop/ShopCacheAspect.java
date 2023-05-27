package pers.qh.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

@Component
@Aspect
public class ShopCacheAspect {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RBloomFilter skuBloomFilter;
    @Autowired
    private RedisTemplate redisTemplate;

    //1.切面编程+redisson分布式锁+布隆过滤器
    @Around("@annotation(pers.qh.aop.ShopCache)")
    public Object chcheAroundAdvice(ProceedingJoinPoint joinPoint){
        //拿到目标方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        //获取目标方法上的参数
        Object[] methodParms = joinPoint.getArgs();
        //拿到目标方法上的注解
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);

        Object suffix = methodParms[0];
        String prefix = shopCache.value();
        String cacheKey = prefix + ":" + suffix;
        Object objectCache = redisTemplate.opsForValue().get(cacheKey);
        String lockKey = "lock-" + suffix;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            Optional.ofNullable(objectCache).orElseGet(() -> {
                boolean enableBloom = shopCache.enableBloom();
                Object objectDb = null;
                if(enableBloom){
                    //获取是否开启布隆过滤器的开关
                    boolean flag = skuBloomFilter.contains(suffix);
                    if (flag) {
                        try {
                            objectDb = joinPoint.proceed();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else{
                    try {
                        objectDb = joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                redisTemplate.opsForValue().set(cacheKey, objectDb);
                return objectDb;
            });
        } finally {
            lock.unlock();
        }
        return objectCache;
    }

    //2.切面编程+双重检查+分布式锁=牛逼大了
    //@Around("@annotation(pers.qh.aop.ShopCache)")
    public Object cacheAroundAdvice2(ProceedingJoinPoint joinPoint) throws Throwable {
        //拿到目标方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        //获取目标方法上的参数
        Object[] methodParms = joinPoint.getArgs();
        //拿到目标方法上的注解
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);

        Object suffix = methodParms[0];
        String prefix = shopCache.value();
        String cacheKey = prefix + ":" + suffix;
        Object objectCache = redisTemplate.opsForValue().get(cacheKey);
        //判断是否加锁
        if(objectCache==null){
            String lockKey = "lock-" + suffix;
            RLock lock = redissonClient.getLock(lockKey);
            try {
                lock.lock();
                //判断是否需要访问数据库
                if(objectCache==null){
                    //获取是否开启布隆过滤器的开关
                    boolean enableBloom = shopCache.enableBloom();
                    Object objectDb=null;
                    if(enableBloom){
                        //布隆过滤器是否存在
                        boolean flag = skuBloomFilter.contains(suffix);
                        if(flag){
                            //执行方法
                            objectDb= joinPoint.proceed();
                        }
                    }else{
                        objectDb= joinPoint.proceed();
                    }
                    redisTemplate.opsForValue().set(cacheKey,objectDb);
                    return objectDb;
                }
            } finally {
                lock.unlock();
            }
        }
        return objectCache;
    }

    //3.改为本地锁
    @Around("@annotation(pers.qh.aop.ShopCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        //拿到目标方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        //获取目标方法上的参数
        Object[] methodParms = joinPoint.getArgs();
        //拿到目标方法上的注解
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);

        Object suffix = methodParms[0];
        String prefix = shopCache.value();
        String cacheKey = prefix + ":" + suffix;
        Object objectCache = redisTemplate.opsForValue().get(cacheKey);
        //判断是否加锁
        if(objectCache==null){
            String lockKey = "lock-" + suffix;
            //利用 intern 才能确定认同一把锁
            synchronized (lockKey.intern()){
                //判断是否访问数据库
                if(objectCache==null){
                    //由于目标方法是读，不是写数据，所有就算在分布式环境下也不会造成数据问题
                    Object objectDb = joinPoint.proceed();
                    redisTemplate.opsForValue().set(cacheKey,objectDb);
                    return objectDb;
                }
            }
        }
        return objectCache;
    }

    //@ShopCache(value = "skuInfo:#{#params}")
    //public SkuInfo getSkuInfo(Long skuId) {
    //4.利用EL表达式获取信息
    //@Around("@annotation(pers.qh.aop.ShopCache)")
    public Object cacheAroundAdvice4(ProceedingJoinPoint joinPoint) throws Throwable {
        //拿到目标方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        //获取目标方法上的参数
        Object[] methodParms = joinPoint.getArgs();
        //拿到目标方法上的注解
        ShopCache shopCache = targetMethod.getAnnotation(ShopCache.class);
        String elExpress = shopCache.value();
        String cacheKye=getExpressionValue(elExpress,methodParms);
        System.out.println(cacheKye);
        return null;
    }

    private String getExpressionValue(String key, Object[] methodParms) {
        //1.获取一个spring的表达式解析式
        SpelExpressionParser elParsesr = new SpelExpressionParser();
        //2.利用解析器解析表达式
        Expression expression = elParsesr.parseExpression(key, new TemplateParserContext());
        //3.准备一个计算环境
        StandardEvaluationContext context = new StandardEvaluationContext();
        //4.设置表达式的值
        context.setVariable("params",methodParms);
        //5.表达式解析之后的结果
        String value = expression.getValue(context, String.class);
        return value;
    }
}
