package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新面积规划指标参数对象
 *
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel(value = "更新面积规划指标参数对象", description = "更新面积规划指标参数对象")
public class UpdateAreaQuotaReqBody {

    @NotBlank(message = "versionId不能为空")
    @ApiModelProperty(value = "面积版本ID", required = true)
    private String versionId;

    @Valid
    @NotNull(message = "data不能为空")
    @ApiModelProperty(value = "指标数据项，数组", required = true)
    private List<QuotaBodyVO> data;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<QuotaBodyVO> getData() {
        return data;
    }

    public void setData(List<QuotaBodyVO> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UpdateAreaQuotaReqBody{" +
                "versionId='" + versionId + '\'' +
                ", data=" + data +
                '}';
    }
}
