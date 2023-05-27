package pers.qh.mapper;

import org.apache.ibatis.annotations.Param;
import pers.qh.entity.PlatformPropertyKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 属性表 Mapper 接口
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
public interface PlatformPropertyKeyMapper extends BaseMapper<PlatformPropertyKey> {

    List<PlatformPropertyKey> getPropertyByCategoryId(
            @Param("category1Id") Long category1Id,
            @Param("category2Id") Long category2Id,
            @Param("category3Id") Long category3Id
    );

    List<PlatformPropertyKey> getPlatformPropertyBySkuId(@Param("skuId") Long skuId);
}