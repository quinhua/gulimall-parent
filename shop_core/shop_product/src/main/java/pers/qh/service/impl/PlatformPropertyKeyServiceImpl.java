package pers.qh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.util.CollectionUtils;
import pers.qh.entity.PlatformPropertyValue;
import pers.qh.entity.PlatformPropertyKey;
import pers.qh.mapper.PlatformPropertyKeyMapper;
import pers.qh.service.PlatformPropertyKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.qh.service.PlatformPropertyValueService;

import java.util.List;

/**
 * <p>
 * 属性表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Primary
@Service
@RequiredArgsConstructor
public class PlatformPropertyKeyServiceImpl extends ServiceImpl<PlatformPropertyKeyMapper, PlatformPropertyKey> implements PlatformPropertyKeyService {
    private final PlatformPropertyValueService propertyValueService;

    /**
     * 根据三个等级类别查询平台属性
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    // @Override
//    public List<PlatformPropertyKey> getPlatformPropertyKeyByCategoryId0(Long category1Id, Long category2Id, Long category3Id) {
//        //1.根据商品分类id商品平台属性名称
//        List<PlatformPropertyKey> propertyKeyList = baseMapper.getPlatformPropertyKeyByCategoryId(category1Id, category2Id, category3Id);
//        //2.根据平台属性名称id商品平台属性值
//        if (!CollectionUtils.isEmpty(propertyKeyList)) {
//            for (PlatformPropertyKey propertyKey : propertyKeyList) {
//                LambdaQueryWrapper<PlatformPropertyValue> wrapper = new LambdaQueryWrapper<>();
//                wrapper.eq(PlatformPropertyValue::getPropertyKeyId, propertyKey.getId());
//                List<PlatformPropertyValue> propertyValueList = propertyValueService.list(wrapper);
//                propertyKey.setPropertyValueList(propertyValueList);
//            }
//        }
//        return propertyKeyList;
//    }

    /**
     * 根据三个等级类别查询平台属性 - 优化
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<PlatformPropertyKey> getPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        return baseMapper.getPropertyByCategoryId(category1Id, category2Id, category3Id);
    }

    /**
     * 添加平台属性值
     * @param platformPropertyKey
     */
    @Override
    public void savePlatformProperty(PlatformPropertyKey platformPropertyKey) {
        //a.判断修改还是添加平台属性
        if (platformPropertyKey.getId() != null) {
            baseMapper.updateById(platformPropertyKey);
            //直接先删除原有的平台属性值集合
            QueryWrapper<PlatformPropertyValue> wrapper = new QueryWrapper<>();
            wrapper.eq("property_key_id", platformPropertyKey.getId());
            propertyValueService.remove(wrapper);
        } else {
            //b.保存平台属性key
            baseMapper.insert(platformPropertyKey);
        }
        //c.保存平台属性值集合
        List<PlatformPropertyValue> propertyValueList = platformPropertyKey.getPropertyValueList();
        if (!CollectionUtils.isEmpty(propertyValueList)) {
            for (PlatformPropertyValue propertyValue : propertyValueList) {
                //设置该平台属性值属于哪个key
                propertyValue.setPropertyKeyId(platformPropertyKey.getId());
            }
            propertyValueService.saveBatch(propertyValueList);
        }
    }

}