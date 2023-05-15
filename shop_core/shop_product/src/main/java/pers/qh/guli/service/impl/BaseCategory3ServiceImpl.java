package pers.qh.guli.service.impl;

import org.springframework.context.annotation.Primary;
import pers.qh.guli.entity.BaseCategory3;
import pers.qh.guli.mapper.BaseCategory3Mapper;
import pers.qh.guli.service.BaseCategory3Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 三级分类表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Primary
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3> implements BaseCategory3Service {

}
