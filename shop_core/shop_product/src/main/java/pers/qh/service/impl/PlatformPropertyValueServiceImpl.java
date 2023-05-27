package pers.qh.service.impl;

import org.springframework.context.annotation.Primary;
import pers.qh.entity.PlatformPropertyValue;
import pers.qh.mapper.PlatformPropertyValueMapper;
import pers.qh.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 属性值表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Primary
@Service
public class PlatformPropertyValueServiceImpl extends ServiceImpl<PlatformPropertyValueMapper, PlatformPropertyValue> implements PlatformPropertyValueService {

}
