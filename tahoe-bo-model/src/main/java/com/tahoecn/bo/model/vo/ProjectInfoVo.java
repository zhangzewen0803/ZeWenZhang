package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value="项目信息",description="项目信息")
public class ProjectInfoVo {

    @ApiModelProperty(value="项目Id", name="projectId")
    private String projectId;

    @ApiModelProperty(value="版本Id", name="versionId")
    private String versionId;

    @ApiModelProperty(value="项目名称", name="projectName")
    private String projectName;

    @ApiModelProperty(value="项目案名", name="projectCaseName")
    private String projectCaseName;

    @ApiModelProperty(value="所属区域", name="areaCompany")
    private String areaCompany;

    @ApiModelProperty(value="所属城市公司", name="cityCompany")
    private String cityCompany;

    @ApiModelProperty(value="所在城市Id", name="cityId")
    private String cityId;

    @ApiModelProperty(value="所在城市", name="city")
    private String city;

    @ApiModelProperty(value="获取状态Code", name="obtainStatusCode")
    private String obtainStatusCode;

    @ApiModelProperty(value="项目地址", name="projectAddress")
    private String projectAddress;

    @ApiModelProperty(value="项目地理点位", name="addressPoint")
    private String addressPoint;

    @ApiModelProperty(value="项目总图", name="projectPic")
    private String projectPic;

    @ApiModelProperty(value="分期总图", name="totalPic")
    private List<String> totalPic;

}
