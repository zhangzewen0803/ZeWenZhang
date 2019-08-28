package com.tahoecn.bo.model.vo.reqvo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目更新信息",description="项目更新信息")
public class ProjectUpdateInfoReqParam {

    @ApiModelProperty(name="versionId", value="版本id", required=true)
    private String versionId;

    @ApiModelProperty(name="city", value="所在城市", required=false)
    private String city;

    @ApiModelProperty(name="projectCaseName", value="项目案名", required=false)
    private String projectCaseName;

    @ApiModelProperty(name="projectAddress", value="项目地址", required=false)
    private String projectAddress;

    @ApiModelProperty(name="obtainStatusCode", value="获取状态Code", required=false)
    private String obtainStatusCode;

    @ApiModelProperty(name="operateWayCode", value="操盘方式Code", required=false)
    private String operateWayCode;

    @ApiModelProperty(name="projectPic", value="项目总图", required=false)
    private String projectPic;

    @ApiModelProperty(name="projectPoint", value="项目总图坐标", required=false)
    private String projectPoint;
}
