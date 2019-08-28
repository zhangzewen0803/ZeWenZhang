package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value = "参数", description = "参数")
public class UpdateLandPartQuotaReqBody {

    @ApiModelProperty("地块业态关联信息ID")
    private String landPartProductTypeId;

    @ApiModelProperty("业态CODE")
    private String productTypeCode;

    @ApiModelProperty("业态ID")
    private String productTypeId;

    @ApiModelProperty("业态名称")
    private String productTypeName;

    @ApiModelProperty("分期关联地块ID")
    private String projectSubLandPartId;

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

    public String getProjectSubLandPartId() {
        return projectSubLandPartId;
    }

    public void setProjectSubLandPartId(String projectSubLandPartId) {
        this.projectSubLandPartId = projectSubLandPartId;
    }

    public Quota[] getQuotas() {
        return quotas;
    }

    public void setQuotas(Quota[] quotas) {
        this.quotas = quotas;
    }

    public String getLandPartProductTypeId() {
        return landPartProductTypeId;
    }

    public void setLandPartProductTypeId(String landPartProductTypeId) {
        this.landPartProductTypeId = landPartProductTypeId;
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
