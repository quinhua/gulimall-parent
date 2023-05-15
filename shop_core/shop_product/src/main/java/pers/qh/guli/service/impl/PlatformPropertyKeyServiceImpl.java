package pers.qh.guli.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.util.CollectionUtils;
import pers.qh.guli.entity.PlatformPropertyKey;
import pers.qh.guli.entity.PlatformPropertyValue;
import pers.qh.guli.mapper.PlatformPropertyKeyMapper;
import pers.qh.guli.service.PlatformPropertyKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.qh.guli.service.PlatformPropertyValueService;

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

    @Override
    public List<PlatformPropertyKey> getPlatformPropertyKeyByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        //1.根据商品分类id商品平台属性名称
        List<PlatformPropertyKey> propertyKeyList = baseMapper.getPlatformPropertyKeyByCategoryId(category1Id, category2Id, category3Id);
        //2.根据平台属性名称id商品平台属性值
        if (!CollectionUtils.isEmpty(propertyKeyList)) {
            for (PlatformPropertyKey propertyKey : propertyKeyList) {
                LambdaQueryWrapper<PlatformPropertyValue> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PlatformPropertyValue::getPropertyKeyId, propertyKey.getId());
                List<PlatformPropertyValue> propertyValueList = propertyValueService.list(wrapper);
                propertyKey.setPropertyValueList(propertyValueList);
            }
        }
        return propertyKeyList;
    }

    //@Override
    public List<PlatformPropertyKey> getPlatformPropertyByCategoryId2(Long category1Id, Long category2Id, Long category3Id) {
        return baseMapper.getPlatformPropertyKeyByCategoryId(category1Id, category2Id, category3Id);
    }
}