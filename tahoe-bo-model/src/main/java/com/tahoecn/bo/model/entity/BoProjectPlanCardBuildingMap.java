package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 工规证楼栋关系表
 * </p>
 *
 * @author panglx
 * @since 2019-06-04
 */
public class BoProjectPlanCardBuildingMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 工规证ID
     */
    private String projectPlanCardId;

    /**
     * 楼栋ID
     */
    private String buildingId;

    /**
     * 楼栋同属ID
     */
    private String originBuildingId;

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
    public String getProjectPlanCardId() {
        return projectPlanCardId;
    }

    public void setProjectPlanCardId(String projectPlanCardId) {
        this.projectPlanCardId = projectPlanCardId;
    }
    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }
    public String getOriginBuildingId() {
        return originBuildingId;
    }

    public void setOriginBuildingId(String originBuildingId) {
        this.originBuildingId = originBuildingId;
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
        return "BoProjectPlanCardBuildingMap{" +
        "id=" + id +
        ", projectPlanCardId=" + projectPlanCardId +
        ", buildingId=" + buildingId +
        ", originBuildingId=" + originBuildingId +
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
