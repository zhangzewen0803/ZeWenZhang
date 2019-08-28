package com.tahoecn.bo.model.vo.reqvo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目下删除地块信息",description="项目下删除地块信息")
public class ProjectDeleteLandReqParam {

    @ApiModelProperty(name="versionId", value="版本id", required=true)
    private String versionId;

    @ApiModelProperty(name="landId", value="土地id", required=true)
    private String landId;

}
