package pers.qh.guli.service.impl;

import org.springframework.context.annotation.Primary;
import pers.qh.guli.entity.BaseCategory1;
import pers.qh.guli.mapper.BaseCategory1Mapper;
import pers.qh.guli.service.BaseCategory1Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 一级分类表 服务实现类
 * </p>
 *
 * @author qianhui
 * @since 2023-05-15
 */
@Primary
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategory1Service {

}
