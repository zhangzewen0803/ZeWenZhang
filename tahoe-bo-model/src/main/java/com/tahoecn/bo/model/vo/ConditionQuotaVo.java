package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="项目规划条件指标",description="项目规划条件指标")
public class ConditionQuotaVo {

    @ApiModelProperty(value="总用地面积", name="landTotalMeasure")
    private String landTotalMeasure;

    @ApiModelProperty(value="可建设用地面积", name="landConstructableMeasure")
    private String landConstructableMeasure;

    @ApiModelProperty(value="代征用地面积", name="landExpropriationMeasure")
    private String landExpropriationMeasure;

    @ApiModelProperty(value="计容建筑面积", name="landBuildingMeasure")
    private String landBuildingMeasure;

    @ApiModelProperty(value="土地获取价款", name="landAcquisitionPrice")
    private String landAcquisitionPrice;

    @ApiModelProperty(value="楼面均价", name="floorAveragePrice")
    private String floorAveragePrice;

}
