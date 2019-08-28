package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value = "参数", description = "参数")
public class UpdateBuildingQuotaReqBody {

    @ApiModelProperty("楼栋业态关联信息ID")
    private String buildingProductTypeId;

    @ApiModelProperty("业态CODE")
    private String productTypeCode;

    @ApiModelProperty("业态ID")
    private String productTypeId;

    @ApiModelProperty("业态名称")
    private String productTypeName;

    @ApiModelProperty("分期关联楼栋ID")
    private String projectSubBuildingId;

    @ApiModelProperty("关联的指标数据")
    private Quota[] quotas;

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }


    public Quota[] getQuotas() {
        return quotas;
    }

    public void setQuotas(Quota[] quotas) {
        this.quotas = quotas;
    }

    public String getBuildingProductTypeId() {
        return buildingProductTypeId;
    }

    public void setBuildingProductTypeId(String buildingProductTypeId) {
        this.buildingProductTypeId = buildingProductTypeId;
    }

    public String getProjectSubBuildingId() {
        return projectSubBuildingId;
    }

    public void setProjectSubBuildingId(String projectSubBuildingId) {
        this.projectSubBuildingId = projectSubBuildingId;
    }

    static class Quota{

        private String landPartProductTypeQuotaId;

        private String quotaCode;

        private String quotaValue;

        public String getQuotaCode() {
            return quotaCode;
        }

        public void setQuotaCode(String quotaCode) {
            this.quotaCode = quotaCode;
        }

        public String getQuotaValue() {
            return quotaValue;
        }

        public void setQuotaValue(String quotaValue) {
            this.quotaValue = quotaValue;
        }

        public String getLandPartProductTypeQuotaId() {
            return landPartProductTypeQuotaId;
        }

        public void setLandPartProductTypeQuotaId(String landPartProductTypeQuotaId) {
            this.landPartProductTypeQuotaId = landPartProductTypeQuotaId;
        }
    }

}
