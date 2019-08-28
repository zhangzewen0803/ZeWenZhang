package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 项目基础信息扩展表
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public class BoProjectExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 项目/分期ID
     */
    private String projectId;

    /**
     * 所在城市ID
     */
    private String cityId;

    /**
     * 所在城市名称
     */
    private String cityName;

    /**
     * 获取状态CODE
     */
    private String gainStatusCode;

    /**
     * 获取状态名称
     */
    private String gainStatusName;

    /**
     * 项目/分期名称
     */
    private String projectName;

    /**
     * 项目/分期案名
     */
    private String caseName;

    /**
     * 操盘方式CODE
     */
    private String tradeModeCode;

    /**
     * 操盘方式名称
     */
    private String tradeModeName;

    /**
     * 项目/分期负责人名称
     */
    private String principalName;

    /**
     * 项目/分期负责人ID
     */
    private String principalId;

    /**
     * 计税方式CODE
     */
    private String taxTypeCode;

    /**
     * 计税方式名称
     */
    private String taxTypeName;

    /**
     * 并表方式CODE
     */
    private String mergeTableTypeCode;

    /**
     * 并表方式名称
     */
    private String mergeTableTypeName;

    /**
     * 分期开发状态CODE
     */
    private String devStatusCode;

    /**
     * 分期开发状态名称
     */
    private String devStatusName;

    /**
     * 项目地址
     */
    private String projectAddress;

    /**
     * 项目/分期总图地址
     */
    private String overviewPicUrl;

    /**
     * 项目/分期地理位置坐标
     */
    private String addressPoint;

    /**
     * 分区划分ID
     */
    private String partId;

    /**
     * 分区划分名称
     */
    private String partName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 审批ID
     */
    private String approveId;

    /**
     * 版本状态(0-编制中；1-审批中；2：审批通过；3：已驳回；）
     */
    private Integer versionStatus;

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

    /**
     * 项目类型
     */
    private String projectType;

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
    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public String getGainStatusCode() {
        return gainStatusCode;
    }

    public void setGainStatusCode(String gainStatusCode) {
        this.gainStatusCode = gainStatusCode;
    }
    public String getGainStatusName() {
        return gainStatusName;
    }

    public void setGainStatusName(String gainStatusName) {
        this.gainStatusName = gainStatusName;
    }
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }
    public String getTradeModeCode() {
        return tradeModeCode;
    }

    public void setTradeModeCode(String tradeModeCode) {
        this.tradeModeCode = tradeModeCode;
    }
    public String getTradeModeName() {
        return tradeModeName;
    }

    public void setTradeModeName(String tradeModeName) {
        this.tradeModeName = tradeModeName;
    }
    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
    public String getTaxTypeCode() {
        return taxTypeCode;
    }

    public void setTaxTypeCode(String taxTypeCode) {
        this.taxTypeCode = taxTypeCode;
    }
    public String getTaxTypeName() {
        return taxTypeName;
    }

    public void setTaxTypeName(String taxTypeName) {
        this.taxTypeName = taxTypeName;
    }
    public String getMergeTableTypeCode() {
        return mergeTableTypeCode;
    }

    public void setMergeTableTypeCode(String mergeTableTypeCode) {
        this.mergeTableTypeCode = mergeTableTypeCode;
    }
    public String getMergeTableTypeName() {
        return mergeTableTypeName;
    }

    public void setMergeTableTypeName(String mergeTableTypeName) {
        this.mergeTableTypeName = mergeTableTypeName;
    }
    public String getDevStatusCode() {
        return devStatusCode;
    }

    public void setDevStatusCode(String devStatusCode) {
        this.devStatusCode = devStatusCode;
    }
    public String getDevStatusName() {
        return devStatusName;
    }

    public void setDevStatusName(String devStatusName) {
        this.devStatusName = devStatusName;
    }
    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }
    public String getOverviewPicUrl() {
        return overviewPicUrl;
    }

    public void setOverviewPicUrl(String overviewPicUrl) {
        this.overviewPicUrl = overviewPicUrl;
    }
    public String getAddressPoint() {
        return addressPoint;
    }

    public void setAddressPoint(String addressPoint) {
        this.addressPoint = addressPoint;
    }
    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }
    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getApproveId() {
        return approveId;
    }

    public void setApproveId(String approveId) {
        this.approveId = approveId;
    }
    public Integer getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(Integer versionStatus) {
        this.versionStatus = versionStatus;
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

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    @Override
    public String toString() {
        return "BoProjectExtend{" +
                "id='" + id + '\'' +
                ", projectId='" + projectId + '\'' +
                ", cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", gainStatusCode='" + gainStatusCode + '\'' +
                ", gainStatusName='" + gainStatusName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", caseName='" + caseName + '\'' +
                ", tradeModeCode='" + tradeModeCode + '\'' +
                ", tradeModeName='" + tradeModeName + '\'' +
                ", principalName='" + principalName + '\'' +
                ", principalId='" + principalId + '\'' +
                ", taxTypeCode='" + taxTypeCode + '\'' +
                ", taxTypeName='" + taxTypeName + '\'' +
                ", mergeTableTypeCode='" + mergeTableTypeCode + '\'' +
                ", mergeTableTypeName='" + mergeTableTypeName + '\'' +
                ", devStatusCode='" + devStatusCode + '\'' +
                ", devStatusName='" + devStatusName + '\'' +
                ", projectAddress='" + projectAddress + '\'' +
                ", overviewPicUrl='" + overviewPicUrl + '\'' +
                ", addressPoint='" + addressPoint + '\'' +
                ", partId='" + partId + '\'' +
                ", partName='" + partName + '\'' +
                ", version='" + version + '\'' +
                ", approveId='" + approveId + '\'' +
                ", versionStatus=" + versionStatus +
                ", approveStartTime=" + approveStartTime +
                ", approveEndTime=" + approveEndTime +
                ", preId='" + preId + '\'' +
                ", isDelete=" + isDelete +
                ", isDisable=" + isDisable +
                ", createTime=" + createTime +
                ", createrId='" + createrId + '\'' +
                ", createrName='" + createrName + '\'' +
                ", updateTime=" + updateTime +
                ", updaterId='" + updaterId + '\'' +
                ", updaterName='" + updaterName + '\'' +
                ", projectType='" + projectType + '\'' +
                '}';
    }
}
