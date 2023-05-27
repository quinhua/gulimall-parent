package pers.qh.service;

import pers.qh.entity.BaseCategoryView;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.qh.vo.CategoryVo;

import java.util.List;

/**
 * <p>
 * VIEW 服务类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-19
 */
public interface BaseCategoryViewService extends IService<BaseCategoryView> {

    List<CategoryVo> getIndexCategory();
}
