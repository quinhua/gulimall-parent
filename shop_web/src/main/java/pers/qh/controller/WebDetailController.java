package pers.qh.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.qh.ProductFeignClient;
import pers.qh.entity.BaseCategoryView;
import pers.qh.entity.ProductSalePropertyKey;
import pers.qh.entity.SkuInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
@Controller
public class WebDetailController {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPool;

    @GetMapping("{skuId}.html")
    public String skuDetail(@PathVariable Long skuId, Model model){
        //a.根据skuId查询商品的基本信息
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            model.addAttribute("skuInfo", skuInfo);
            return skuInfo;
        },threadPool);
        //b.根据三级分类id获取商品的分类信息 select * from base_category_view a where a.category3_id =61
        CompletableFuture<Void> viewFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
            model.addAttribute("categoryView", categoryView);
        },threadPool);
        //c.根据skuId查询商品的实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            model.addAttribute("price", skuPrice);
        }, threadPool);
        //d.销售属性组合id与skuId的对应关系
        CompletableFuture<Void> idMappingFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long productId = skuInfo.getProductId();
            Map<Object, Object> salePropertyIdAndSkuIdMapping = productFeignClient.getSalePropertyIdAndSkuIdMapping(productId);
            model.addAttribute("salePropertyValueIdJson", JSON.toJSONString(salePropertyIdAndSkuIdMapping));
        },threadPool);
        //e.获取该SKU对应的销售属性(一份)和所有的销售属性(全份)
        CompletableFuture<Void> spuSaleFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long productId = skuInfo.getProductId();
            List<ProductSalePropertyKey> spuSalePropertyList = productFeignClient.getSpuSalePropertyAndSelected(productId, skuId);
            model.addAttribute("spuSalePropertyList", spuSalePropertyList);
        },threadPool);
        CompletableFuture.allOf(
                priceFuture,
                skuInfoFuture,
                viewFuture,
                spuSaleFuture,
                idMappingFuture
        ).join();
        return "detail/index";
    }

    //@GetMapping("{skuId}.html")
    public String skuDetail0(@PathVariable Long skuId, Model model){
        //a.根据skuId查询商品的基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        model.addAttribute("skuInfo",skuInfo);
        //b.根据三级分类id获取商品的分类信息 select * from base_category_view a where a.category3_id =61
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
        model.addAttribute("categoryView",categoryView);
        //c.根据skuId查询商品的实时价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        model.addAttribute("price",skuPrice);
        //d.销售属性组合id与skuId的对应关系
        Long productId = skuInfo.getProductId();
        Map<Object, Object> salePropertyIdAndSkuIdMapping = productFeignClient.getSalePropertyIdAndSkuIdMapping(productId);
        model.addAttribute("salePropertyValueIdJson", JSON.toJSONString(salePropertyIdAndSkuIdMapping));
        //e.获取该SKU对应的销售属性(一份)和所有的销售属性(全份)
        List<ProductSalePropertyKey> spuSalePropertyList = productFeignClient.getSpuSalePropertyAndSelected(productId, skuId);
        model.addAttribute("spuSalePropertyList",spuSalePropertyList);
        return "detail/index";
    }
}