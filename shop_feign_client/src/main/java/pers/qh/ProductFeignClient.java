package pers.qh;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.qh.entity.*;
import pers.qh.vo.CategoryVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "shop-product")
public interface ProductFeignClient {
    //a.根据skuId查询商品的基本信息
    @GetMapping("/sku/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable Long skuId);
    //b.根据三级分类id获取商品的分类信息 select * from base_category_view a where a.category3_id =61
    @GetMapping("/sku/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable Long category3Id);
    //c.根据skuId查询商品的实时价格
    @GetMapping("/sku/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable Long skuId) ;
    //d.销售属性组合id与skuId的对应关系
    @GetMapping("/sku/getSalePropertyIdAndSkuIdMapping/{productId}")
    Map<Object, Object> getSalePropertyIdAndSkuIdMapping(@PathVariable Long productId) ;
    //e.获取该SKU对应的销售属性(一份)和所有的销售属性(全份)
    @GetMapping("/sku/getSpuSalePropertyAndSelected/{productId}/{skuId}")
    List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(@PathVariable Long productId, @PathVariable Long skuId) ;

    //根据id查询品牌信息
    @GetMapping("/product/brand/getBaseBrandById/{brandId}")
    BaseBrand getBaseBrandById(@PathVariable Long brandId);

    //首页数据查询
    @GetMapping("/product/getIndexCategory")
    List<CategoryVo> getIndexCategory();

    //根据skuId查询单个商品的平台属性
    @GetMapping("/product/getPlatformPropertyBySkuId/{skuId}")
    List<PlatformPropertyKey> getPlatformPropertyBySkuId(@PathVariable Long skuId);
}