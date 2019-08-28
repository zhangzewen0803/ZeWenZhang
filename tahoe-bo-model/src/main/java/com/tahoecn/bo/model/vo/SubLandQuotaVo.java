package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="分期地块规划条件指标",description="分期地块规划条件指标")
public class SubLandQuotaVo extends LandBasicAreaInfoVo{

    @ApiModelProperty(value="地块id", name="landId")
    private String landId;

    @ApiModelProperty(value="地块关联id", name="landExtendId")
    private String landExtendId;

    @ApiModelProperty(value="地块名称", name="landName")
    private String landName;

    @ApiModelProperty(value="地块编号", name="landCode")
    private String landCode;

    @ApiModelProperty(value="全部开发（0-否；1-是）", name="isDev")
    private String isDev;


}
