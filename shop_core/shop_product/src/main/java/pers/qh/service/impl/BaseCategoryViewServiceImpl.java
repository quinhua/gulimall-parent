package pers.qh.service.impl;

import pers.qh.entity.BaseCategoryView;
import pers.qh.mapper.BaseCategoryViewDao;
import pers.qh.service.BaseCategoryViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.qh.vo.CategoryVo;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 * VIEW 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-19
 */
@Service
public class BaseCategoryViewServiceImpl extends ServiceImpl<BaseCategoryViewDao, BaseCategoryView> implements BaseCategoryViewService {

    //@Override
    public List<CategoryVo> getIndexCategory1() {
        //查询所有分类信息
        List<BaseCategoryView> allCategoryView = this.list(null);
        //查询所有的一级分类信息
        Map<Long, List<BaseCategoryView>> category1Map = allCategoryView.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        return category1Map.entrySet().stream().map(category1Entry -> {
            Long category1Id = category1Entry.getKey();
            List<BaseCategoryView> category1VoList = category1Entry.getValue();
            CategoryVo category1Vo = new CategoryVo().setCategoryId(category1Id).setCategoryName(category1VoList.get(0).getCategory1Name());
            //获取所有的二级分类信息
            Map<Long, List<BaseCategoryView>> category2Map = category1VoList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            List<CategoryVo> category2List = category2Map.entrySet().stream().map(category2Entry -> {
                Long category2Id = category2Entry.getKey();
                List<BaseCategoryView> category2VoList = category2Entry.getValue();
                CategoryVo category2Vo = new CategoryVo().setCategoryId(category2Id).setCategoryName(category2VoList.get(0).getCategory2Name());
                //获取所有的三级分类信息
                Map<Long, List<BaseCategoryView>> category3Map = category2VoList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                List<CategoryVo> category3List = category3Map.entrySet().stream().map(category3Entry -> {
                    Long category3Id = category3Entry.getKey();
                    List<BaseCategoryView> category3VoList = category3Entry.getValue();
                    return new CategoryVo().setCategoryId(category3Id).setCategoryName(category3VoList.get(0).getCategory3Name());
                }).collect(Collectors.toList());
                category2Vo.setCategoryChild(category3List);
                return category2Vo;
            }).collect(Collectors.toList());
            category1Vo.setCategoryChild(category2List);
            return category1Vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVo> getIndexCategory() {
        return baseMapper.getIndexCategory();
    }

}
