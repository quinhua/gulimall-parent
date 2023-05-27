package pers.qh.service.impl;

import pers.qh.entity.ProductImage;
import pers.qh.mapper.ProductImageDao;
import pers.qh.service.ProductImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品图片表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageDao, ProductImage> implements ProductImageService {

}
