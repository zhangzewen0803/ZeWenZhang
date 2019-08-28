package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SubProjectLandAreaDetailsVo extends LandBasicAreaInfoVo{

    @ApiModelProperty(value="总用地面积(㎡)", name="tt_totalMeasure")
    private String tt_totalMeasure;

    @ApiModelProperty(value="建设用地面积(㎡)", name="tt_canUseMeasure")
    private String tt_canUseMeasure;

    @ApiModelProperty(value="计容建面（m²）", name="tt_meterBuildMeasure")
    private String tt_meterBuildMeasure;

    @ApiModelProperty(value="建筑占地面积 (㎡)", name="tt_buildCoverMeasure")
    private String tt_buildCoverMeasure;

    @ApiModelProperty(value="代征地用地面积 (㎡)", name="tt_useTakeMeasure")
    private String tt_useTakeMeasure;

    @ApiModelProperty(value="土地获取价款 (万元)", name="tt_getLandPrice")
    private String tt_getLandPrice;

    @ApiModelProperty(value="地上面积（建筑计容面积）", name="tt_upLandMeasure")
    private String tt_upLandMeasure;

    @ApiModelProperty(value="地下面积（建筑计容面积）", name="tt_downLandMeasure")
    private String tt_downLandMeasure;

}
