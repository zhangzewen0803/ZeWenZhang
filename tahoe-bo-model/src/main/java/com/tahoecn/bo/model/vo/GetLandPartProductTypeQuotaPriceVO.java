package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 地块业态面积价格
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value="地块业态面积价格", description="地块业态面积价格")
public class GetLandPartProductTypeQuotaPriceVO implements Serializable{

    @ApiModelProperty(value="指标头信息/指标定义信息")
    private List<QuotaHeadVO> head;

    @ApiModelProperty("地块业态面积价格列表")
    private List<LandPartProductTypePriceVO> landPartProductTypePriceList;

    public List<QuotaHeadVO> getHead() {
        return head;
    }

    public void setHead(List<QuotaHeadVO> head) {
        this.head = head;
    }

    public List<LandPartProductTypePriceVO> getLandPartProductTypePriceList() {
        return landPartProductTypePriceList;
    }

    public void setLandPartProductTypePriceList(List<LandPartProductTypePriceVO> landPartProductTypePriceList) {
        this.landPartProductTypePriceList = landPartProductTypePriceList;
    }

    @Override
    public String toString() {
        return "GetLandPartProductTypeQuotaPriceVO{" +
                "head=" + head +
                ", landPartProductTypePriceList=" + landPartProductTypePriceList +
                '}';
    }
}

