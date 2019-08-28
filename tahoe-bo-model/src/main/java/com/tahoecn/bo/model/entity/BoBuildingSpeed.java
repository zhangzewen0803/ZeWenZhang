package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 楼栋信息填报表
 * </p>
 *
 * @author panglx
 * @since 2019-06-28
 */
public class BoBuildingSpeed implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 楼栋ID
     */
    private String buildId;

    /**
     * 是否开工（0-否；1-是）
     */
    private Integer isStart;

    /**
     * 开工时间
     */
    private LocalDateTime startTime;

    /**
     * 预售形象进度要求
     */
    private String requirements;

    /**
     * 当前进度（0-未施工；1-施工中: 2-结构施工完毕）
     */
    private Integer currentStart;

    /**
     * 完工层数
     */
    private String completeNumber;

    /**
     * 是否达到预售形象进度（0-否；1-是）
     */
    private Integer isRequirements;

    /**
     * 是否取得预售证（0-否；1-是）
     */
    private Integer isSale;

    /**
     * 是否达到按揭放款进度（0-否；1-是）
     */
    private Integer isLoan;

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

    private String projectId;

    private LocalDateTime speedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }
    public Integer getIsStart() {
        return isStart;
    }

    public void setIsStart(Integer isStart) {
        this.isStart = isStart;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
    public Integer getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(Integer currentStart) {
        this.currentStart = currentStart;
    }
    public String getCompleteNumber() {
        return completeNumber;
    }

    public void setCompleteNumber(String completeNumber) {
        this.completeNumber = completeNumber;
    }
    public Integer getIsRequirements() {
        return isRequirements;
    }

    public void setIsRequirements(Integer isRequirements) {
        this.isRequirements = isRequirements;
    }
    public Integer getIsSale() {
        return isSale;
    }

    public void setIsSale(Integer isSale) {
        this.isSale = isSale;
    }
    public Integer getIsLoan() {
        return isLoan;
    }

    public void setIsLoan(Integer isLoan) {
        this.isLoan = isLoan;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getSpeedTime() {
        return speedTime;
    }

    public void setSpeedTime(LocalDateTime speedTime) {
        this.speedTime = speedTime;
    }

    @Override
    public String toString() {
        return "BoBuildingSpeed{" +
                "id='" + id + '\'' +
                ", buildId='" + buildId + '\'' +
                ", isStart=" + isStart +
                ", startTime=" + startTime +
                ", requirements='" + requirements + '\'' +
                ", currentStart=" + currentStart +
                ", completeNumber='" + completeNumber + '\'' +
                ", isRequirements=" + isRequirements +
                ", isSale=" + isSale +
                ", isLoan=" + isLoan +
                ", isDelete=" + isDelete +
                ", isDisable=" + isDisable +
                ", createTime=" + createTime +
                ", createrId='" + createrId + '\'' +
                ", createrName='" + createrName + '\'' +
                ", updateTime=" + updateTime +
                ", updaterId='" + updaterId + '\'' +
                ", updaterName='" + updaterName + '\'' +
                ", projectId='" + projectId + '\'' +
                ", speedTime=" + speedTime +
                '}';
    }
}
