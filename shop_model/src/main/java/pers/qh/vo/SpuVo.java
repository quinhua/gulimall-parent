package pers.qh.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class SpuVo {
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @ApiModelProperty(value = "商品描述(后台简述）")
    private String description;
    @ApiModelProperty(value = "三级分类id")
    private Long category3Id;
    @ApiModelProperty(value = "品牌id")
    private Long brandId;
    private List<SpuImageVo> productImageList;
    private List<SalePropertyKeyVo> salePropertyKeyList;
}
