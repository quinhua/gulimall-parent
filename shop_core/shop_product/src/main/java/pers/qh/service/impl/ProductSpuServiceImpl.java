package pers.qh.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.qh.entity.ProductImage;
import pers.qh.entity.ProductSalePropertyKey;
import pers.qh.entity.ProductSalePropertyValue;
import pers.qh.entity.ProductSpu;
import pers.qh.mapper.ProductSpuDao;
import pers.qh.service.ProductImageService;
import pers.qh.service.ProductSalePropertyKeyService;
import pers.qh.service.ProductSpuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
@Service
@RequiredArgsConstructor
public class ProductSpuServiceImpl extends ServiceImpl<ProductSpuDao, ProductSpu> implements ProductSpuService {
    private final ProductImageService productImageService;
    private final ProductSalePropertyKeyService salePropertyKeyService;

    @Transactional
    @Override
    public void saveProductSpu(ProductSpu productSpu) {
        //保存SPU的基本信息
        save(productSpu);
        //保存SPU的图片信息
        Long spuId = productSpu.getId();
        List<ProductImage> productImageList = productSpu.getProductImageList();
        if(!CollectionUtils.isEmpty(productImageList)){
            productImageList.forEach(productImage -> productImage.setProductId(spuId));
            productImageService.saveBatch(productImageList);
        }
        //保存SPU的销售信息
        List<ProductSalePropertyKey> salePropertyKeyList = productSpu.getSalePropertyKeyList();
        if(!CollectionUtils.isEmpty(salePropertyKeyList)){
            salePropertyKeyList.forEach(salePropertyKey -> {
                salePropertyKey.setProductId(spuId);
                List<ProductSalePropertyValue> salePropertyValueList = salePropertyKey.getSalePropertyValueList();
                if(!CollectionUtils.isEmpty(salePropertyValueList)){
                    salePropertyValueList.forEach(salePropertyValue -> {
                        salePropertyValue.setProductId(spuId);
                        salePropertyValue.setSalePropertyKeyName(salePropertyKey.getSalePropertyKeyName());
                    });
                }
            });
            salePropertyKeyService.saveBatch(salePropertyKeyList);
        }
    }
}
