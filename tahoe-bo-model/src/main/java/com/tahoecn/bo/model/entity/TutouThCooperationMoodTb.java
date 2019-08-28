package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * TUTOU-合作方式表
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThCooperationMoodTb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 原表ID
     */
    private Integer sourceId;

    /**
     * 合作方式类型
     */
    private String cooperationMoodType;

    /**
     * 是否有效(0否 1是)
     */
    private Integer ifValid;

    /**
     * 备注
     */
    private String comment;

    /**
     * 是否删除 0否  1是
     */
    private Integer deleted;

    /**
     * 创建时间-自建
     */
    private LocalDateTime createTime;

    /**
     * 更新时间-自建
     */
    private LocalDateTime updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }
    public String getCooperationMoodType() {
        return cooperationMoodType;
    }

    public void setCooperationMoodType(String cooperationMoodType) {
        this.cooperationMoodType = cooperationMoodType;
    }
    public Integer getIfValid() {
        return ifValid;
    }

    public void setIfValid(Integer ifValid) {
        this.ifValid = ifValid;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TutouThCooperationMoodTb{" +
        "id=" + id +
        ", sourceId=" + sourceId +
        ", cooperationMoodType=" + cooperationMoodType +
        ", ifValid=" + ifValid +
        ", comment=" + comment +
        ", deleted=" + deleted +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
