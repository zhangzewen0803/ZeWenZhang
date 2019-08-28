package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目分期地块数据表/映射表
 * </p>
 *
 * @author panglx
 * @since 2019-06-17
 */
public class BoProjectLandPartMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 地块ID
     */
    private String landPartId;

    /**
     * 项目分期扩展信息ID
     */
    private String projectExtendId;

    /**
     * 地块名称
     */
    private String landPartName;

    /**
     * 是否全部开发（0-否；1-是）
     */
    private Integer isAllDev;

    /**
     * 总用地面积
     */
    private String totalUseLandArea;

    /**
     * 计容建筑面积(总计容面积)
     */
    private String capacityBuildingArea;

    /**
     * (可)建设用地面积
     */
    private String canUseLandArea;

    /**
     * 建筑占地面积
     */
    private String buildingLandCoverArea;

    /**
     * (总)代征用地面积
     */
    private String takeByLandUseArea;

    /**
     * 土地获取价款
     */
    private String landGetPrice;

    /**
     * 地上计容面积
     */
    private String aboveGroundCalcVolumeArea;

    /**
     * 地下计容面积
     */
    private String underGroundCalcVolumeArea;

    /**
     * 相关分期ID，多个逗号隔开
     */
    private String refProjectId;

    /**
     * 相关分期名称，多个逗号隔开
     */
    private String refProjectName;

    /**
     * 是否删除（0-否；1-是）
     */
    private Integer isDelete;

    /**
     * 是否禁用（0-否；1-是）
     */
    private Integer isDisable;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者ID
     */
    private String createrId;

    /**
     * 创建者名称
     */
    private String createrName;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新者ID
     */
    private String updaterId;

    /**
     * 更新者名称
     */
    private String updaterName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getLandPartId() {
        return landPartId;
    }

    public void setLandPartId(String landPartId) {
        this.landPartId = landPartId;
    }
    public String getProjectExtendId() {
        return projectExtendId;
    }

    public void setProjectExtendId(String projectExtendId) {
        this.projectExtendId = projectExtendId;
    }
    public String getLandPartName() {
        return landPartName;
    }

    public void setLandPartName(String landPartName) {
        this.landPartName = landPartName;
    }
    public Integer getIsAllDev() {
        return isAllDev;
    }

    public void setIsAllDev(Integer isAllDev) {
        this.isAllDev = isAllDev;
    }
    public String getTotalUseLandArea() {
        return totalUseLandArea;
    }

    public void setTotalUseLandArea(String totalUseLandArea) {
        this.totalUseLandArea = totalUseLandArea;
    }
    public String getCapacityBuildingArea() {
        return capacityBuildingArea;
    }

    public void setCapacityBuildingArea(String capacityBuildingArea) {
        this.capacityBuildingArea = capacityBuildingArea;
    }
    public String getCanUseLandArea() {
        return canUseLandArea;
    }

    public void setCanUseLandArea(String canUseLandArea) {
        this.canUseLandArea = canUseLandArea;
    }
    public String getBuildingLandCoverArea() {
        return buildingLandCoverArea;
    }

    public void setBuildingLandCoverArea(String buildingLandCoverArea) {
        this.buildingLandCoverArea = buildingLandCoverArea;
    }
    public String getTakeByLandUseArea() {
        return takeByLandUseArea;
    }

    public void setTakeByLandUseArea(String takeByLandUseArea) {
        this.takeByLandUseArea = takeByLandUseArea;
    }
    public String getLandGetPrice() {
        return landGetPrice;
    }

    public void setLandGetPrice(String landGetPrice) {
        this.landGetPrice = landGetPrice;
    }
    public String getAboveGroundCalcVolumeArea() {
        return aboveGroundCalcVolumeArea;
    }

    public void setAboveGroundCalcVolumeArea(String aboveGroundCalcVolumeArea) {
        this.aboveGroundCalcVolumeArea = aboveGroundCalcVolumeArea;
    }
    public String getUnderGroundCalcVolumeArea() {
        return underGroundCalcVolumeArea;
    }

    public void setUnderGroundCalcVolumeArea(String underGroundCalcVolumeArea) {
        this.underGroundCalcVolumeArea = underGroundCalcVolumeArea;
    }
    public String getRefProjectId() {
        return refProjectId;
    }

    public void setRefProjectId(String refProjectId) {
        this.refProjectId = refProjectId;
    }
    public String getRefProjectName() {
        return refProjectName;
    }

    public void setRefProjectName(String refProjectName) {
        this.refProjectName = refProjectName;
    }
    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
    public Integer getIsDisable() {
        return isDisable;
    }

    public void setIsDisable(Integer isDisable) {
        this.isDisable = isDisable;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }
    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public String getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(String updaterId) {
        this.updaterId = updaterId;
    }
    public String getUpdaterName() {
        return updaterName;
    }

    public void setUpdaterName(String updaterName) {
        this.updaterName = updaterName;
    }

    @Override
    public String toString() {
        return "BoProjectLandPartMap{" +
        "id=" + id +
        ", landPartId=" + landPartId +
        ", projectExtendId=" + projectExtendId +
        ", landPartName=" + landPartName +
        ", isAllDev=" + isAllDev +
        ", totalUseLandArea=" + totalUseLandArea +
        ", capacityBuildingArea=" + capacityBuildingArea +
        ", canUseLandArea=" + canUseLandArea +
        ", buildingLandCoverArea=" + buildingLandCoverArea +
        ", takeByLandUseArea=" + takeByLandUseArea +
        ", landGetPrice=" + landGetPrice +
        ", aboveGroundCalcVolumeArea=" + aboveGroundCalcVolumeArea +
        ", underGroundCalcVolumeArea=" + underGroundCalcVolumeArea +
        ", refProjectId=" + refProjectId +
        ", refProjectName=" + refProjectName +
        ", isDelete=" + isDelete +
        ", isDisable=" + isDisable +
        ", createTime=" + createTime +
        ", createrId=" + createrId +
        ", createrName=" + createrName +
        ", updateTime=" + updateTime +
        ", updaterId=" + updaterId +
        ", updaterName=" + updaterName +
        "}";
    }
}
