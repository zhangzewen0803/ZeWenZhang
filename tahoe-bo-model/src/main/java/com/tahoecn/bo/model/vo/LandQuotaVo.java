package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="地块规划条件指标",description="地块规划条件指标")
public class LandQuotaVo {

    @ApiModelProperty(value="地块id", name="landId")
    private String landId = "";

    @ApiModelProperty(value="地块名称", name="landName")
    private String landName = "";

    @ApiModelProperty(value="地块编号", name="landCode")
    private String landCode = "";

    @ApiModelProperty(value="总用地面积", name="landTotalMeasure")
    private String landTotalMeasure = "";

    @ApiModelProperty(value="可建设用地面积", name="landConstructableMeasure")
    private String landConstructableMeasure = "";

    @ApiModelProperty(value="计容建筑面积", name="meterBuildMeasure")
    private String meterBuildMeasure;

    @ApiModelProperty(value="建筑占地面积", name="buildingCoverArea")
    private String buildingCoverArea = "";

    @ApiModelProperty(value="代征用地面积", name="takeLandUseArea")
    private String takeLandUseArea = "";

    @ApiModelProperty(value="代征用地面积", name="landPrice")
    private String landPrice = "";

    @ApiModelProperty(value="溢价比例", name="premiumProportion")
    private String premiumProportion = "";

}
