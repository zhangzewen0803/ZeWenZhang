package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="工程进度填报修改信息",description="工程进度填报修改信息")
public class ProjectSpeedUpdateReqParam {

    @ApiModelProperty(value="项目分期Id", name="projectId", required=true)
    private String projectId;

    @ApiModelProperty(value="进度时间", name="speedTime", required=true)
    private String speedTime;

    @ApiModelProperty(value="工程进度填报信息", name="projectSpeedVoList", required=true)
    private List<ProjectSpeedVo> projectSpeedVoList;

}
