package pers.qh.vo;

import io.swagger.annotations.ApiModelProperty;

public class SalePropertyValueVo {
    @ApiModelProperty(value = "销售属性id")
    private Long salePropertyKeyId;
    @ApiModelProperty(value = "销售属性值名称")
    private String salePropertyValueName;
}