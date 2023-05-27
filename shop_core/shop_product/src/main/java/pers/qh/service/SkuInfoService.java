package pers.qh.service;

import pers.qh.entity.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 库存单元表 服务类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    SkuInfo getSkuInfo(Long skuId);
}
