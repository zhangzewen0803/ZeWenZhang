package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * TUTOU-投委会状态码表
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThStateTb implements Serializable {

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
     * 状态ID
     */
    private Integer steateId;

    /**
     * 状态名称
     */
    private String steateName;

    /**
     * 类型
type=1，      1报意向
type=2or3，1待上会、2上会中、3通过、4驳回、5否决。
type=5,        1待签约、2已签约、3否决。
type=6,        1待确权、2确权中、3已确权、4否决。
type=7,        1未成交、2已成交、3暂存（土地线索）
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
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
    public Integer getSteateId() {
        return steateId;
    }

    public void setSteateId(Integer steateId) {
        this.steateId = steateId;
    }
    public String getSteateName() {
        return steateName;
    }

    public void setSteateName(String steateName) {
        this.steateName = steateName;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
        return "TutouThStateTb{" +
        "id=" + id +
        ", sourceId=" + sourceId +
        ", steateId=" + steateId +
        ", steateName=" + steateName +
        ", type=" + type +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
