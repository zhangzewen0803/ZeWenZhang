package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 阶段状态信息
 *
 * @author panglx
 * @date 2019/6/24
 */
@ApiModel("阶段状态信息")
@Data
public class StageStatusVO implements Serializable {

    @ApiModelProperty("阶段CODE")
    private String stageCode;

    @ApiModelProperty("阶段名称")
    private String stageName;

    @ApiModelProperty("阶段状态")
    private Integer stageStatus;

    @ApiModelProperty("阶段状态描述")
    private String stageStatusDesc;

    @ApiModelProperty("排序号")
    private Integer sortNo;

}
