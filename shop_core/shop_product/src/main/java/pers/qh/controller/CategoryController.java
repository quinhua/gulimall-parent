package pers.qh.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.qh.entity.BaseCategory1;
import pers.qh.entity.BaseCategory2;
import pers.qh.entity.BaseCategory3;
import pers.qh.result.ResultVo;
import pers.qh.service.BaseCategory1Service;
import pers.qh.service.BaseCategory2Service;
import pers.qh.service.BaseCategory3Service;
import pers.qh.service.BaseCategoryViewService;
import pers.qh.vo.CategoryVo;

import java.util.List;

/**
 * <p>
 * 一级分类表 前端控制器
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Api(tags = "商品属性分类接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class CategoryController {

    private final BaseCategory1Service category1Service;
    private final BaseCategory2Service category2Service;
    private final BaseCategory3Service category3Service;
    private final BaseCategoryViewService categoryViewService;

    /**
     * 查询商品一级分类 http://localhost:8000/product/getCategory1
     * @return
     */
    @ApiOperation("查询商品一级分类")
    @GetMapping("getCategory1")
    public ResultVo getCategory1(){
        List<BaseCategory1> category1List = category1Service.list(null);
        return ResultVo.ok(category1List);
    }

    /**
     * 查询商品二级分类 http://localhost:8000/product/getCategory2/2
     * @param category1Id
     * @return
     */
    @ApiOperation("查询商品二级分类")
    @GetMapping("getCategory2/{category1Id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category1Id",value = "一级分类id",required = true)
    })
    public ResultVo getCategory2(@PathVariable Long category1Id){
        //QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        // wrapper.eq("category1_id",category1Id);
        wrapper.eq(BaseCategory2::getCategory1Id,category1Id);
        List<BaseCategory2> category2List = category2Service.list(wrapper);
        return ResultVo.ok(category2List);
    }

    /**
     * 查询商品三级分类 http://localhost:8000/product/getCategory3/13
     * @param category2Id
     * @return
     */
    @ApiOperation("查询商品三级分类")
    @GetMapping("getCategory3/{category2Id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category2Id",value = "二级分类id",required = true)
    })
    public ResultVo getCategory3(@PathVariable Long category2Id){
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory3::getCategory2Id,category2Id);
        List<BaseCategory3> category3List = category3Service.list(wrapper);
        return ResultVo.ok(category3List);
    }

    @ApiOperation("首页分类数据的查询")
    @GetMapping("getIndexCategory")
    public List<CategoryVo> getIndexCategory(){
        return categoryViewService.getIndexCategory();
    }

}