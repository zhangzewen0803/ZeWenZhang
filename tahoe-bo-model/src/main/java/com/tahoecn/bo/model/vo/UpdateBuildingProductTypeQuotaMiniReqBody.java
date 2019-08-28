package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新楼栋业态指标Mini版参数
 *
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel(value = "更新楼栋业态指标Mini版参数", description = "更新楼栋业态指标Mini版参数")
public class UpdateBuildingProductTypeQuotaMiniReqBody {

    @NotBlank(message = "versionId不能为空")
    @ApiModelProperty(value = "面积版本ID", required = true)
    private String versionId;

    @NotBlank(message = "base不能为空")
    @ApiModelProperty(value = "基准，可选项：BUILDING、PRODUCT_TYPE" +
            "\r\n以BUILDING为基准，支持更改单栋楼栋信息，多条楼栋业态信息，批量选择的楼栋与基准楼栋信息保持一致，批量选择楼栋的业态指标与基准楼栋的所有业态指标保持一致" +
            "\r\n以PRODUCT_TYPE为基准，支持更改单栋楼栋信息，仅支持更改一条楼栋业态指标，批量选择的楼栋与基准楼栋信息保持一致，批量选择楼栋的业态指标与基准楼栋的一条业态指标保持一致，其他不变")
    private String base;

    @ApiModelProperty(value = "需要复制信息的楼栋ID，多个以,分隔")
    private String buildingIds;

    @Valid
    @NotNull(message = "buildingQuotaList不能为空")
    @ApiModelProperty("指标项")
    private List<QuotaBodyVO> buildingQuotaList;

    @Valid
    @NotNull(message = "buildingProductTypeList不能为空")
    @ApiModelProperty(value = "楼栋业态信息，数组")
    private List<BuildingProductTypeMiniVO> buildingProductTypeList;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getBuildingIds() {
        return buildingIds;
    }

    public void setBuildingIds(String buildingIds) {
        this.buildingIds = buildingIds;
    }

    public List<QuotaBodyVO> getBuildingQuotaList() {
        return buildingQuotaList;
    }

    public void setBuildingQuotaList(List<QuotaBodyVO> buildingQuotaList) {
        this.buildingQuotaList = buildingQuotaList;
    }

    public List<BuildingProductTypeMiniVO> getBuildingProductTypeList() {
        return buildingProductTypeList;
    }

    public void setBuildingProductTypeList(List<BuildingProductTypeMiniVO> buildingProductTypeList) {
        this.buildingProductTypeList = buildingProductTypeList;
    }

    @Override
    public String toString() {
        return "UpdateBuildingProductTypeQuotaMiniReqBody{" +
                "versionId='" + versionId + '\'' +
                ", base='" + base + '\'' +
                ", buildingIds='" + buildingIds + '\'' +
                ", buildingQuotaList=" + buildingQuotaList +
                ", buildingProductTypeList=" + buildingProductTypeList +
                '}';
    }
}
