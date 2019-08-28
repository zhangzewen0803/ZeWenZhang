package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 指标分组关系表/映射表，用于为分组批量分配指标数据
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class BoQuotaGroupMap implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 指标ID
     */
    private String quotaId;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 指标CODE
     */
    private String code;

    /**
     * 指标类型
     */
    private String quotaType;

    /**
     * 值类型(TEXT、NUMBER、DATE、GROUP)
     */
    private String valueType;

    /**
     * 数据格式，根据value_type对应不同的含义
            value_type=TEXT时，data_format无意义
            value_type=NUMBER时，data_format=2表示小数点后保留2位
            value_type=DATE时，data_format=yyyy-MM-dd表示日期的格式
            value_type=GROUP时，data_format=DIC_CODE表示字典CODE值
     */
    private String dataFormat;

    /**
     * 单位
     */
    private String unit;

    /**
     * 分组CODE（STAGE-分期；）
     */
    private String groupCode;

    /**
     * 是否汇总
     */
    private Integer isGather;

    /**
     * 是否可编辑
     */
    private Integer isEdit;

    /**
     * 公式
     */
    private String formula;

    /**
     * 正则
     */
    private String regexps;

    /**
     * 排序
     */
    private Integer sortNo;

    /**
     * 父ID
     */
    private String parentId;

    /**
     * 字段跨行
     */
    private Integer colspan;

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
    public String getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(String quotaId) {
        this.quotaId = quotaId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }
    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
    public Integer getIsGather() {
        return isGather;
    }

    public void setIsGather(Integer isGather) {
        this.isGather = isGather;
    }
    public Integer getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Integer isEdit) {
        this.isEdit = isEdit;
    }
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
    public String getRegexps() {
        return regexps;
    }

    public void setRegexps(String regexps) {
        this.regexps = regexps;
    }
    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public Integer getColspan() {
        return colspan;
    }

    public void setColspan(Integer colspan) {
        this.colspan = colspan;
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
        return "BoQuotaGroupMap{" +
        "id=" + id +
        ", quotaId=" + quotaId +
        ", name=" + name +
        ", code=" + code +
        ", quotaType=" + quotaType +
        ", valueType=" + valueType +
        ", dataFormat=" + dataFormat +
        ", unit=" + unit +
        ", groupCode=" + groupCode +
        ", isGather=" + isGather +
        ", isEdit=" + isEdit +
        ", formula=" + formula +
        ", regexps=" + regexps +
        ", sortNo=" + sortNo +
        ", parentId=" + parentId +
        ", colspan=" + colspan +
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
