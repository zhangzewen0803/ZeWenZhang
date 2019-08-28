package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 楼栋业态指标数据
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value="楼栋业态指标数据", description="楼栋业态指标数据")
public class GetBuildingProductTypeQuotaListVO implements Serializable{

    @ApiModelProperty(value="指标头信息/指标定义信息，数组")
    private List<QuotaHeadVO> head;

    @ApiModelProperty(value = "楼栋信息列表")
    private List<BuildingVO> buildingList;

    @ApiModelProperty(value="楼栋业态信息，数组")
    private List<BuildingProductTypeVO> buildingProductTypeList;

    @Override
    public String toString() {
        return "GetBuildingProductTypeQuotaListVO{" +
                "head=" + head +
                ", buildingList=" + buildingList +
                ", buildingProductTypeList=" + buildingProductTypeList +
                '}';
    }

    public List<QuotaHeadVO> getHead() {
        return head;
    }

    public void setHead(List<QuotaHeadVO> head) {
        this.head = head;
    }

    public List<BuildingProductTypeVO> getBuildingProductTypeList() {
        return buildingProductTypeList;
    }

    public void setBuildingProductTypeList(List<BuildingProductTypeVO> buildingProductTypeList) {
        this.buildingProductTypeList = buildingProductTypeList;
    }

    public List<BuildingVO> getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(List<BuildingVO> buildingList) {
        this.buildingList = buildingList;
    }
}

