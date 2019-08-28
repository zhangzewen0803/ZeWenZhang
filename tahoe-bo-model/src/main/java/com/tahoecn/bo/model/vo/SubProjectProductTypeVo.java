package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SubProjectProductTypeVo {

    @ApiModelProperty(value="业态Id", name="quoteId")
    private String productId;

    @ApiModelProperty(value="业态TypeId", name="productTypeId")
    private String productTypeId;

    @ApiModelProperty(value="业态Code", name="quoteCode")
    private String productCode;

    @ApiModelProperty(value="业态名称", name="quoteValue")
    private String productName;

    @ApiModelProperty(value="产权CODE", name="attrQuotaValue")
    private String attrQuotaValue;

    @ApiModelProperty(value="项目分期指标信息", name="subProjectQuoteTypeDetails")
    private List<SubProjectQuoteTypeVo> subProjectQuoteTypeDetails;

}
