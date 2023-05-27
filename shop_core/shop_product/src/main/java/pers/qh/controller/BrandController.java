package pers.qh.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import pers.qh.configuration.MinioTemplate;
import pers.qh.entity.BaseBrand;
import pers.qh.result.ResultVo;
import pers.qh.service.BaseBrandService;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author qianhui
 * @since 2023-05-16
 */

@Api(tags = "品牌接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product/brand")
public class BrandController {
    private final MinioTemplate minioTemplate;
    private final BaseBrandService baseBrandService;

    @ApiOperation("分页查询品牌信息")
    @GetMapping("queryBrandByPage/{pageNum}/{pageSize}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "总数", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true)
    })
    public ResultVo queryBrandByPage(
            @PathVariable Long pageNum,
            @PathVariable Long pageSize
    ) {
        IPage<BaseBrand> page = new Page<>(pageNum, pageSize);
        baseBrandService.page(page, null);
        return ResultVo.ok(page);
    }

    @ApiOperation("添加品牌")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brand", value = "品牌信息", required = true)
    })
    public ResultVo saveBrand(
            @RequestBody BaseBrand brand
    ) {
        baseBrandService.save(brand);
        return ResultVo.ok();
    }

    @ApiOperation("根据id查询品牌信息")
    @GetMapping("{brandId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = true)
    })
    public ResultVo getBrandById(
            @PathVariable Long brandId
    ) {
        BaseBrand brand = baseBrandService.getById(brandId);
        return ResultVo.ok(brand);
    }

    @ApiOperation("更新品牌信息")
    @PutMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brand", value = "品牌信息", required = true)
    })
    public ResultVo updateBrand(
            @RequestBody BaseBrand brand
    ) {
        baseBrandService.updateById(brand);
        return ResultVo.ok();
    }

    @ApiOperation("删除品牌信息")
    @DeleteMapping("{brandId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = true)
    })
    public ResultVo remove(
            @PathVariable Long brandId
    ) {
        baseBrandService.removeById(brandId);
        return ResultVo.ok();
    }

    @ApiOperation("查询所有的品牌")
    @GetMapping("getAllBrand")
    public ResultVo getAllBrand() {
        List<BaseBrand> brandList = baseBrandService.list(null);
        return ResultVo.ok(brandList);
    }

    @ApiOperation("fastdfs文件上传")
    @PostMapping("fileUpload1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", required = true)
    })
    public ResultVo fastdfsFileUpload(MultipartFile file) throws Exception {
        String configFilePath = Objects.requireNonNull(this.getClass().getResource("/tracker.conf")).getFile();//需要一个配置文件告诉fastdfs在哪里
        ClientGlobal.init(configFilePath); //初始化
        TrackerClient trackerClient = new TrackerClient();//创建trackerClient 客户端
        TrackerServer trackerServer = trackerClient.getConnection();//用trackerClient获取连接
        StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);//创建StorageClient1
        //对文件实现上传
        String path = storageClient1.upload_appender_file1(
                file.getBytes(),
                FilenameUtils.getExtension(file.getOriginalFilename()),
                null);
        return ResultVo.ok("http://192.168.16.16:8888/"+path);
    }

    @ApiOperation("minio文件上传")
    @PostMapping("fileUpload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", required = true)
    })
    public ResultVo minioFileUpload(MultipartFile file) throws Exception {
        String url = minioTemplate.upload(file);
        return ResultVo.ok(url).message("上传成功");
    }

    @ApiOperation("根据id查询品牌信息")
    @GetMapping("getBaseBrandById/{brandId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = true)
    })
    public BaseBrand getBaseBrand(
            @PathVariable Long brandId
    ) {
        return baseBrandService.getById(brandId);
    }

}