package pers.qh.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.qh.ProductFeignClient;
import pers.qh.dao.ProductMapper;
import pers.qh.entity.BaseBrand;
import pers.qh.entity.BaseCategoryView;
import pers.qh.entity.PlatformPropertyKey;
import pers.qh.entity.SkuInfo;
import pers.qh.search.*;
import pers.qh.service.SearchService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void onSale(Long skuId) {
        Product product = new Product();
        //a.商品的基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo != null) {
            product.setId(skuInfo.getId())
                    .setProductName(skuInfo.getSkuName())
                    .setCreateTime(new Date())
                    .setPrice(skuInfo.getPrice().doubleValue())
                    .setDefaultImage(skuInfo.getSkuDefaultImg());
            //b.品牌的信息
            Long brandId = skuInfo.getBrandId();
            BaseBrand brand = productFeignClient.getBaseBrandById(brandId);
            if (brand != null) {
                product.setBrandId(brandId)
                        .setBrandName(brand.getBrandName())
                        .setBrandLogoUrl(brand.getBrandLogoUrl());
            }
            //c.根据三级分类id查询商品的分类信息
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView categoryView = productFeignClient.getCategoryView(category3Id);
            if (categoryView != null) {
                product.setCategory1Id(categoryView.getCategory1Id())
                        .setCategory1Name(categoryView.getCategory1Name())
                        .setCategory2Id(categoryView.getCategory2Id())
                        .setCategory2Name(categoryView.getCategory2Name())
                        .setCategory3Id(categoryView.getCategory3Id())
                        .setCategory3Name(categoryView.getCategory3Name());
            }
            //d.根据skuId查询商品的平台属性（一个sku有多个（一对）平台属性）
            List<PlatformPropertyKey> platformPropertyKeyList = productFeignClient.getPlatformPropertyBySkuId(skuId);
            if (!CollectionUtils.isEmpty(platformPropertyKeyList)) {
                List<SearchPlatformProperty> searchPlartformList = platformPropertyKeyList.stream().map(platformPropertyKey -> {
                    SearchPlatformProperty searchPlatformProperty = new SearchPlatformProperty();
                    String propertyValue = platformPropertyKey.getPropertyValueList().get(0).getPropertyValue();
                    searchPlatformProperty
                            //平台属性id
                            .setPropertyKeyId(platformPropertyKey.getId())
                            //平台属性名称
                            .setPropertyKey(platformPropertyKey.getPropertyKey())
                            //平台属性值
                            .setPropertyValue(propertyValue);
                    return searchPlatformProperty;
                }).collect(Collectors.toList());
                product.setPlatformProperty(searchPlartformList);
            }
        }
        productMapper.save(product);
    }

    @Override
    public void offSale(Long skuId) {
        productMapper.deleteById(skuId);
    }

    @SneakyThrows
    @Override
    public SearchResponseVo searchProduct(SearchParam searchParam) {
        //1.生成DSL语句
        SearchRequest searchRequest = buildQueryDsl(searchParam);
        //2.实现对DSL语句的调用
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse);
        //3.对结果进行解析
        SearchResponseVo searchResponseVo=parseSearchResult(searchResponse);
        //4.设置其他参数
        searchResponseVo.setPageNo(searchParam.getPageNo());
        searchResponseVo.setPageSize(searchParam.getPageSize());
        //5.设置总页数
        boolean flag=searchResponseVo.getTotal()%searchResponseVo.getPageSize()==0;
        long totalPages=0;
        if(flag){
            totalPages=searchResponseVo.getTotal()/searchResponseVo.getPageSize();
        }else{
            totalPages=searchResponseVo.getTotal()/searchResponseVo.getPageSize()+1;
        }
        searchResponseVo.setTotalPages(totalPages);
        return searchResponseVo;
    }

    private SearchResponseVo parseSearchResult(SearchResponse searchResponse) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //1.商品的基本信息集合
        SearchHits firtstHits = searchResponse.getHits();
        //拿到总记录数
        long totalHits = firtstHits.totalHits;
        searchResponseVo.setTotal(totalHits);
        SearchHit[] secondHits = firtstHits.getHits();
        //如果不判断这个地方会报错
        if(secondHits!=null&&secondHits.length>0){
            for (SearchHit secondHit : secondHits) {
                Product product = JSONObject.parseObject(secondHit.getSourceAsString(), Product.class);
                //获取到高亮的信息
                HighlightField highlightField = secondHit.getHighlightFields().get("productName");
                if(highlightField!=null){
                    Text fragment = highlightField.getFragments()[0];
                    product.setProductName(fragment.toString());
                }
                //把单个获取到的对象放到集合当中
                searchResponseVo.getProductList().add(product);
            }
        }
        //2.商品的品牌信息聚合
        ParsedLongTerms brandIdAgg = searchResponse.getAggregations().get("brandIdAgg");
        List<SearchBrandVo> brandVoList = brandIdAgg.getBuckets().stream().map(bucket -> {
            SearchBrandVo searchBrandVo = new SearchBrandVo();
            //品牌的id
            Number brandId = bucket.getKeyAsNumber();
            searchBrandVo.setBrandId(brandId.longValue());
            //品牌的名称
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            searchBrandVo.setBrandName(brandName);
            //品牌的图片地址
            ParsedStringTerms brandLogoUrlAgg = bucket.getAggregations().get("brandLogoUrlAgg");
            String brandLogoUrl = brandLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchBrandVo.setBrandLogoUrl(brandLogoUrl);
            return searchBrandVo;
        }).collect(Collectors.toList());
        searchResponseVo.setBrandVoList(brandVoList);
        //3.商品的平台属性信息聚合
        ParsedNested platformPropertyAgg = searchResponse.getAggregations().get("platformPropertyAgg");
        ParsedLongTerms propertyKeyIdAgg = platformPropertyAgg.getAggregations().get("propertyKeyIdAgg");
        List<SearchPlatformPropertyVo> searchPropertyVoList = propertyKeyIdAgg.getBuckets().stream().map(bucket -> {
            SearchPlatformPropertyVo searchPlatformPropertyVo = new SearchPlatformPropertyVo();
            // 平台属性Id
            Number propertyKeyId = bucket.getKeyAsNumber();
            searchPlatformPropertyVo.setPropertyKeyId(propertyKeyId.longValue());
            //属性名称
            ParsedStringTerms propertyKeyAgg = bucket.getAggregations().get("propertyKeyAgg");
            String propertyKey = propertyKeyAgg.getBuckets().get(0).getKeyAsString();
            searchPlatformPropertyVo.setPropertyKey(propertyKey);
            //当前属性值的集合
            ParsedStringTerms propertyValueAgg = bucket.getAggregations().get("propertyValueAgg");
            List<String> propertyValueList = propertyValueAgg.getBuckets().stream()
                    .map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
            searchPlatformPropertyVo.setPropertyValueList(propertyValueList);
            return searchPlatformPropertyVo;
        }).collect(Collectors.toList());
        searchResponseVo.setPlatformPropertyList(searchPropertyVoList);
        return searchResponseVo;
    }

    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        //1.构造一个大括号
        SearchSourceBuilder esSqlBuilder = new SearchSourceBuilder();
        //2.构造一个bool
        BoolQueryBuilder firstBool = QueryBuilders.boolQuery();
        //3.构造一级分类过滤器
        if (!StringUtils.isEmpty(searchParam.getCategory1Id())) {
            TermQueryBuilder category1Id = QueryBuilders.termQuery("category1Id", searchParam.getCategory1Id());
            firstBool.filter(category1Id);
        }
        //3.构造二级分类过滤器
        if (!StringUtils.isEmpty(searchParam.getCategory2Id())) {
            TermQueryBuilder category2Id = QueryBuilders.termQuery("category2Id", searchParam.getCategory2Id());
            firstBool.filter(category2Id);
        }
        //3.构造三级分类过滤器
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())) {
            TermQueryBuilder category3Id = QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id());
            firstBool.filter(category3Id);
        }
        //4.构造品牌过滤器 brandName=1:苹果
        String brandName = searchParam.getBrandName();
        if (!StringUtils.isEmpty(brandName)) {
            String[] brandParam = brandName.split(":");
            if (brandParam.length == 2) {
                firstBool.filter(QueryBuilders.termQuery("brandId", brandParam[0]));
            }
        }
        //5.构造关键字查询 keyword=高端苹果
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("productName", keyword).operator(Operator.OR);
            firstBool.must(matchQuery);
        }
        //5.构造平台属性过滤器 props=4:骁龙888:CPU型号&props=5:5.0英寸以下:屏幕尺寸
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0) {
            for (String prop : props) {
                //4:骁龙888:CPU型号
                String[] platformSplit = prop.split(":");
                if (platformSplit.length == 3) {
                    //构造第二个bool
                    BoolQueryBuilder secondBool = QueryBuilders.boolQuery();
                    secondBool.must(QueryBuilders.termQuery("platformProperty.propertyKeyId", platformSplit[0]));
                    secondBool.must(QueryBuilders.termQuery("platformProperty.propertyValue", platformSplit[1]));
                    //构造一个嵌套查询 不做评分机制
                    NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("platformProperty", secondBool, ScoreMode.None);
                    firstBool.filter(nestedQuery);
                }
            }
        }
        //6构造一个query
        esSqlBuilder.query(firstBool);
        //7.构造分页信息 5 2
        int from = (searchParam.getPageNo() - 1) * searchParam.getPageSize();
        esSqlBuilder.from(from);
        esSqlBuilder.size(searchParam.getPageSize());
        /**
         * 8.商品搜索排序
         *  综合排序 order=1:desc 热点排序 单纯商品点击次数
         *  价格排序 order=2:desc price
         */
        String uiOrder = searchParam.getOrder();
        if (!StringUtils.isEmpty(uiOrder)) {
            String[] orderSplit = uiOrder.split(":");
            if (orderSplit.length == 2) {
                String param = orderSplit[0];
                String fileName = "";
                switch (param) {
                    case "1":
                        fileName = "hotScore";
                        break;
                    case "2":
                        fileName = "price";
                        break;
                }
                esSqlBuilder.sort(fileName, "asc".equals(orderSplit[1]) ? SortOrder.ASC : SortOrder.DESC);
            }
        } else {
            //如果没有排序默认给一个综合排序
            esSqlBuilder.sort("hotScore", SortOrder.DESC);
        }
        //9.高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("productName");
        highlightBuilder.preTags("<span style=color:red>");
        highlightBuilder.postTags("</span>");
        esSqlBuilder.highlighter(highlightBuilder);
        //10.构造品牌聚合
        TermsAggregationBuilder brandIdAggBuiler = AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("brandLogoUrlAgg").field("brandLogoUrl"));
        esSqlBuilder.aggregation(brandIdAggBuiler);
        //11.平台舒心聚合 (1+2+4构造法)
        esSqlBuilder.aggregation(
                AggregationBuilders.nested("platformPropertyAgg", "platformProperty")
                        .subAggregation(
                                AggregationBuilders.terms("propertyKeyIdAgg").field("platformProperty.propertyKeyId")
                                        .subAggregation(
                                                AggregationBuilders.terms("propertyKeyAgg").field("platformProperty.propertyKey")
                                        )
                                        .subAggregation(
                                                AggregationBuilders.terms("propertyValueAgg").field("platformProperty.propertyValue")
                                        )
                        )
        );
        //指定使用哪个索引
        SearchRequest searchRequest = new SearchRequest("product");
        searchRequest.types("info");
        searchRequest.source(esSqlBuilder);
        System.out.println("拼接好的DSL:" + esSqlBuilder);
        return searchRequest;
    }

}
