package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 楼栋信息
 *
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("楼栋信息")
@Data
public class BuildingVO implements Serializable {

    @ApiModelProperty("主键ID")
    private String id;

    @NotBlank(message = "building -> projectLandPartId不能为空")
    @ApiModelProperty("所属地块ID")
    private String projectLandPartId;

    @NotBlank(message = "building -> projectLandPartName不能为空")
    @ApiModelProperty("所属地块名称")
    private String projectLandPartName;

    @NotBlank(message = "building -> name不能为空")
    @ApiModelProperty("楼栋名称")
    private String name;

    @Valid
    @NotNull(message = "building -> quotaList不能为空")
    @ApiModelProperty("指标项")
    private List<QuotaBodyVO> quotaList;

}
