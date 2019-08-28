package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="选择地块信息",description="选择地块信息")
public class LandSelInfoVo extends LandBasicAreaInfoVo{

    @ApiModelProperty(value="地块Id", name="landId")
    private String landId;

    @ApiModelProperty(value="地块名称", name="landName")
    private String landName;

    @ApiModelProperty(value="地块编号", name="landName")
    private String landCode;

    @ApiModelProperty(value="总用地面积", name="landTotalMeasure")
    private String landTotalMeasure = "";

    @ApiModelProperty(value="可建设用地面积", name="landConstructableMeasure")
    private String landConstructableMeasure = "";

    @ApiModelProperty(value="计容建筑面积", name="meterBuildMeasure")
    private String meterBuildMeasure = "";

    @ApiModelProperty(value="建筑占地面积", name="buildingCoverArea")
    private String buildingCoverArea = "0";

    @ApiModelProperty(value="代征用地面积", name="takeLandUseArea")
    private String takeLandUseArea = "0";

    @ApiModelProperty(value="土地获取价款", name="landPrice")
    private String landPrice = "0";

    @ApiModelProperty(value="溢价比例", name="premiumProportion")
    private String premiumProportion = "";

}
