package pers.qh.mapper;

import feign.Param;
import pers.qh.entity.SkuSalePropertyValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * sku销售属性值 Mapper 接口
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
public interface SkuSalePropertyValueDao extends BaseMapper<SkuSalePropertyValue> {
    List<Map> getSalePropertyIdAndSkuIdMapping(@Param("productId") Long productId);
}
