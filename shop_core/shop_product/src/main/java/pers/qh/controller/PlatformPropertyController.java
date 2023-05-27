package pers.qh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.qh.entity.PlatformPropertyKey;
import pers.qh.entity.PlatformPropertyValue;
import pers.qh.mapper.PlatformPropertyKeyMapper;
import pers.qh.result.ResultVo;
import pers.qh.service.PlatformPropertyKeyService;
import pers.qh.service.PlatformPropertyValueService;

import java.util.List;

/**
 * <p>
 * 属性表 前端控制器
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Api(tags = "平台属性接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class PlatformPropertyController {
    private final PlatformPropertyKeyService propertyKeyService;
    private final PlatformPropertyValueService propertyValueService;
    private final PlatformPropertyKeyMapper propertyKeyMapper;

    @ApiOperation("根据分类id查询平台属性")
    @GetMapping("/getPlatformPropertyByCategoryId/{category1Id}/{category2Id}/{category3Id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category1Id",value = "一级分类id",required = true),
            @ApiImplicitParam(name = "category2Id",value = "二级分类id",required = true),
            @ApiImplicitParam(name = "category3Id",value = "三级分类id",required = true)
    })
    public ResultVo getPlatformPropertyByCategoryId(
            @PathVariable Long category1Id,
            @PathVariable Long category2Id,
            @PathVariable Long category3Id
    ){
        List<PlatformPropertyKey> propertyKeyList=propertyKeyService.getPropertyByCategoryId(category1Id,category2Id,category3Id);
        return ResultVo.ok(propertyKeyList);
    }

    @ApiOperation("根据平台属性keyId查询平台属性值")
    @GetMapping("/getPropertyValueByPropertyKeyId/{propertyKeyId}")
    public ResultVo getPropertyValueByPropertyKeyId(
            @PathVariable Long propertyKeyId
    ){
        LambdaQueryWrapper<PlatformPropertyValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformPropertyValue::getPropertyKeyId,propertyKeyId);
        List<PlatformPropertyValue> list = propertyValueService.list(wrapper);
        return ResultVo.ok(list);
    }

    @ApiOperation("添加平台属性值")
    @PostMapping("/savePlatformProperty")
    public ResultVo savePlatformProperty(
           @RequestBody PlatformPropertyKey platformPropertyKey
    ){
        propertyKeyService.savePlatformProperty(platformPropertyKey);
        return ResultVo.ok();
    }

    @ApiOperation("根据skuId查询单个商品的平台属性")
    @GetMapping("getPlatformPropertyBySkuId/{skuId}")
    public List<PlatformPropertyKey> getPlatformPropertyBySkuId(@PathVariable Long skuId){
        return propertyKeyMapper.getPlatformPropertyBySkuId(skuId);
    }

}