package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 楼栋信息
 *
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel("楼栋信息")
public class BuildingMiniVO implements Serializable {

    @NotBlank(message = "楼栋id不能为空")
    @ApiModelProperty("主键ID")
    private String id;

    @Valid
    @NotNull(message = "building -> quotaList不能为空")
    @ApiModelProperty("指标项")
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
        return "BuildingMiniVO{" +
                "id='" + id + '\'' +
                ", quotaList=" + quotaList +
                '}';
    }
}
