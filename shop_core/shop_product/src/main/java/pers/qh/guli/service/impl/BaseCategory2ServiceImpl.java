package pers.qh.guli.service.impl;

import org.springframework.context.annotation.Primary;
import pers.qh.guli.entity.BaseCategory2;
import pers.qh.guli.mapper.BaseCategory2Mapper;
import pers.qh.guli.service.BaseCategory2Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 二级分类表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Primary
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2> implements BaseCategory2Service {

}
