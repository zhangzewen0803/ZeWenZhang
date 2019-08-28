package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 同步MDM-项目分期表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class MdmProjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "sid", type = IdType.INPUT)
    private String sid;

    /**
     * 项目名称
     */
    private String fullName;

    /**
     * 项目代码
     */
    private String fullCode;

    /**
     * 分期代码
     */
    private String code;

    /**
     * 分期名称
     */
    private String name;

    /**
     * 排序ID
     */
    private Integer sortId;

    /**
     * 当前类别，PROJET=项目，PROJECT_SUB=分期
     */
    private String levelType;

    /**
     * 父级ID,分期父级ID是项目，项目父级ID是城市公司虚拟目录。
            所在城市公司、所在区域可根据此ID在组织架构中找到。
     */
    private String parentSid;

    /**
     * *所属城市公司ID，非同步字段，需手动填写
     */
    private String cityCompanyId;

    /**
     * *所属城市公司名称，非同步字段，需手动填写
     */
    private String cityCompanyName;

    /**
     * *所属区域ID，非同步字段，需手动填写
     */
    private String regionId;

    /**
     * *所属区域名称，非同步字段，需手动填写
     */
    private String regionName;

    /**
     * 项目推广名
     */
    private String extentionName;

    /**
     * 母公司名称
     */
    private String parentCompany;

    /**
     * 法人公司ID
     */
    private String companyLegalId;

    /**
     * 法人公司名称
     */
    private String companyLegalName;

    /**
     * 审批申请时间
     */
    private LocalDateTime approveBeginDate;

    /**
     * 审批结束时间
     */
    private LocalDateTime approveEndDate;

    /**
     * 项目创建时间
     */
    private LocalDateTime createDate;

    /**
     * 项目更新时间
     */
    private LocalDateTime updateDate;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
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
    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }
    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }
    public String getParentSid() {
        return parentSid;
    }

    public void setParentSid(String parentSid) {
        this.parentSid = parentSid;
    }
    public String getCityCompanyId() {
        return cityCompanyId;
    }

    public void setCityCompanyId(String cityCompanyId) {
        this.cityCompanyId = cityCompanyId;
    }
    public String getCityCompanyName() {
        return cityCompanyName;
    }

    public void setCityCompanyName(String cityCompanyName) {
        this.cityCompanyName = cityCompanyName;
    }
    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    public String getExtentionName() {
        return extentionName;
    }

    public void setExtentionName(String extentionName) {
        this.extentionName = extentionName;
    }
    public String getParentCompany() {
        return parentCompany;
    }

    public void setParentCompany(String parentCompany) {
        this.parentCompany = parentCompany;
    }
    public String getCompanyLegalId() {
        return companyLegalId;
    }

    public void setCompanyLegalId(String companyLegalId) {
        this.companyLegalId = companyLegalId;
    }
    public String getCompanyLegalName() {
        return companyLegalName;
    }

    public void setCompanyLegalName(String companyLegalName) {
        this.companyLegalName = companyLegalName;
    }
    public LocalDateTime getApproveBeginDate() {
        return approveBeginDate;
    }

    public void setApproveBeginDate(LocalDateTime approveBeginDate) {
        this.approveBeginDate = approveBeginDate;
    }
    public LocalDateTime getApproveEndDate() {
        return approveEndDate;
    }

    public void setApproveEndDate(LocalDateTime approveEndDate) {
        this.approveEndDate = approveEndDate;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "MdmProjectInfo{" +
        "sid=" + sid +
        ", fullName=" + fullName +
        ", fullCode=" + fullCode +
        ", code=" + code +
        ", name=" + name +
        ", sortId=" + sortId +
        ", levelType=" + levelType +
        ", parentSid=" + parentSid +
        ", cityCompanyId=" + cityCompanyId +
        ", cityCompanyName=" + cityCompanyName +
        ", regionId=" + regionId +
        ", regionName=" + regionName +
        ", extentionName=" + extentionName +
        ", parentCompany=" + parentCompany +
        ", companyLegalId=" + companyLegalId +
        ", companyLegalName=" + companyLegalName +
        ", approveBeginDate=" + approveBeginDate +
        ", approveEndDate=" + approveEndDate +
        ", createDate=" + createDate +
        ", updateDate=" + updateDate +
        "}";
    }
}
