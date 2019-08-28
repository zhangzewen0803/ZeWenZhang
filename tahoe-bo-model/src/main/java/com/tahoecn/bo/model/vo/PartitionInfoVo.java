package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目分期分区信息",description="项目分期分区信息")
public class PartitionInfoVo {

    @ApiModelProperty(value="分区id", name="partitionId")
    private String partitionId;

    @ApiModelProperty(value="分区code", name="partitionCode")
    private String partitionCode;

    @ApiModelProperty(value="分区名称", name="partitionName")
    private String partitionName;

}
