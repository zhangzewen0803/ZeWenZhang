package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 楼栋业态面积价格
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value="楼栋业态面积价格", description="楼栋业态面积价格")
public class GetBuildingProductTypeQuotaPriceVO implements Serializable{

    @ApiModelProperty(value="指标头信息/指标定义信息")
    private List<QuotaHeadVO> head;

    @ApiModelProperty("楼栋业态面积价格列表")
    private List<BuildingProductTypePriceVO> buildingProductTypePriceList;

    public List<QuotaHeadVO> getHead() {
        return head;
    }

    public void setHead(List<QuotaHeadVO> head) {
        this.head = head;
    }

    public List<BuildingProductTypePriceVO> getBuildingProductTypePriceList() {
        return buildingProductTypePriceList;
    }

    public void setBuildingProductTypePriceList(List<BuildingProductTypePriceVO> buildingProductTypePriceList) {
        this.buildingProductTypePriceList = buildingProductTypePriceList;
    }

    @Override
    public String toString() {
        return "GetBuildingProductTypeQuotaPriceVO{" +
                "head=" + head +
                ", buildingProductTypePriceList=" + buildingProductTypePriceList +
                '}';
    }
}

