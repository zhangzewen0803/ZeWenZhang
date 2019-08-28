package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 楼栋信息表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class BoBuilding implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 项目分期指标扩展表ID
     */
    private String projectQuotaExtendId;

    /**
     * 所属地块ID
     */
    private String projectLandPartId;

    /**
     * 所属地块名称
     */
    private String projectLandPartName;

    /**
     * 楼栋名称
     */
    private String name;

    /**
     * 楼栋同属ID，不同阶段版本的楼栋，为同一个楼栋的ID标识
     */
    private String originId;

    /**
     * 上一版本ID
     */
    private String preId;

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

    public String getProjectLandPartName() {
        return projectLandPartName;
    }

    public void setProjectLandPartName(String projectLandPartName) {
        this.projectLandPartName = projectLandPartName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getProjectQuotaExtendId() {
        return projectQuotaExtendId;
    }

    public void setProjectQuotaExtendId(String projectQuotaExtendId) {
        this.projectQuotaExtendId = projectQuotaExtendId;
    }
    public String getProjectLandPartId() {
        return projectLandPartId;
    }

    public void setProjectLandPartId(String projectLandPartId) {
        this.projectLandPartId = projectLandPartId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }
    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
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
        return "BoBuilding{" +
                "id='" + id + '\'' +
                ", projectQuotaExtendId='" + projectQuotaExtendId + '\'' +
                ", projectLandPartId='" + projectLandPartId + '\'' +
                ", projectLandPartName='" + projectLandPartName + '\'' +
                ", name='" + name + '\'' +
                ", originId='" + originId + '\'' +
                ", preId='" + preId + '\'' +
                ", isDelete=" + isDelete +
                ", isDisable=" + isDisable +
                ", createTime=" + createTime +
                ", createrId='" + createrId + '\'' +
                ", createrName='" + createrName + '\'' +
                ", updateTime=" + updateTime +
                ", updaterId='" + updaterId + '\'' +
                ", updaterName='" + updaterName + '\'' +
                '}';
    }
}
