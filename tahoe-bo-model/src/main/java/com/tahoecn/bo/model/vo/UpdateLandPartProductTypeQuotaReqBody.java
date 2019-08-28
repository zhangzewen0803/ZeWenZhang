package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新地块业态指标参数
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel(value = "更新地块业态指标参数", description = "更新地块业态指标参数")
public class UpdateLandPartProductTypeQuotaReqBody {

    @NotBlank(message = "versionId不能为空")
    @ApiModelProperty(value = "面积版本ID",required = true)
    private String versionId;

    @Valid
    @NotNull(message = "landPartProductTypeList不能为空")
    @ApiModelProperty(value="地块业态信息，数组",required = true)
    private List<LandPartProductTypeVO> landPartProductTypeList;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<LandPartProductTypeVO> getLandPartProductTypeList() {
        return landPartProductTypeList;
    }

    public void setLandPartProductTypeList(List<LandPartProductTypeVO> landPartProductTypeList) {
        this.landPartProductTypeList = landPartProductTypeList;
    }
}
