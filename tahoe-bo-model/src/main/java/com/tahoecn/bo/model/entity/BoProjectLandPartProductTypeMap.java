package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 项目分期地块业态关系表
 * </p>
 *
 * @author panglx
 * @since 2019-06-11
 */
public class BoProjectLandPartProductTypeMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 项目地块关系ID
     */
    private String projectLandPartId;

    /**
     * 业态ID
     */
    private String productTypeId;

    /**
     * 业态CODE
     */
    private String productTypeCode;

    /**
     * 业态名称
     */
    private String productTypeName;

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

    private String propertyRightAttr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getProjectLandPartId() {
        return projectLandPartId;
    }

    public void setProjectLandPartId(String projectLandPartId) {
        this.projectLandPartId = projectLandPartId;
    }
    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }
    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }
    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
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

    public String getPropertyRightAttr() {
        return propertyRightAttr;
    }

    public void setPropertyRightAttr(String propertyRightAttr) {
        this.propertyRightAttr = propertyRightAttr;
    }

    @Override
    public String toString() {
        return "BoProjectLandPartProductTypeMap{" +
                "id='" + id + '\'' +
                ", projectLandPartId='" + projectLandPartId + '\'' +
                ", productTypeId='" + productTypeId + '\'' +
                ", productTypeCode='" + productTypeCode + '\'' +
                ", productTypeName='" + productTypeName + '\'' +
                ", isDelete=" + isDelete +
                ", isDisable=" + isDisable +
                ", createTime=" + createTime +
                ", createrId='" + createrId + '\'' +
                ", createrName='" + createrName + '\'' +
                ", updateTime=" + updateTime +
                ", updaterId='" + updaterId + '\'' +
                ", updaterName='" + updaterName + '\'' +
                ", propertyRightAttr='" + propertyRightAttr + '\'' +
                '}';
    }
}
