package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="项目分期信息",description="项目分期信息")
public class SubProjectInfoVo {

    @ApiModelProperty(value="版本Id", name="versionId")
    private String versionId;

    @ApiModelProperty(value="项目分期Id", name="subProjectId")
    private String subProjectId;

    @ApiModelProperty(value="项目Id", name="projectId")
    private String projectId;

    @ApiModelProperty(value="项目名称", name="projectName")
    private String projectName;

    @ApiModelProperty(value="项目分期名称", name="subProjectName")
    private String subProjectName;

    @ApiModelProperty(value="项目类型", name="projectType")
    private String projectType;

    @ApiModelProperty(value="操盘方式", name="tradeModeCode")
    private String tradeModeCode;

    @ApiModelProperty(value="并表方式", name="mergeTableTypeCode")
    private String mergeTableTypeCode;

    @ApiModelProperty(value="计税方式", name="taxTypeCode")
    private String taxTypeCode;

    @ApiModelProperty(value="分期总图", name="totalSubPic")
    private String totalSubPic;

    @ApiModelProperty(value="分区数量", name="partitionNum")
    private String partitionNum;

    @ApiModelProperty(value="分期分区详情", name="partitionDetails")
    private List<PartitionInfoVo> partitionDetails;

    @ApiModelProperty(value="获取状态Code", name="obtainStatusCode")
    private String obtainStatusCode;


}
