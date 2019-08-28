package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新楼栋业态指标参数
 *
 * @author panglx
 * @date 2019/5/27
 */
@ApiModel(value = "更新楼栋业态指标参数", description = "更新楼栋业态指标参数")
public class UpdateBuildingProductTypeQuotaReqBody {

    @NotBlank(message = "versionId不能为空")
    @ApiModelProperty(value = "面积版本ID", required = true)
    private String versionId;

    @Valid
    @NotNull(message = "buildingList不能为空")
    @ApiModelProperty(value = "楼栋信息")
    private List<BuildingVO> buildingList;

    @Valid
    @NotNull(message = "buildingProductTypeList不能为空")
    @ApiModelProperty(value = "楼栋业态信息，数组")
    private List<BuildingProductTypeVO> buildingProductTypeList;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<BuildingProductTypeVO> getBuildingProductTypeList() {
        return buildingProductTypeList;
    }

    public void setBuildingProductTypeList(List<BuildingProductTypeVO> buildingProductTypeList) {
        this.buildingProductTypeList = buildingProductTypeList;
    }

    public List<BuildingVO> getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(List<BuildingVO> buildingList) {
        this.buildingList = buildingList;
    }
}
