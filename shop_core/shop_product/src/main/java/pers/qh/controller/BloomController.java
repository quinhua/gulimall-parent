package pers.qh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.qh.entity.SkuInfo;
import pers.qh.result.ResultVo;
import pers.qh.service.SkuInfoService;

import java.util.List;

@Api(tags = "布隆过滤器初始化")
@RestController
@RequestMapping("/init")
@RequiredArgsConstructor
public class BloomController {
    private final SkuInfoService skuInfoService;
    private final RBloomFilter skuBlooFilter;

    @GetMapping("/sku/bloom")
    public ResultVo skuBloom(){
        //查询数据库中所有的id
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SkuInfo::getId);
        List<SkuInfo> list = skuInfoService.list(wrapper);
        list.forEach(skuInfo -> {
            //把id全部放入布隆过滤器中
            skuBlooFilter.add(skuInfo.getId());
        });
        return ResultVo.ok().message("初始化成功！");
    }
}
