package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="地块指标数据", description="地块指标数据")
public class LandAreaBasicInfoVo {

    @ApiModelProperty(value="总用地面积-全部(㎡)", name="totalTotalUserArea")
    private String totalTotalUserArea;

    @ApiModelProperty(value="总用地面积-未开发(㎡)", name="notTotalUserArea")
    private String notTotalUserArea;

    @ApiModelProperty(value="总用地面积-本期(㎡)", name="thisTotalUserArea")
    private String thisTotalUserArea;


    @ApiModelProperty(value="计容建面-全部（m²）", name="totalCapBuildArea")
    private String totalCapBuildArea;

    @ApiModelProperty(value="计容建面-未开发（m²）", name="notCapBuildArea")
    private String notCapBuildArea;

    @ApiModelProperty(value="计容建面-本期（m²）", name="thisCapBuildArea")
    private String thisCapBuildArea;


    @ApiModelProperty(value="建设用地面积-全部(㎡)", name="totalCanUseArea")
    private String totalCanUseArea;

    @ApiModelProperty(value="建设用地面积-未开发(㎡)", name="notCanUseArea")
    private String notCanUseArea;

    @ApiModelProperty(value="建设用地面积-本期(㎡)", name="thisCanUseArea")
    private String thisCanUseArea;


    @ApiModelProperty(value="建筑占地面积-全部(㎡)", name="buildCoverMeasure")
    private String totalBuildCoverArea;

    @ApiModelProperty(value="建筑占地面积-未开发(㎡)", name="notBuildCoverArea")
    private String notBuildCoverArea;

    @ApiModelProperty(value="建筑占地面积-本期(㎡)", name="thisBuildCoverArea")
    private String thisBuildCoverArea;


    @ApiModelProperty(value="代征地用地面积-全部(㎡)", name="totalTakeUseArea")
    private String totalTakeUseArea;

    @ApiModelProperty(value="代征地用地面积-未开发(㎡)", name="notTakeUseArea")
    private String notTakeUseArea;

    @ApiModelProperty(value="代征地用地面积-本期(㎡)", name="thisTakeUseArea")
    private String thisTakeUseArea;


    @ApiModelProperty(value="土地获取价款-全部(万元)", name="totalLandPrice")
    private String totalLandPrice;

    @ApiModelProperty(value="土地获取价款-未开发(万元)", name="notLandPrice")
    private String notLandPrice;

    @ApiModelProperty(value="土地获取价款-本期(万元)", name="thisLandPrice")
    private String thisLandPrice;

}
