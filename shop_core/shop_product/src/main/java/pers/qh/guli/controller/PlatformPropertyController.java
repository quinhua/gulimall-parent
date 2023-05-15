package pers.qh.guli.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.qh.guli.entity.PlatformPropertyKey;
import pers.qh.guli.result.ResultVo;
import pers.qh.guli.service.PlatformPropertyKeyService;

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
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/product")
public class PlatformPropertyController {
    private final PlatformPropertyKeyService propertyKeyService;

    @ApiOperation("根据分类id查询平台属性")
    @GetMapping("/getPlatformPropertyByCategoryId/{category1Id}/{category2Id}/{category3Id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category1Id",value = "一级分类id",required = false),
            @ApiImplicitParam(name = "category2Id",value = "二级分类id",required = false),
            @ApiImplicitParam(name = "category3Id",value = "三级分类id",required = false)
    })
    public ResultVo getPlatformPropertyByCategoryId(
            @PathVariable Long category1Id,
            @PathVariable Long category2Id,
            @PathVariable Long category3Id
    ){
        List<PlatformPropertyKey> propertyKeyList=propertyKeyService.getPlatformPropertyKeyByCategoryId(category1Id,category2Id,category3Id);
        return ResultVo.ok(propertyKeyList);
    }

}