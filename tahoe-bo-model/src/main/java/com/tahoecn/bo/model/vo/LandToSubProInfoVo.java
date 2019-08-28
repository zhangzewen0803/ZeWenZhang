package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="分期地块指标数据", description="分期地块指标数据")
public class LandToSubProInfoVo {

    @ApiModelProperty(value="是否全部开发（0-否；1-是）")
    private Integer isDev;

    @ApiModelProperty(value="地块指标数据")
    private LandAreaBasicInfoVo landAreaBasicInfoVo;

    @ApiModelProperty(value="指标头信息/指标定义信息，数组")
    private List<QuotaHeadVO> head;

    @ApiModelProperty(value="地块业态信息，数组")
    private List<LandProductToProjectVo> LandProductToProjectVo;

}
