package pers.qh.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.qh.ProductFeignClient;
import pers.qh.SearchFeignClient;
import pers.qh.entity.ProductImage;
import pers.qh.entity.ProductSalePropertyKey;
import pers.qh.entity.SkuInfo;
import pers.qh.mapper.ProductSalePropertyKeyDao;
import pers.qh.result.ResultVo;
import pers.qh.service.ProductImageService;
import pers.qh.service.SkuInfoService;

import java.util.List;

/**
 * <p>
 * 基本销售属性表 前端控制器
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
@Api(tags = "销售属性SKU")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class SkuController {
    private final ProductSalePropertyKeyDao propertyKeyDao;
    private final ProductImageService productImageService;
    private final SkuInfoService skuInfoService;
    private final SearchFeignClient searchFeignClient;

    @ApiOperation("根据分类id查询商品的SPU列表")
    @GetMapping("querySalePropertyByProductId/{spuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spuId",value = "属性id",required = true)
    })
    public ResultVo queryProductSpuByPage(
            @PathVariable Long spuId
    ){
        List<ProductSalePropertyKey> propertyKeyList=propertyKeyDao.querySalePropertyByProductId(spuId);
        return ResultVo.ok(propertyKeyList);
    }

    @ApiOperation("根据productId查询拥有的图片")
    @GetMapping("queryProductImageByProductId/{spuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spuId",value = "属性id",required = true)
    })
    public ResultVo queryProductImageByProductId(
            @PathVariable Long spuId
    ){
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId,spuId);
        List<ProductImage> imageList = productImageService.list(wrapper);
        return ResultVo.ok(imageList);
    }

    @ApiOperation("添加SKU")
    @PostMapping("saveSkuInfo")
    public ResultVo saveSkuInfo(
            @RequestBody SkuInfo skuInfo
            ){
        skuInfoService.saveSkuInfo(skuInfo);
        return ResultVo.ok();
    }

    @ApiOperation("SKU 列表查询")
    @GetMapping("querySkuInfoByPage/{pageNum}/{pageSize}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "总数",required = true),
            @ApiImplicitParam(name = "pageSize",value = "分页大小",required = true),
    })
    public ResultVo querySkuInfoByPage(
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ){
        Page<SkuInfo> page = new Page<>(pageNum,pageSize);
        skuInfoService.page(page,null);
        return ResultVo.ok(page);
    }

    @ApiOperation("商品上架")
    @GetMapping("onSale/{skuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "属性id",required = true)
    })
    public ResultVo onSale(
            @PathVariable Long skuId
    ){
        SkuInfo skuInfo = new SkuInfo().setId(skuId).setIsSale(1);
        skuInfoService.updateById(skuInfo);
        searchFeignClient.onSale(skuId);
        return ResultVo.ok("上架成功");
    }

    @ApiOperation("商品下架")
    @GetMapping("offSale/{skuId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId",value = "属性id",required = true)
    })
    public ResultVo offSale(
            @PathVariable Long skuId
    ){
        SkuInfo skuInfo = new SkuInfo().setId(skuId).setIsSale(0);
        skuInfoService.updateById(skuInfo);
        searchFeignClient.offSale(skuId);
        return ResultVo.ok("下架成功");
    }
}

