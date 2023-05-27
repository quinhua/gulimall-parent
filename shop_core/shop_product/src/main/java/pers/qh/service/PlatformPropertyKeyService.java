package pers.qh.service;

import pers.qh.entity.PlatformPropertyKey;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性表 服务类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
public interface PlatformPropertyKeyService extends IService<PlatformPropertyKey> {

    /**
     * 根据三个等级类别查询平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<PlatformPropertyKey> getPropertyByCategoryId(
            Long category1Id,
            Long category2Id,
            Long category3Id
    );

    /**
     * 添加平台属性值
     * @param platformPropertyKey
     */
    void savePlatformProperty(PlatformPropertyKey platformPropertyKey);
}
