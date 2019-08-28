package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 指标体
 *
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value = "指标体", description = "指标体")
public class QuotaBodyVO implements Serializable{

    @NotBlank(message = "quotaCode不能为空")
    @ApiModelProperty(value = "指标CODE")
    private String quotaCode;

    @ApiModelProperty(value = "指标值")
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

    @Override
    public String toString() {
        return "Body{" +
                "quotaCode='" + quotaCode + '\'' +
                ", quotaValue='" + quotaValue + '\'' +
                '}';
    }

}
