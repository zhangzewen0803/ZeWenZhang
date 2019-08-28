package com.tahoecn.bo.model.vo.reqvo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目 或 分期生成新版本",description="项目 或 分期生成新版本")
public class ProjectLaunchAppReqParam {

    @ApiModelProperty(name="versionId", value="版本id", required=true)
    private String versionId;

    @ApiModelProperty(name="versionState", value="版本状态", required=false)
    private Integer versionState;

}
