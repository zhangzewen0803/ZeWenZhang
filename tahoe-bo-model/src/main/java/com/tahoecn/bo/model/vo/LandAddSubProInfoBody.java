package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="项目分期添加地块信息(新)",description="项目分期修改基本信息(新)")
public class LandAddSubProInfoBody {

    @ApiModelProperty(name="isDev", value="是否全部开发（0-否；1-是）", required=true)
    private Integer isDev;

    @ApiModelProperty(value="土地Id", name="landId", required=true)
    private String landId;

    @ApiModelProperty(name="versionId", value="版本Id", required=true)
    private String versionId;

    @ApiModelProperty(name="versionId", value="版本Id", required=true)
    private LandBasicAreaInfoVo landBasicAreaInfoVo;

    @ApiModelProperty(value="地块业态信息，数组", required=true)
    private List<LandProductToProjectVo> LandProductToProjectVo;

}
