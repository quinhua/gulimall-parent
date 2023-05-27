package pers.qh;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pers.qh.result.ResultVo;
import pers.qh.search.SearchParam;

@FeignClient(value = "shop-search")
public interface SearchFeignClient {
    //商品的上架
    @GetMapping("/search/onSale/{skuId}")
    public ResultVo onSale(@PathVariable Long skuId);
    //商品的下架
    @GetMapping("/search/offSale/{skuId}")
    public ResultVo offSale(@PathVariable Long skuId);

    //商品的搜索
    @PostMapping("/search/searchProduct")
    public ResultVo searchProduct(SearchParam searchParam);
}
