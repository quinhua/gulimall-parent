package pers.qh.service;

import pers.qh.entity.BaseBrand;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-16
 */
public interface BaseBrandService extends IService<BaseBrand> {

    void setNum();
}
