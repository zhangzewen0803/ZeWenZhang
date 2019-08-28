package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 业态表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class BoProductType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 业态CODE
     */
    private String code;

    /**
     * 业态名称
     */
    private String name;

    /**
     * ID全路径
     */
    private String idTree;

    /**
     * 业态CODE全路径，/分隔
     */
    private String codeTree;

    /**
     * 业态名称全路径，/分隔
     */
    private String nameTree;

    /**
     * 父ID
     */
    private String parentId;

    /**
     * 排序号
     */
    private Integer sortNo;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdTree() {
        return idTree;
    }

    public void setIdTree(String idTree) {
        this.idTree = idTree;
    }

    public String getCodeTree() {
        return codeTree;
    }

    public void setCodeTree(String codeTree) {
        this.codeTree = codeTree;
    }

    public String getNameTree() {
        return nameTree;
    }

    public void setNameTree(String nameTree) {
        this.nameTree = nameTree;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    @Override
    public String toString() {
        return "BoProductType{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", idTree='" + idTree + '\'' +
                ", codeTree='" + codeTree + '\'' +
                ", nameTree='" + nameTree + '\'' +
                ", parentId='" + parentId + '\'' +
                ", sortNo=" + sortNo +
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
