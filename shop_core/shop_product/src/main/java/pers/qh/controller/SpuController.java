package pers.qh.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.qh.entity.BaseSaleProperty;
import pers.qh.entity.ProductSpu;
import pers.qh.result.ResultVo;
import pers.qh.service.BaseSalePropertyService;
import pers.qh.service.ProductSpuService;

import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author qianhui
 * @since 2023-05-17
 */
@Api("平台属性SPU")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class SpuController {
    private final ProductSpuService SpuService;
    private final BaseSalePropertyService baseSalePropertyService;
    @ApiOperation("根据分类id查询商品的SPU列表")
    @GetMapping("queryProductSpuByPage/{pageNum}/{pageSize}/{category3Id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "总数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true),
            @ApiImplicitParam(name = "category3Id", value = "三级id", required = true)
    })
    public ResultVo queryProductSpuByPage(
            @PathVariable Long pageNum,
            @PathVariable Long pageSize,
            @PathVariable Long category3Id
    ) {
        IPage<ProductSpu> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSpu::getCategory3Id,category3Id);
        SpuService.page(page,wrapper);
        return ResultVo.ok(page);
    }

    @ApiOperation("查询所有销售属性")
    @GetMapping("queryAllSaleProperty")
    public ResultVo queryAllSaleProperty() {
        List<BaseSaleProperty> list = baseSalePropertyService.list(null);
        return ResultVo.ok(list);
    }

    @ApiOperation("添加SPU")
    @PostMapping("saveProductSpu")
    public ResultVo saveProductSpu(
            @RequestBody ProductSpu productSpu
    ) {
        SpuService.saveProductSpu(productSpu);
        return ResultVo.ok();
    }

}

