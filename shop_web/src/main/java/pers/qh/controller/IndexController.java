package pers.qh.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pers.qh.ProductFeignClient;
import pers.qh.SearchFeignClient;
import pers.qh.result.ResultVo;
import pers.qh.search.SearchParam;
import pers.qh.vo.CategoryVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private SearchFeignClient searchFeignClient;

    @GetMapping({"/", "index", "/index.html"})
    public String index(Model model) {
        //查询分类信息
        List<CategoryVo> list = productFeignClient.getIndexCategory();
        model.addAttribute("list", list);
        return "index/index";
    }

    @RequestMapping("search.html")
    public String searchHtml(SearchParam searchParam, Model model) {
        ResultVo<Map> resultVo = searchFeignClient.searchProduct(searchParam);
        model.addAllAttributes(resultVo.getData());
        //1.浏览器路径上的参数需要进行拼接
        String urlParam=browserPageUrlParam(searchParam);
        model.addAttribute("urlParam",urlParam);
        //2.页面品牌信息需要进行回显
        String brandName = searchParam.getBrandName();
        String brandNameParam=pageBrandParam(brandName);
        model.addAttribute("brandNameParam",brandNameParam);
        //3.平台属性信息的回显
        String[] props = searchParam.getProps();
        List<Map<String,String>> propsParamList=pagePlatformParam(props);
        model.addAttribute("propsParamList",propsParamList);
        //4.需要返回排序的参数给前端页面
        String order = searchParam.getOrder();
        Map<String,Object> orderMap=pageSortParam(order);
        model.addAttribute("orderMap",orderMap);
        return "search/index";
    }

    //order=2:asc
    private Map<String, Object> pageSortParam(String order) {
        Map<String, Object> orderMap = new HashMap<>();
        if(!StringUtils.isEmpty(order)){
            String[] orderSplit = order.split(":");
            if(orderSplit.length==2){
                orderMap.put("type",orderSplit[0]);
                orderMap.put("sort",orderSplit[1]);
            }
        }else{
            //默认给一个排序
            orderMap.put("type",1);
            orderMap.put("sort","desc");
        }
        return orderMap;
    }

    //&props=4:骁龙888:CPU型号&props=5:6.55-6.64英寸:屏幕尺寸
    private List<Map<String, String>> pagePlatformParam(String[] props) {
        List<Map<String, String>> propsParamList = new ArrayList<>();
        if(props!=null&&props.length>0){
            for (String prop : props) {
                //props=4:骁龙888:CPU型号
                String[] propSplit = prop.split(":");
                if(propSplit.length==3){
                    Map<String, String> propMap=new HashMap<>();
                    propMap.put("propertyKey",propSplit[2]);
                    propMap.put("propertyKeyId",propSplit[0]);
                    propMap.put("propertyValue",propSplit[1]);
                    propsParamList.add(propMap);
                }
            }
        }
        return propsParamList;
    }

    //brandName=1:苹果
    private String pageBrandParam(String brandName) {
        //判断是否有品牌信息
        if(!StringUtils.isEmpty(brandName)){
            String[] brandSplit = brandName.split(":");
            if(brandSplit.length==2){
                return "品牌:"+brandSplit[1];
            }
        }
        return null;
    }

    //keyword=高端苹果&brandName=1:苹果&props=4:骁龙888:CPU型号&props=5:6.55-6.64英寸:屏幕尺寸
    private String browserPageUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder();
        //判断是否有关键字
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            urlParam.append("keyword="+searchParam.getKeyword());
        }
        //判断是否有品牌
        if(!StringUtils.isEmpty(searchParam.getBrandName())){
            urlParam.append("&brandName="+searchParam.getBrandName());
        }
        //判断是否有平台属性
        if(searchParam.getProps()!=null&&searchParam.getProps().length>0){
            for (String prop : searchParam.getProps()) {
                urlParam.append("&props="+prop);
            }
        }
        return "search.html?"+urlParam;
    }

}
