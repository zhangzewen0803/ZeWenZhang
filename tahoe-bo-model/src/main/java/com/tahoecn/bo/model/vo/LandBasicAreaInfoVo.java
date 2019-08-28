package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LandBasicAreaInfoVo {

    @ApiModelProperty(value="总用地面积(㎡)", name="totalMeasure")
    private String totalMeasure;

    @ApiModelProperty(value="建设用地面积(㎡)", name="useMeasure")
    private String canUseMeasure;

    @ApiModelProperty(value="计容建面（m²）", name="meterBuildMeasure")
    private String meterBuildMeasure;

    @ApiModelProperty(value="建筑占地面积 (㎡)", name="buildCoverMeasure")
    private String buildCoverMeasure;

    @ApiModelProperty(value="代征地用地面积 (㎡)", name="useTakeMeasure")
    private String useTakeMeasure;

    @ApiModelProperty(value="土地获取价款 (万元)", name="getLandPrice")
    private String getLandPrice;

    @ApiModelProperty(value="地上面积（建筑计容面积）", name="upLandMeasure")
    private String upLandMeasure;

    @ApiModelProperty(value="地下面积（建筑计容面积）", name="downLandMeasure")
    private String downLandMeasure;

}
