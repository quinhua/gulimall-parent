package pers.qh.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class SalePropertyKeyVo {
    @ApiModelProperty(value = "销售属性id")
    private Long salePropertyKeyId;
    @ApiModelProperty(value = "销售属性名称(冗余)")
    private String salePropertyKeyName;
    private List<SalePropertyValueVo> salePropertyValueList;
}