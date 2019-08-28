package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限内项目数据信息
 */
@ApiModel("权限内项目数据信息响应对象")
@Data
public class PowerProjectInfoVo{

    @ApiModelProperty(value="组织机构sid", name="id")
    private String id;

    @ApiModelProperty(value="组织机构名称", name="name")
    private String name;
    
    @ApiModelProperty(value="组织机构类型", name="type")
    private String type;
    
    @ApiModelProperty(value="组织机构父级sid", name="pId")
    private String pId;
    
    @ApiModelProperty(value="是否项目 0不是1是", name="isProject")
    private String isProject;

}
