package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value="分期业态数据", description="分期业态数据")
public class LandProductToProjectVo {

    @ApiModelProperty(value = "业态Id")
    private String productTypeId;

    @NotBlank(message = "productTypeCode不能为空")
    @ApiModelProperty(value = "业态CODE")
    private String productTypeCode;

    @NotBlank(message = "productTypeName不能为空")
    @ApiModelProperty(value = "业态名称")
    private String productTypeName;

    @NotBlank(message = "productAttrType不能为空")
    @ApiModelProperty(value = "产权名称")
    private String productAttrType;

    @Valid
    @NotNull(message = "quotaList不能为空")
    @ApiModelProperty(value = "指标列表")
    private List<QuotaBodyVO> quotaList;

}
