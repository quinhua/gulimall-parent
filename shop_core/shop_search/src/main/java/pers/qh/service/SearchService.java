package pers.qh.service;

import pers.qh.search.SearchParam;
import pers.qh.search.SearchResponseVo;

import java.util.List;

public interface SearchService {
    void onSale(Long skuId);

    void offSale(Long skuId);


    SearchResponseVo searchProduct(SearchParam searchParam);
}
