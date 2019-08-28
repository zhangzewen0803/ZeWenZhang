package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目基本信息参数字典")
public class BasicProjectParamInfoVo {

    @ApiModelProperty("itemKey")
    private String itemKey;

    @ApiModelProperty("itemValue")
    private String itemValue;

}
