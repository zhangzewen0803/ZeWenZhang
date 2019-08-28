package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 地块业态指标数据
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value="地块业态指标数据", description="地块业态指标数据")
public class GetLandPartProductTypeQuotaListVO implements Serializable{

    @ApiModelProperty(value="指标头信息/指标定义信息，数组")
    private List<QuotaHeadVO> head;

    @ApiModelProperty(value="地块业态信息，数组")
    private List<LandPartProductTypeVO> landPartProductTypeList;

    @Override
    public String toString() {
        return "GetLandPartProductTypeQuotaListVO{" +
                "head=" + head +
                ", landPartProductTypeList=" + landPartProductTypeList +
                '}';
    }

    public List<QuotaHeadVO> getHead() {
        return head;
    }

    public void setHead(List<QuotaHeadVO> head) {
        this.head = head;
    }

    public List<LandPartProductTypeVO> getLandPartProductTypeList() {
        return landPartProductTypeList;
    }

    public void setLandPartProductTypeList(List<LandPartProductTypeVO> landPartProductTypeList) {
        this.landPartProductTypeList = landPartProductTypeList;
    }
}

