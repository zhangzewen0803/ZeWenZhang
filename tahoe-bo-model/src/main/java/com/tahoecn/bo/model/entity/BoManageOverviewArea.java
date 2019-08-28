package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 经营概览面积统计表
 * </p>
 *
 * @author panglx
 * @since 2019-07-18
 */
public class BoManageOverviewArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 组织ID
     */
    private String orgId;

    /**
     * 组织类型
     */
    private String orgType;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 父组织ID
     */
    private String parentOrgId;

    /**
     * 父组织名称
     */
    private String parentOrgName;

    /**
     * 业态CODE
     */
    private String productTypeCode;

    /**
     * 全盘动态可售建筑面积 
     */
    private BigDecimal overallSaleArea;

    /**
     * 投资决策会可售建筑面积
     */
    private BigDecimal investDecisionSaleArea;

    /**
     * 经营决策会可售建筑面积
     */
    private BigDecimal manageDecisionSaleArea;

    /**
     * 未开发（土储）面积
     */
    private BigDecimal unDevelopArea;

    /**
     * 未开工面积
     */
    private BigDecimal unStartArea;

    /**
     * 已开工未达形象进度面积
     */
    private BigDecimal unReachImgGoalArea;

    /**
     * 已达形象进度未取证面积
     */
    private BigDecimal unGetCardArea;

    /**
     * 已取证未售（存货）面积
     */
    private BigDecimal unSaleArea;

    /**
     * 地上总实际建筑面积
     */
    private BigDecimal totalActualAreaAbove;

    /**
     * 地下总实际建筑面积
     */
    private BigDecimal totalActualAreaUnder;

    /**
     * 地上总建筑面积
     */
    private BigDecimal totalAreaAbove;

    /**
     * 地下总建筑面积
     */
    private BigDecimal totalAreaUnder;

    /**
     * 地上总可售建筑面积
     */
    private BigDecimal totalSaleAreaAbove;

    /**
     * 地下总可售建筑面积
     */
    private BigDecimal totalSaleAreaUnder;

    /**
     * 地上总可租建筑面积
     */
    private BigDecimal totalRentAreaAbove;

    /**
     * 地下总可租建筑面积
     */
    private BigDecimal totalRentAreaUnder;

    /**
     * 地上总赠送建筑面积
     */
    private BigDecimal totalGiveAreaAbove;

    /**
     * 地下总赠送建筑面积
     */
    private BigDecimal totalGiveAreaUnder;

    /**
     * 地上总非可租售建筑面积
     */
    private BigDecimal totalUnRentAreaAbove;

    /**
     * 地下总非可租售建筑面积
     */
    private BigDecimal totalUnRentAreaUnder;

    /**
     * 总用地面积
     */
    private BigDecimal totalUsedArea;

    /**
     * 建设用地面积
     */
    private BigDecimal buildingUsedArea;

    /**
     * 建设占地面积
     */
    private BigDecimal buildingCoverdArea;

    /**
     * 基底面积
     */
    private BigDecimal basalArea;

    /**
     * 地上计容建筑面积
     */
    private BigDecimal capacityAreaAbove;

    /**
     * 地下计容建筑面积
     */
    private BigDecimal capacityAreaUnder;

    /**
     * 人防面积
     */
    private BigDecimal civilDefenceArea;

    /**
     * 非人防面积
     */
    private BigDecimal unCivilDefenceArea;

    /**
     * 易地人防面积
     */
    private BigDecimal elesCivilDefenceArea;

    /**
     * 易地绿地面积
     */
    private BigDecimal elesGreenArea;

    /**
     * 地上总代征用地面积
     */
    private BigDecimal totalExpropriationAreaAbove;

    /**
     * 地下总代征用地面积
     */
    private BigDecimal totalExpropriationAreaUnder;

    /**
     * 批量精装修套内面积
     */
    private BigDecimal batchDecorationArea;

    /**
     * 典型户型精装修套内面积
     */
    private BigDecimal baseApartmentArea;

    /**
     * 公区精装修面积
     */
    private BigDecimal publicDecorationArea;

    /**
     * 精装修对应实际建筑面积
     */
    private BigDecimal decorationForActualArea;

    /**
     * 代征硬景面积
     */
    private BigDecimal substituteHardArea;

    /**
     * 区内硬景面积 
     */
    private BigDecimal intraHardArea;

    /**
     * 代征软景面积
     */
    private BigDecimal substituteSoftArea;

    /**
     * 区内软景面积 
     */
    private BigDecimal intraSoftArea;

    /**
     * 已售面积
     */
    private BigDecimal saledArea;

    /**
     * 统计版本ID
     */
    private String manageOverviewVersionId;

    /**
     * 截止日期/版本日期
     */
    private LocalDate versionDate;

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
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(String parentOrgId) {
        this.parentOrgId = parentOrgId;
    }
    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }
    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }
    public BigDecimal getOverallSaleArea() {
        return overallSaleArea;
    }

    public void setOverallSaleArea(BigDecimal overallSaleArea) {
        this.overallSaleArea = overallSaleArea;
    }
    public BigDecimal getInvestDecisionSaleArea() {
        return investDecisionSaleArea;
    }

    public void setInvestDecisionSaleArea(BigDecimal investDecisionSaleArea) {
        this.investDecisionSaleArea = investDecisionSaleArea;
    }
    public BigDecimal getManageDecisionSaleArea() {
        return manageDecisionSaleArea;
    }

    public void setManageDecisionSaleArea(BigDecimal manageDecisionSaleArea) {
        this.manageDecisionSaleArea = manageDecisionSaleArea;
    }
    public BigDecimal getUnDevelopArea() {
        return unDevelopArea;
    }

    public void setUnDevelopArea(BigDecimal unDevelopArea) {
        this.unDevelopArea = unDevelopArea;
    }
    public BigDecimal getUnStartArea() {
        return unStartArea;
    }

    public void setUnStartArea(BigDecimal unStartArea) {
        this.unStartArea = unStartArea;
    }
    public BigDecimal getUnReachImgGoalArea() {
        return unReachImgGoalArea;
    }

    public void setUnReachImgGoalArea(BigDecimal unReachImgGoalArea) {
        this.unReachImgGoalArea = unReachImgGoalArea;
    }
    public BigDecimal getUnGetCardArea() {
        return unGetCardArea;
    }

    public void setUnGetCardArea(BigDecimal unGetCardArea) {
        this.unGetCardArea = unGetCardArea;
    }
    public BigDecimal getUnSaleArea() {
        return unSaleArea;
    }

    public void setUnSaleArea(BigDecimal unSaleArea) {
        this.unSaleArea = unSaleArea;
    }
    public BigDecimal getTotalActualAreaAbove() {
        return totalActualAreaAbove;
    }

    public void setTotalActualAreaAbove(BigDecimal totalActualAreaAbove) {
        this.totalActualAreaAbove = totalActualAreaAbove;
    }
    public BigDecimal getTotalActualAreaUnder() {
        return totalActualAreaUnder;
    }

    public void setTotalActualAreaUnder(BigDecimal totalActualAreaUnder) {
        this.totalActualAreaUnder = totalActualAreaUnder;
    }
    public BigDecimal getTotalAreaAbove() {
        return totalAreaAbove;
    }

    public void setTotalAreaAbove(BigDecimal totalAreaAbove) {
        this.totalAreaAbove = totalAreaAbove;
    }
    public BigDecimal getTotalAreaUnder() {
        return totalAreaUnder;
    }

    public void setTotalAreaUnder(BigDecimal totalAreaUnder) {
        this.totalAreaUnder = totalAreaUnder;
    }
    public BigDecimal getTotalSaleAreaAbove() {
        return totalSaleAreaAbove;
    }

    public void setTotalSaleAreaAbove(BigDecimal totalSaleAreaAbove) {
        this.totalSaleAreaAbove = totalSaleAreaAbove;
    }
    public BigDecimal getTotalSaleAreaUnder() {
        return totalSaleAreaUnder;
    }

    public void setTotalSaleAreaUnder(BigDecimal totalSaleAreaUnder) {
        this.totalSaleAreaUnder = totalSaleAreaUnder;
    }
    public BigDecimal getTotalRentAreaAbove() {
        return totalRentAreaAbove;
    }

    public void setTotalRentAreaAbove(BigDecimal totalRentAreaAbove) {
        this.totalRentAreaAbove = totalRentAreaAbove;
    }
    public BigDecimal getTotalRentAreaUnder() {
        return totalRentAreaUnder;
    }

    public void setTotalRentAreaUnder(BigDecimal totalRentAreaUnder) {
        this.totalRentAreaUnder = totalRentAreaUnder;
    }
    public BigDecimal getTotalGiveAreaAbove() {
        return totalGiveAreaAbove;
    }

    public void setTotalGiveAreaAbove(BigDecimal totalGiveAreaAbove) {
        this.totalGiveAreaAbove = totalGiveAreaAbove;
    }
    public BigDecimal getTotalGiveAreaUnder() {
        return totalGiveAreaUnder;
    }

    public void setTotalGiveAreaUnder(BigDecimal totalGiveAreaUnder) {
        this.totalGiveAreaUnder = totalGiveAreaUnder;
    }
    public BigDecimal getTotalUnRentAreaAbove() {
        return totalUnRentAreaAbove;
    }

    public void setTotalUnRentAreaAbove(BigDecimal totalUnRentAreaAbove) {
        this.totalUnRentAreaAbove = totalUnRentAreaAbove;
    }
    public BigDecimal getTotalUnRentAreaUnder() {
        return totalUnRentAreaUnder;
    }

    public void setTotalUnRentAreaUnder(BigDecimal totalUnRentAreaUnder) {
        this.totalUnRentAreaUnder = totalUnRentAreaUnder;
    }
    public BigDecimal getTotalUsedArea() {
        return totalUsedArea;
    }

    public void setTotalUsedArea(BigDecimal totalUsedArea) {
        this.totalUsedArea = totalUsedArea;
    }
    public BigDecimal getBuildingUsedArea() {
        return buildingUsedArea;
    }

    public void setBuildingUsedArea(BigDecimal buildingUsedArea) {
        this.buildingUsedArea = buildingUsedArea;
    }
    public BigDecimal getBuildingCoverdArea() {
        return buildingCoverdArea;
    }

    public void setBuildingCoverdArea(BigDecimal buildingCoverdArea) {
        this.buildingCoverdArea = buildingCoverdArea;
    }
    public BigDecimal getBasalArea() {
        return basalArea;
    }

    public void setBasalArea(BigDecimal basalArea) {
        this.basalArea = basalArea;
    }
    public BigDecimal getCapacityAreaAbove() {
        return capacityAreaAbove;
    }

    public void setCapacityAreaAbove(BigDecimal capacityAreaAbove) {
        this.capacityAreaAbove = capacityAreaAbove;
    }
    public BigDecimal getCapacityAreaUnder() {
        return capacityAreaUnder;
    }

    public void setCapacityAreaUnder(BigDecimal capacityAreaUnder) {
        this.capacityAreaUnder = capacityAreaUnder;
    }
    public BigDecimal getCivilDefenceArea() {
        return civilDefenceArea;
    }

    public void setCivilDefenceArea(BigDecimal civilDefenceArea) {
        this.civilDefenceArea = civilDefenceArea;
    }
    public BigDecimal getUnCivilDefenceArea() {
        return unCivilDefenceArea;
    }

    public void setUnCivilDefenceArea(BigDecimal unCivilDefenceArea) {
        this.unCivilDefenceArea = unCivilDefenceArea;
    }
    public BigDecimal getElesCivilDefenceArea() {
        return elesCivilDefenceArea;
    }

    public void setElesCivilDefenceArea(BigDecimal elesCivilDefenceArea) {
        this.elesCivilDefenceArea = elesCivilDefenceArea;
    }
    public BigDecimal getElesGreenArea() {
        return elesGreenArea;
    }

    public void setElesGreenArea(BigDecimal elesGreenArea) {
        this.elesGreenArea = elesGreenArea;
    }
    public BigDecimal getTotalExpropriationAreaAbove() {
        return totalExpropriationAreaAbove;
    }

    public void setTotalExpropriationAreaAbove(BigDecimal totalExpropriationAreaAbove) {
        this.totalExpropriationAreaAbove = totalExpropriationAreaAbove;
    }
    public BigDecimal getTotalExpropriationAreaUnder() {
        return totalExpropriationAreaUnder;
    }

    public void setTotalExpropriationAreaUnder(BigDecimal totalExpropriationAreaUnder) {
        this.totalExpropriationAreaUnder = totalExpropriationAreaUnder;
    }
    public BigDecimal getBatchDecorationArea() {
        return batchDecorationArea;
    }

    public void setBatchDecorationArea(BigDecimal batchDecorationArea) {
        this.batchDecorationArea = batchDecorationArea;
    }
    public BigDecimal getBaseApartmentArea() {
        return baseApartmentArea;
    }

    public void setBaseApartmentArea(BigDecimal baseApartmentArea) {
        this.baseApartmentArea = baseApartmentArea;
    }
    public BigDecimal getPublicDecorationArea() {
        return publicDecorationArea;
    }

    public void setPublicDecorationArea(BigDecimal publicDecorationArea) {
        this.publicDecorationArea = publicDecorationArea;
    }
    public BigDecimal getDecorationForActualArea() {
        return decorationForActualArea;
    }

    public void setDecorationForActualArea(BigDecimal decorationForActualArea) {
        this.decorationForActualArea = decorationForActualArea;
    }
    public BigDecimal getSubstituteHardArea() {
        return substituteHardArea;
    }

    public void setSubstituteHardArea(BigDecimal substituteHardArea) {
        this.substituteHardArea = substituteHardArea;
    }
    public BigDecimal getIntraHardArea() {
        return intraHardArea;
    }

    public void setIntraHardArea(BigDecimal intraHardArea) {
        this.intraHardArea = intraHardArea;
    }
    public BigDecimal getSubstituteSoftArea() {
        return substituteSoftArea;
    }

    public void setSubstituteSoftArea(BigDecimal substituteSoftArea) {
        this.substituteSoftArea = substituteSoftArea;
    }
    public BigDecimal getIntraSoftArea() {
        return intraSoftArea;
    }

    public void setIntraSoftArea(BigDecimal intraSoftArea) {
        this.intraSoftArea = intraSoftArea;
    }
    public BigDecimal getSaledArea() {
        return saledArea;
    }

    public void setSaledArea(BigDecimal saledArea) {
        this.saledArea = saledArea;
    }
    public String getManageOverviewVersionId() {
        return manageOverviewVersionId;
    }

    public void setManageOverviewVersionId(String manageOverviewVersionId) {
        this.manageOverviewVersionId = manageOverviewVersionId;
    }
    public LocalDate getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(LocalDate versionDate) {
        this.versionDate = versionDate;
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
        return "BoManageOverviewArea{" +
        "id=" + id +
        ", orgId=" + orgId +
        ", orgType=" + orgType +
        ", orgName=" + orgName +
        ", parentOrgId=" + parentOrgId +
        ", parentOrgName=" + parentOrgName +
        ", productTypeCode=" + productTypeCode +
        ", overallSaleArea=" + overallSaleArea +
        ", investDecisionSaleArea=" + investDecisionSaleArea +
        ", manageDecisionSaleArea=" + manageDecisionSaleArea +
        ", unDevelopArea=" + unDevelopArea +
        ", unStartArea=" + unStartArea +
        ", unReachImgGoalArea=" + unReachImgGoalArea +
        ", unGetCardArea=" + unGetCardArea +
        ", unSaleArea=" + unSaleArea +
        ", totalActualAreaAbove=" + totalActualAreaAbove +
        ", totalActualAreaUnder=" + totalActualAreaUnder +
        ", totalAreaAbove=" + totalAreaAbove +
        ", totalAreaUnder=" + totalAreaUnder +
        ", totalSaleAreaAbove=" + totalSaleAreaAbove +
        ", totalSaleAreaUnder=" + totalSaleAreaUnder +
        ", totalRentAreaAbove=" + totalRentAreaAbove +
        ", totalRentAreaUnder=" + totalRentAreaUnder +
        ", totalGiveAreaAbove=" + totalGiveAreaAbove +
        ", totalGiveAreaUnder=" + totalGiveAreaUnder +
        ", totalUnRentAreaAbove=" + totalUnRentAreaAbove +
        ", totalUnRentAreaUnder=" + totalUnRentAreaUnder +
        ", totalUsedArea=" + totalUsedArea +
        ", buildingUsedArea=" + buildingUsedArea +
        ", buildingCoverdArea=" + buildingCoverdArea +
        ", basalArea=" + basalArea +
        ", capacityAreaAbove=" + capacityAreaAbove +
        ", capacityAreaUnder=" + capacityAreaUnder +
        ", civilDefenceArea=" + civilDefenceArea +
        ", unCivilDefenceArea=" + unCivilDefenceArea +
        ", elesCivilDefenceArea=" + elesCivilDefenceArea +
        ", elesGreenArea=" + elesGreenArea +
        ", totalExpropriationAreaAbove=" + totalExpropriationAreaAbove +
        ", totalExpropriationAreaUnder=" + totalExpropriationAreaUnder +
        ", batchDecorationArea=" + batchDecorationArea +
        ", baseApartmentArea=" + baseApartmentArea +
        ", publicDecorationArea=" + publicDecorationArea +
        ", decorationForActualArea=" + decorationForActualArea +
        ", substituteHardArea=" + substituteHardArea +
        ", intraHardArea=" + intraHardArea +
        ", substituteSoftArea=" + substituteSoftArea +
        ", intraSoftArea=" + intraSoftArea +
        ", saledArea=" + saledArea +
        ", manageOverviewVersionId=" + manageOverviewVersionId +
        ", versionDate=" + versionDate +
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
