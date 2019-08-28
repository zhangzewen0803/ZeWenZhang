package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 指标头/指标定义
 *
 * @author panglx
 * @date 2019/5/25
 */
@ApiModel("指标头、指标定义")
public class QuotaHeadVO  implements Serializable{

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "指标名称")
    private String name;

    @ApiModelProperty(value = "指标CODE")
    private String code;

    @ApiModelProperty(value = "指标类型")
    private String quotaType;

    @ApiModelProperty(value = "值类型(TEXT、NUMBER、DATE、GROUP)")
    private String valueType;

    @ApiModelProperty(value = "数据格式，根据value_type对应不同的含义")
    private String dataFormat;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "是否汇总")
    private Integer isGather;

    @ApiModelProperty(value = "是否可编辑")
    private Integer isEdit;

    @ApiModelProperty(value = "公式")
    private String formula;

    @ApiModelProperty(value = "正则")
    private String regexps;

    @ApiModelProperty(value = "排序")
    private Integer sortNo;

    @ApiModelProperty(value = "父ID")
    private String parentId;

    @ApiModelProperty(value = "字段跨行")
    private Integer colspan;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Head{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", quotaType='" + quotaType + '\'' +
                ", valueType='" + valueType + '\'' +
                ", dataFormat='" + dataFormat + '\'' +
                ", unit='" + unit + '\'' +
                ", isGather=" + isGather +
                ", isEdit=" + isEdit +
                ", formula='" + formula + '\'' +
                ", regexps='" + regexps + '\'' +
                ", sortNo=" + sortNo +
                ", parentId='" + parentId + '\'' +
                ", colspan=" + colspan +
                '}';
    }
}
