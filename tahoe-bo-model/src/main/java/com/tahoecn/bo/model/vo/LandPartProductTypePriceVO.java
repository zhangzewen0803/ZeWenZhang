package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 地块业态价格
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("地块业态价格")
public class LandPartProductTypePriceVO extends LandPartProductTypeVO {

    @ApiModelProperty("地块业态价格指标列表")
    private List<QuotaBodyVO> priceQuotaList;

    public List<QuotaBodyVO> getPriceQuotaList() {
        return priceQuotaList;
    }

    public void setPriceQuotaList(List<QuotaBodyVO> priceQuotaList) {
        this.priceQuotaList = priceQuotaList;
    }

    @Override
    public String toString() {
        return "LandPartProductTypePriceVO{" +
                "priceQuotaList=" + priceQuotaList +
                '}';
    }
}
