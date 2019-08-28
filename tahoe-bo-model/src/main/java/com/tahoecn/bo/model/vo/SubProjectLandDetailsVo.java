package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="项目分期指定地块详情信息",description="项目分期指定地块详情信息")
public class SubProjectLandDetailsVo extends SubProjectLandAreaDetailsVo{

    @ApiModelProperty(value="项目分期Id", name="subProjectId")
    private String subProjectId;

    @ApiModelProperty(value="分期业态详情信息", name="subProjectProductTypeDetails")
    private List<SubProjectProductTypeVo> subProjectProductTypeDetails;
}
