package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新业态价格
 *
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value = "更新业态价格", description = "更新业态价格")
public class UpdateProductTypePriceReqBody {

    @ApiModelProperty(value = "价格版本ID", required = true)
    @NotBlank(message = "versionId不能为空")
    private String versionId;

    @Valid
    @ApiModelProperty(value = "价格指标信息", required = true)
    @NotNull(message = "priceList不能为空")
    private List<Price> priceList;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<Price> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<Price> priceList) {
        this.priceList = priceList;
    }

    @Override
    public String toString() {
        return "UpdateLandPartProductTypePriceReqBody{" +
                "versionId='" + versionId + '\'' +
                ", priceList=" + priceList +
                '}';
    }

    @ApiModel("价格信息")
    public static class Price {

        @ApiModelProperty("关联数据ID")
        @NotBlank(message = "refId不能为空")
        private String refId;

        @Valid
        @ApiModelProperty("价格指标信息")
        @NotNull(message = "quotaList不能为空")
        private List<QuotaBodyVO> quotaList;

        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public List<QuotaBodyVO> getQuotaList() {
            return quotaList;
        }

        public void setQuotaList(List<QuotaBodyVO> quotaList) {
            this.quotaList = quotaList;
        }

        @Override
        public String toString() {
            return "Price{" +
                    "refId='" + refId + '\'' +
                    ", quotaList=" + quotaList +
                    '}';
        }
    }
}
