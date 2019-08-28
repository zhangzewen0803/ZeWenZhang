package com.tahoecn.bo.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value="业态信息",description="业态信息")
public class ProductTypeInfoDto implements Serializable {

    @ApiModelProperty(value="业态id", name="typeId")
    private String typeId;

    @ApiModelProperty(value="业态ParentId", name="typeParentId")
    private String typeParentId;

    @ApiModelProperty(value="业态名称", name="typeName")
    private String typeName;

    @ApiModelProperty(value="业态编码", name="typeCode")
    private String typeCode;

}
