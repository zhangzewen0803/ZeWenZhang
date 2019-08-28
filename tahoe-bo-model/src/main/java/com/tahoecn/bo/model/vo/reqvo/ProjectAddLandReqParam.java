package com.tahoecn.bo.model.vo.reqvo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目下添加地块信息",description="项目下添加地块信息")
public class ProjectAddLandReqParam {

    @ApiModelProperty(name="projectId", value="项目id", required=true)
    private String projectId;

    @ApiModelProperty(name="landId", value="土地id", required=true)
    private String landId;

}
