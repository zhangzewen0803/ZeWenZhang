package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SubProjectCanUseLandVo {

    @ApiModelProperty(value="地块Id", name="landId")
    private String landId;

    @ApiModelProperty(value="地块名称", name="landName")
    private String landName;

    @ApiModelProperty(value="总用地面积", name="landTotalMeasure")
    private String landTotalMeasure;

    @ApiModelProperty(value="可建设用地面积", name="landConstructableMeasure")
    private String landConstructableMeasure;

    @ApiModelProperty(value="计容建筑面积", name="meterBuildMeasure")
    private String meterBuildMeasure;

    @ApiModelProperty(value="地块规划详情", name="subProjectQuoteTypeDetails")
    private List<SubProjectQuoteTypeVo> subProjectQuoteTypeDetails;

}
