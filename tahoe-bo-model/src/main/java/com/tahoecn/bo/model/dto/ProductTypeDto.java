package com.tahoecn.bo.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javafx.scene.chart.ValueAxis;
import lombok.Data;

/**
 * @ClassName：ProductTypeDto
 * @Description：TODO
 * @Author zewenzhang
 * @Date 2019/8/29 14:05
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "业态信息",description = "业态信息")
public class ProductTypeDto {

    @ApiModelProperty(value = "业态id",name = "typeId")
    private String typeId;

    @ApiModelProperty(value = "业态编码",name = "typeCode")
    private String typeCode;

    @ApiModelProperty(value = "业态名称",name = "typeName")
    private String typeName;

    @ApiModelProperty(value = "业态父ID",name = "typeParentId")
    private String typeParentId;
}
