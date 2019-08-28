package com.tahoecn.bo.model.vo.reqvo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目分期生成新版本",description="项目分期生成新版本")
public class SubProjectUpdateVersReqParam {

    @ApiModelProperty(name="subProjectId", value="项目分期id", required=true)
    private String subProjectId;

}
