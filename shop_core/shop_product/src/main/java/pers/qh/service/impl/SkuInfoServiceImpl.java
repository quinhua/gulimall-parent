package pers.qh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import pers.qh.aop.ShopCache;
import pers.qh.exception.SleepUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.qh.constant.RedisConst;
import pers.qh.entity.SkuImage;
import pers.qh.entity.SkuInfo;
import pers.qh.entity.SkuPlatformPropertyValue;
import pers.qh.entity.SkuSalePropertyValue;
import pers.qh.mapper.SkuInfoDao;
import pers.qh.service.SkuImageService;
import pers.qh.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.qh.service.SkuPlatformPropertyValueService;
import pers.qh.service.SkuSalePropertyValueService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 库存单元表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
@Service
@RequiredArgsConstructor
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfo> implements SkuInfoService {
    private final SkuPlatformPropertyValueService skuPlatformValueService;
    private final SkuSalePropertyValueService skuSaleValueService;
    private final SkuImageService skuImageService;
    private final RedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final RBloomFilter skuBloomFilter;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存SKU的基本信息
        this.save(skuInfo);
        //保存 SKU 的平台属性
        Long skuId = skuInfo.getId();
        Long productId = skuInfo.getProductId();
        List<SkuPlatformPropertyValue> skuPlatformValueList = skuInfo.getSkuPlatformPropertyValueList();
        if (!CollectionUtils.isEmpty(skuPlatformValueList)) {
            skuPlatformValueList.forEach(skuPlatformValue -> skuPlatformValue.setSkuId(skuId));
            skuPlatformValueService.saveBatch(skuPlatformValueList);
        }
        //保存SKU的销售属性信息
        List<SkuSalePropertyValue> skuSalePropertyValueList = skuInfo.getSkuSalePropertyValueList();
        if (!CollectionUtils.isEmpty(skuSalePropertyValueList)) {
            skuSalePropertyValueList.forEach(skuSalePropertyValue -> {
                skuSalePropertyValue.setSkuId(skuId);
                skuSalePropertyValue.setProductId(productId);
            });
            skuSaleValueService.saveBatch(skuSalePropertyValueList);
        }
        //保存SKU的图片信息
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            skuImageList.forEach(skuImage -> skuImage.setSkuId(skuId));
            skuImageService.saveBatch(skuImageList);
        }
    }

    //@ShopCache("skuInfo")
    //@ShopCache(value = "skuInfo:#{#params}")
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = getSkuInfoFromRedis(skuId);
        //SkuInfo skuInfo = getSkuInfoFromRedisWithTreadLock(skuId);
        //SkuInfo skuInfo = getSkuInfoFromRedisson(skuId);
        return skuInfo;
    }

    private SkuInfo getSkuInfoFromRedisson(Long skuId) {
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        String lockKey = "lock-" + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        SkuInfo skuInfoResult = Optional.ofNullable(skuInfo).orElseGet(() -> {
            try {
                boolean flag = skuBloomFilter.contains(skuId);
                if (flag) {
                    lock.lock();
                    SkuInfo skuInfoFromDb = getSkuInfoFromDb(skuId);
                    redisTemplate.opsForValue().set(cacheKey, skuInfoFromDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    return skuInfoFromDb;
                }
                return null;
            } finally {
                lock.unlock();
            }
        });
        return skuInfoResult;
    }

    ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private SkuInfo getSkuInfoFromRedisWithTreadLock(Long skuId) {
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        //锁的粒度太大 需要更改
        String lockKey = "lock-" + skuId;
        if (skuInfo == null) {
            String token = threadLocal.get();
            boolean accquireLock = false;
            if (!StringUtils.isEmpty(token)) {
                //已经拿到过锁了
                accquireLock = true;
            } else {
                token = UUID.randomUUID().toString();

                accquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 5, TimeUnit.SECONDS);
            }
            if (accquireLock) {
                SkuInfo skuInfoDb = getSkuInfoFromDb(skuId);
                redisTemplate.opsForValue().set(cacheKey, skuInfoDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(luaScript);
                redisScript.setResultType(Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), token);
                //擦屁股
                threadLocal.remove();
                return skuInfoDb;
            } else {
                //自旋 目的是为了去拿锁
                while (true) {
                    SleepUtils.sleep(50);
                    boolean retryAccquireLock = redisTemplate.opsForValue().setIfAbsent(lockKey, token, 5, TimeUnit.SECONDS);
                    if (retryAccquireLock) {
                        threadLocal.set(token);
                        break;
                    }
                }
                return getSkuInfoFromRedisWithTreadLock(skuId);
            }
        }
        return skuInfo;
    }

    private SkuInfo getSkuInfoFromRedis(Long skuId) {
        //sku:24:info
        String cacheKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //redis采用utf-8编码
        //redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redisTemplate.setValueSerializer();
        SkuInfo skuInfoRedis = (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        if (skuInfoRedis == null) {
            SkuInfo skuInfoDb = getSkuInfoFromDb(skuId);
            //把数据放入到redis中
            redisTemplate.opsForValue().set(cacheKey, skuInfoDb, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
            return skuInfoDb;
        }
        return skuInfoRedis;
    }

    private SkuInfo getSkuInfoFromDb(Long skuId) {
        //1.商品的基本信息
        SkuInfo skuInfo = getById(skuId);
        //2.商品的图片信息
        if (skuInfo != null) {
            LambdaQueryWrapper<SkuImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SkuImage::getSkuId, skuId);
            List<SkuImage> skuImageList = skuImageService.list(wrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

}
