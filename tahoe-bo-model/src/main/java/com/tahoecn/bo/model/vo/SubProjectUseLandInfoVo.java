package com.tahoecn.bo.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="分期占用土地信息",description="分期占用土地信息")
public class SubProjectUseLandInfoVo {

    @ApiModelProperty(value="分期占用土地信息", name="subProjectUseLandList")
    private List<SubProjectUseLandDetailsInfoVo> subProjectUseLandList;

    @Data
    @ApiModel(value="分期占用土地详情信息",description="分期占用土地详情信息")
    public static class SubProjectUseLandDetailsInfoVo{

        @ApiModelProperty(value="地块名称", name="landName")
        private String landName;

        @ApiModelProperty(value="地块编号", name="landCode")
        private String landCode;

        @ApiModelProperty(value="全部开发(0:否 1:是)", name="isDev")
        private String isDev;

        @ApiModelProperty(value="总用地面积(㎡)", name="landTotalMeasure")
        private String landTotalMeasure;

        @ApiModelProperty(value="计容建筑面积(㎡)", name="landBuildingMeasure")
        private String landBuildingMeasure;

        @ApiModelProperty(value="相关分期", name="subProjectName")
        private String subProjectName;

    }
}
