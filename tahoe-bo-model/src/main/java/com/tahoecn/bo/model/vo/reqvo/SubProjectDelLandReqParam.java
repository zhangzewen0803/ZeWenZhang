package com.tahoecn.bo.model.vo.reqvo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="项目分期删除地块信息",description="项目分期删除基本信息")
public class SubProjectDelLandReqParam {

    @ApiModelProperty(name="subProjectId", value="项目分期id", required=true)
    private String subProjectId;

    @ApiModelProperty(name="LandId", value="地块id", required=true)
    private String LandId;

}
