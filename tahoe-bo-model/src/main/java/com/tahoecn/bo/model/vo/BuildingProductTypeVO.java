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
public class BuildingProductTypeVO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "楼栋ID")
    private String buildingId;

    @ApiModelProperty(value = "楼栋名称")
    @NotBlank(message = "buildingProductType -> buildingName不能为空")
    private String buildingName;

    @ApiModelProperty(value = "业态CODE")
    @NotBlank(message = "buildingProductType -> productTypeCode不能为空")
    private String productTypeCode;

    @ApiModelProperty(value = "业态名称")
    @NotBlank(message = "buildingProductType -> productTypeName不能为空")
    private String productTypeName;

    @Valid
    @NotNull(message = "buildingProductType -> quotaList不能为空")
    @ApiModelProperty(value = "指标列表")
    private List<QuotaBodyVO> quotaList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
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
                ", buildingId='" + buildingId + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", productTypeCode='" + productTypeCode + '\'' +
                ", productTypeName='" + productTypeName + '\'' +
                ", quotaList=" + quotaList +
                '}';
    }
}
