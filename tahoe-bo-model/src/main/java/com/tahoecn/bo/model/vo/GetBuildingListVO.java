package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 楼栋信息
 *
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("楼栋信息")
@Data
public class GetBuildingListVO implements Serializable {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("所属地块ID")
    private String projectLandPartId;

    @ApiModelProperty("所属地块名称")
    private String projectLandPartName;

    @ApiModelProperty("楼栋名称")
    private String name;

}
