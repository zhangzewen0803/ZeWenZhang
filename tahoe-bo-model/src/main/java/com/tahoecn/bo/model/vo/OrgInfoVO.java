package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value="组织机构信息",description="组织机构信息")
public class OrgInfoVO {

    @ApiModelProperty(value="组织机构sid", name="fdSid")
    private String fdSid;
    
    @ApiModelProperty(value="组织机构名称", name="fdName")
    private String fdName;
    
    @ApiModelProperty(value="组织机构类型名称", name="fdTypeName")
    private String fdTypeName;
    
    @ApiModelProperty(value="组织机构类型id", name="fdTypeSid")
    private String fdTypeSid;
    
    @ApiModelProperty(value="组织机构类型code", name="fdTypeCode")
    private String fdTypeCode;
    
    @ApiModelProperty(value="组织机构类型属性，1机构，2部门", name="fdTypeBelong")
    private Integer fdTypeBelong;
}
