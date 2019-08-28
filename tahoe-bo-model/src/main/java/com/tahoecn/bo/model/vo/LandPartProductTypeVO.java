package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 地块业态
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("地块业态")
public class LandPartProductTypeVO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @NotBlank(message = "landPartId不能为空")
    @ApiModelProperty(value = "地块ID")
    private String landPartId;

    @NotBlank(message = "landPartName不能为空")
    @ApiModelProperty(value = "地块名称")
    private String landPartName;

    @NotBlank(message = "productTypeCode不能为空")
    @ApiModelProperty(value = "业态CODE")
    private String productTypeCode;

    @NotBlank(message = "productTypeName不能为空")
    @ApiModelProperty(value = "业态名称")
    private String productTypeName;

    @Valid
    @NotNull(message = "quotaList不能为空")
    @ApiModelProperty(value = "指标列表")
    private List<QuotaBodyVO> quotaList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLandPartId() {
        return landPartId;
    }

    public void setLandPartId(String landPartId) {
        this.landPartId = landPartId;
    }

    public String getLandPartName() {
        return landPartName;
    }

    public void setLandPartName(String landPartName) {
        this.landPartName = landPartName;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public List<QuotaBodyVO> getQuotaList() {
        return quotaList;
    }

    public void setQuotaList(List<QuotaBodyVO> quotaList) {
        this.quotaList = quotaList;
    }

    @Override
    public String toString() {
        return "LandPartProductType{" +
                "id='" + id + '\'' +
                ", landPartId='" + landPartId + '\'' +
                ", landPartName='" + landPartName + '\'' +
                ", productTypeCode='" + productTypeCode + '\'' +
                ", productTypeName='" + productTypeName + '\'' +
                ", quotaList=" + quotaList +
                '}';
    }
}
