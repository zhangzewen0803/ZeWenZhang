package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目分期扩展信息表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class BoProjectQuotaExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 项目分期ID
     */
    private String projectId;


    /**
     * 阶段CODE
     */
    private String stageCode;

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本状态(0-编制中；1-审批中；2：审批通过；3：已驳回；4：已废弃；5：废弃已驳回）
     */
    private Integer versionStatus;

    /**
     * 审批ID
     */
    private String approveId;

    /**
     * 审批开始时间
     */
    private LocalDateTime approveStartTime;

    /**
     * 审批结束时间
     */
    private LocalDateTime approveEndTime;

    /**
     * 上一版本ID
     */
    private String preId;

    /**
     * 指标组CODE
     */
    private String quotaGroupCode;

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
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }
    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public Integer getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(Integer versionStatus) {
        this.versionStatus = versionStatus;
    }
    public String getApproveId() {
        return approveId;
    }

    public void setApproveId(String approveId) {
        this.approveId = approveId;
    }
    public LocalDateTime getApproveStartTime() {
        return approveStartTime;
    }

    public void setApproveStartTime(LocalDateTime approveStartTime) {
        this.approveStartTime = approveStartTime;
    }
    public LocalDateTime getApproveEndTime() {
        return approveEndTime;
    }

    public void setApproveEndTime(LocalDateTime approveEndTime) {
        this.approveEndTime = approveEndTime;
    }
    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }
    public String getQuotaGroupCode() {
        return quotaGroupCode;
    }

    public void setQuotaGroupCode(String quotaGroupCode) {
        this.quotaGroupCode = quotaGroupCode;
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
        return "BoProjectQuotaExtend{" +
        "id=" + id +
        ", projectId=" + projectId +
        ", stageCode=" + stageCode +
        ", stageName=" + stageName +
        ", version=" + version +
        ", versionStatus=" + versionStatus +
        ", approveId=" + approveId +
        ", approveStartTime=" + approveStartTime +
        ", approveEndTime=" + approveEndTime +
        ", preId=" + preId +
        ", quotaGroupCode=" + quotaGroupCode +
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
