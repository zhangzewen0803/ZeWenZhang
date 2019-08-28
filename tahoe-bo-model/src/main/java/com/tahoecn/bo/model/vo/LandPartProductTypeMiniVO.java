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
public class LandPartProductTypeMiniVO implements Serializable {

    @NotBlank(message = "主键ID不能为空")
    @ApiModelProperty(value = "主键ID")
    private String id;

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
                ", quotaList=" + quotaList +
                '}';
    }
}
