package pers.qh.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import pers.qh.entity.BaseCategoryView;
import pers.qh.entity.ProductSalePropertyKey;
import pers.qh.entity.SkuInfo;
import pers.qh.mapper.ProductSalePropertyKeyDao;
import pers.qh.service.BaseCategoryViewService;
import pers.qh.service.SkuDetailService;
import pers.qh.service.SkuInfoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * VIEW 前端控制器
 * </p>
 *
 * @author qianhui
 * @since 2023-05-19
 */
@Api(tags = "商品详情-接口")
@RestController
@RequestMapping("/sku")
@RequiredArgsConstructor
public class SkuDetailController {
    private final SkuInfoService skuInfoService;
    private final BaseCategoryViewService categoryViewService;
    private final SkuDetailService skuDetailService;
    private final ProductSalePropertyKeyDao salePropertyKeyDao;

    @ApiOperation("根据skuid查询商品的基本信息")
    @GetMapping("getSkuInfo/{skuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "skuId",required = true)
    })
    public SkuInfo getSkuInfo(
            @PathVariable Long skuId
    ) {
        SkuInfo skuInfo=skuInfoService.getSkuInfo(skuId);
        return skuInfo;
    }

    @ApiOperation("根据三级分类id获取商品的分类信息")
    @GetMapping("getCategoryView/{category3Id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category3Id",value = "category3Id",required = true)
    })
    public BaseCategoryView getCategoryView(
            @PathVariable Long category3Id
    ) {
        return categoryViewService.getById(category3Id);
    }

    @ApiOperation("根据skuId查询商品的实时价格")
    @GetMapping("getSkuPrice/{skuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "skuId",required = true)
    })
    public BigDecimal getSkuPrice(
            @PathVariable Long skuId
    ) {
        SkuInfo skuInfo=skuInfoService.getById(skuId);
        return skuInfo.getPrice();
    }

    @ApiOperation("销售属性组合id与skuId的对应关系")
    @GetMapping("getSalePropertyIdAndSkuIdMapping/{productId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId",value = "productId",required = true)
    })
    public Map<Object, Object> getSalePropertyIdAndSkuIdMapping(
            @PathVariable Long productId
    ) {
        return skuDetailService.getSalePropertyIdAndSkuIdMapping(productId);
    }


    @ApiOperation("获取该SKU对应的销售属性(一份)和所有的销售属性(全份)")
    @GetMapping("getSpuSalePropertyAndSelected/{productId}/{skuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId",value = "productId",required = true),
            @ApiImplicitParam(name = "skuId",value = "skuId",required = true)
    })
    public List<ProductSalePropertyKey> getSpuSalePropertyAndSelected(
            @PathVariable Long productId,@PathVariable Long skuId
    ) {
        return salePropertyKeyDao.getSpuSalePropertyAndSelected(productId,skuId);
    }

}

