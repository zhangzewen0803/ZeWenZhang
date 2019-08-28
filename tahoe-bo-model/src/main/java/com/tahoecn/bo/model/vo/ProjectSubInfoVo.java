package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分期数据信息
 */
@ApiModel("分期数据信息响应对象")
@Data
public class ProjectSubInfoVo{

    @ApiModelProperty(value="组织机构sid", name="id")
    private String id;

    @ApiModelProperty(value="组织机构名称", name="name")
    private String name;
    
    @ApiModelProperty(value="组织机构父级sid", name="pId")
    private String pId;
    
    @ApiModelProperty(value="组织机构父级名称", name="pname")
    private String pname;
}
