package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 楼栋业态
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("楼栋业态")
public class BuildingProductTypeMiniVO implements Serializable {

    @NotBlank(message = "productTypeCode不能为空")
    @ApiModelProperty(value = "业态CODE")
    private String productTypeCode;

    @Valid
    @NotNull(message = "buildingProductType -> quotaList不能为空")
    @ApiModelProperty(value = "指标列表")
    private List<QuotaBodyVO> quotaList;

    public List<QuotaBodyVO> getQuotaList() {
        return quotaList;
    }

    public void setQuotaList(List<QuotaBodyVO> quotaList) {
        this.quotaList = quotaList;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    @Override
    public String toString() {
        return "BuildingProductTypeMiniVO{" +
                ", productTypeCode='" + productTypeCode + '\'' +
                ", quotaList=" + quotaList +
                '}';
    }
}
