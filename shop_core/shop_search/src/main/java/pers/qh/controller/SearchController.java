package pers.qh.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;
import pers.qh.result.ResultVo;
import pers.qh.search.Product;
import pers.qh.search.SearchParam;
import pers.qh.search.SearchResponseVo;
import pers.qh.service.SearchService;


@Api("es-接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    @Autowired
    private ElasticsearchRestTemplate esTemplate;
    @Autowired
    private SearchService searchService;

    @ApiOperation("创建索引")
    @GetMapping("/createIndex")
    public ResultVo createIndex() {
        esTemplate.createIndex(Product.class);
        esTemplate.putMapping(Product.class);
        return ResultVo.ok().message("创建成功");
    }

    @ApiOperation("商品的上架")//=往ES中添加数据
    @GetMapping("onSale/{skuId}")
    public ResultVo onSale(
            @PathVariable Long skuId
    ) {
        searchService.onSale(skuId);
        return ResultVo.ok().message("上架成功");
    }

    @ApiOperation("商品的下架")
    @GetMapping("offSale/{skuId}")
    public ResultVo offSale(@PathVariable Long skuId) {
        searchService.offSale(skuId);
        return ResultVo.ok().message("下架成功");
    }

    @ApiOperation("商品的搜索")
    @PostMapping("searchProduct")
    public ResultVo searchProduct(
            @RequestBody SearchParam searchParam
    ) {
        SearchResponseVo list=searchService.searchProduct(searchParam);
        return ResultVo.ok(list);
    }

}
