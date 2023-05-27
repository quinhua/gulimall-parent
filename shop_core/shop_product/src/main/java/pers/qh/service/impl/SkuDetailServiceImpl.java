package pers.qh.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.qh.aop.ShopCache;
import pers.qh.mapper.SkuSalePropertyValueDao;
import pers.qh.service.SkuDetailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private SkuSalePropertyValueDao skuSalePropertyValueDao;


//    @ShopCache(value = "saleSkuIdMapping",enableBloom = false)
    @Override
    public Map<Object, Object> getSalePropertyIdAndSkuIdMapping(Long productId) {
        Map<Object, Object> salePropertyRetMap = new HashMap<>();
        List<Map> retMapList=skuSalePropertyValueDao.getSalePropertyIdAndSkuIdMapping(productId);
        for (Map retMap : retMapList) {
            salePropertyRetMap.put(retMap.get("sale_property_value_id"),retMap.get("sku_id"));
        }
        return salePropertyRetMap;
    }
}