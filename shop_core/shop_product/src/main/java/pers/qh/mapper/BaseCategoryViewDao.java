package pers.qh.mapper;

import pers.qh.entity.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.qh.vo.CategoryVo;

import java.util.List;

/**
 * <p>
 * VIEW Mapper 接口
 * </p>
 *
 * @author qianhui
 * @since 2023-05-19
 */
public interface BaseCategoryViewDao extends BaseMapper<BaseCategoryView> {

    List<CategoryVo> getIndexCategory();
}
