package com.tahoecn.bo.model.vo.reqvo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目生成新版本",description="项目生成新版本")
public class ProjectUpdateVersReqParam {

    @ApiModelProperty(name="projectId", value="项目id", required=true)
    private String projectId;

    @ApiModelProperty(name="version", value="版本号（只有 项目OR分期修改审批状态 接口需要,不传默认为最新的）", required=false)
    private String version;

}
