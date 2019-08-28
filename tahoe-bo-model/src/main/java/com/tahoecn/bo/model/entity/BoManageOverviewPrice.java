package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 经营概览货值统计表
 * </p>
 *
 * @author panglx
 * @since 2019-08-12
 */
public class BoManageOverviewPrice implements Serializable {

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
     * 组织名称
     */
    private String orgName;

    /**
     * 组织类型
     */
    private String orgType;

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
     * 全盘动态货值
     */
    private BigDecimal overallProductPrice;

    /**
     * 投资决策会货值
     */
    private BigDecimal investDecisionProductPrice;

    /**
     * 经营决策会货值
     */
    private BigDecimal manageDecisionProductPrice;

    /**
     * 未开发（土储）货值
     */
    private BigDecimal unDevelopProductPrice;

    /**
     * 未开工货值
     */
    private BigDecimal unStartProductPrice;

    /**
     * 已开工未达形象进度货值
     */
    private BigDecimal unReachImgGoalProductPrice;

    /**
     * 已达形象进度未取证货值
     */
    private BigDecimal unGetCardProductPrice;

    /**
     * 已取证未售（存货）货值
     */
    private BigDecimal unSaleProductPrice;

    /**
     * 已售货值
     */
    private BigDecimal saledProductPrice;

    /**
     * 已售未取证货值
     */
    private BigDecimal saledUnGetCardProductPrice;

    /**
     * 已售已取证
     */
    private BigDecimal saledGetCardProductPrice;

    /**
     * 三个月内存货货值
     */
    private BigDecimal ageFirstProductPrice;

    /**
     * 3-6个月内存货货值
     */
    private BigDecimal ageSencondProductPrice;

    /**
     * 6-12个月内存货货值
     */
    private BigDecimal ageThirdProductPrice;

    /**
     * 12-24个月内存货货值
     */
    private BigDecimal ageFourthProductPrice;

    /**
     * 24个月以上
     */
    private BigDecimal ageFifthProductPrice;

    /**
     * 未开发均价
     */
    private BigDecimal unDevelopAvgPrice;

    /**
     * 未售均价
     */
    private BigDecimal unSaleAvgPrice;

    /**
     * 已售均价
     */
    private BigDecimal saledAvgPrice;

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
     * 已售面积
     */
    private BigDecimal saledArea;

    /**
     * 已售未取证面积
     */
    private BigDecimal saledUnGetCardArea;

    /**
     * 已售已取证面积
     */
    private BigDecimal saledGetCardArea;

    /**
     * 未开工货值-认购口径
     */
    private BigDecimal offerUnStartProductPrice;

    /**
     * 已开工未达形象进度货值-认购口径
     */
    private BigDecimal offerUnReachImgGoalProductPrice;

    /**
     * 已达形象进度未取证货值-认购口径
     */
    private BigDecimal offerUnGetCardProductPrice;

    /**
     * 已取证未售货值-认购口径
     */
    private BigDecimal offerUnSaleProductPrice;

    /**
     * 已售货值-认购口径
     */
    private BigDecimal offerSaledProductPrice;

    /**
     * 已售未取证货值-认购口径
     */
    private BigDecimal offerSaledUnGetCardProductPrice;

    /**
     * 已售已取证货值-认购口径
     */
    private BigDecimal offerSaledGetCardProductPrice;

    /**
     * 三个月内存货货值-认购口径
     */
    private BigDecimal offerAgeFirstProductPrice;

    /**
     * 3-6个月内存货货值-认购口径
     */
    private BigDecimal offerAgeSencondProductPrice;

    /**
     * 6-12个月内存货货值-认购口径
     */
    private BigDecimal offerAgeThirdProductPrice;

    /**
     * 12-24个月内存货货值-认购口径
     */
    private BigDecimal offerAgeFourthProductPrice;

    /**
     * 24个月以上-认购口径
     */
    private BigDecimal offerAgeFifthProductPrice;

    /**
     * 未开发均价-认购口径
     */
    private BigDecimal offerUnDevelopAvgPrice;

    /**
     * 未售均价-认购口径
     */
    private BigDecimal offerUnSaleAvgPrice;

    /**
     * 已售均价-认购口径
     */
    private BigDecimal offerSaledAvgPrice;

    /**
     * 未开工面积-认购口径
     */
    private BigDecimal offerUnStartArea;

    /**
     * 已开工未达形象进度面积-认购口径
     */
    private BigDecimal offerUnReachImgGoalArea;

    /**
     * 已达形象进度未取证面积-认购口径
     */
    private BigDecimal offerUnGetCardArea;

    /**
     * 已取证未售（存货）面积-认购口径
     */
    private BigDecimal offerUnSaleArea;

    /**
     * 已售面积-认购口径
     */
    private BigDecimal offerSaledArea;

    /**
     * 已售未取证面积-认购口径
     */
    private BigDecimal offerSaledUnGetCardArea;

    /**
     * 已售已取证面积-认购口径
     */
    private BigDecimal offerSaledGetCardArea;

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
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
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
    public BigDecimal getOverallProductPrice() {
        return overallProductPrice;
    }

    public void setOverallProductPrice(BigDecimal overallProductPrice) {
        this.overallProductPrice = overallProductPrice;
    }
    public BigDecimal getInvestDecisionProductPrice() {
        return investDecisionProductPrice;
    }

    public void setInvestDecisionProductPrice(BigDecimal investDecisionProductPrice) {
        this.investDecisionProductPrice = investDecisionProductPrice;
    }
    public BigDecimal getManageDecisionProductPrice() {
        return manageDecisionProductPrice;
    }

    public void setManageDecisionProductPrice(BigDecimal manageDecisionProductPrice) {
        this.manageDecisionProductPrice = manageDecisionProductPrice;
    }
    public BigDecimal getUnDevelopProductPrice() {
        return unDevelopProductPrice;
    }

    public void setUnDevelopProductPrice(BigDecimal unDevelopProductPrice) {
        this.unDevelopProductPrice = unDevelopProductPrice;
    }
    public BigDecimal getUnStartProductPrice() {
        return unStartProductPrice;
    }

    public void setUnStartProductPrice(BigDecimal unStartProductPrice) {
        this.unStartProductPrice = unStartProductPrice;
    }
    public BigDecimal getUnReachImgGoalProductPrice() {
        return unReachImgGoalProductPrice;
    }

    public void setUnReachImgGoalProductPrice(BigDecimal unReachImgGoalProductPrice) {
        this.unReachImgGoalProductPrice = unReachImgGoalProductPrice;
    }
    public BigDecimal getUnGetCardProductPrice() {
        return unGetCardProductPrice;
    }

    public void setUnGetCardProductPrice(BigDecimal unGetCardProductPrice) {
        this.unGetCardProductPrice = unGetCardProductPrice;
    }
    public BigDecimal getUnSaleProductPrice() {
        return unSaleProductPrice;
    }

    public void setUnSaleProductPrice(BigDecimal unSaleProductPrice) {
        this.unSaleProductPrice = unSaleProductPrice;
    }
    public BigDecimal getSaledProductPrice() {
        return saledProductPrice;
    }

    public void setSaledProductPrice(BigDecimal saledProductPrice) {
        this.saledProductPrice = saledProductPrice;
    }
    public BigDecimal getSaledUnGetCardProductPrice() {
        return saledUnGetCardProductPrice;
    }

    public void setSaledUnGetCardProductPrice(BigDecimal saledUnGetCardProductPrice) {
        this.saledUnGetCardProductPrice = saledUnGetCardProductPrice;
    }
    public BigDecimal getSaledGetCardProductPrice() {
        return saledGetCardProductPrice;
    }

    public void setSaledGetCardProductPrice(BigDecimal saledGetCardProductPrice) {
        this.saledGetCardProductPrice = saledGetCardProductPrice;
    }
    public BigDecimal getAgeFirstProductPrice() {
        return ageFirstProductPrice;
    }

    public void setAgeFirstProductPrice(BigDecimal ageFirstProductPrice) {
        this.ageFirstProductPrice = ageFirstProductPrice;
    }
    public BigDecimal getAgeSencondProductPrice() {
        return ageSencondProductPrice;
    }

    public void setAgeSencondProductPrice(BigDecimal ageSencondProductPrice) {
        this.ageSencondProductPrice = ageSencondProductPrice;
    }
    public BigDecimal getAgeThirdProductPrice() {
        return ageThirdProductPrice;
    }

    public void setAgeThirdProductPrice(BigDecimal ageThirdProductPrice) {
        this.ageThirdProductPrice = ageThirdProductPrice;
    }
    public BigDecimal getAgeFourthProductPrice() {
        return ageFourthProductPrice;
    }

    public void setAgeFourthProductPrice(BigDecimal ageFourthProductPrice) {
        this.ageFourthProductPrice = ageFourthProductPrice;
    }
    public BigDecimal getAgeFifthProductPrice() {
        return ageFifthProductPrice;
    }

    public void setAgeFifthProductPrice(BigDecimal ageFifthProductPrice) {
        this.ageFifthProductPrice = ageFifthProductPrice;
    }
    public BigDecimal getUnDevelopAvgPrice() {
        return unDevelopAvgPrice;
    }

    public void setUnDevelopAvgPrice(BigDecimal unDevelopAvgPrice) {
        this.unDevelopAvgPrice = unDevelopAvgPrice;
    }
    public BigDecimal getUnSaleAvgPrice() {
        return unSaleAvgPrice;
    }

    public void setUnSaleAvgPrice(BigDecimal unSaleAvgPrice) {
        this.unSaleAvgPrice = unSaleAvgPrice;
    }
    public BigDecimal getSaledAvgPrice() {
        return saledAvgPrice;
    }

    public void setSaledAvgPrice(BigDecimal saledAvgPrice) {
        this.saledAvgPrice = saledAvgPrice;
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
    public BigDecimal getSaledArea() {
        return saledArea;
    }

    public void setSaledArea(BigDecimal saledArea) {
        this.saledArea = saledArea;
    }
    public BigDecimal getSaledUnGetCardArea() {
        return saledUnGetCardArea;
    }

    public void setSaledUnGetCardArea(BigDecimal saledUnGetCardArea) {
        this.saledUnGetCardArea = saledUnGetCardArea;
    }
    public BigDecimal getSaledGetCardArea() {
        return saledGetCardArea;
    }

    public void setSaledGetCardArea(BigDecimal saledGetCardArea) {
        this.saledGetCardArea = saledGetCardArea;
    }
    public BigDecimal getOfferUnStartProductPrice() {
        return offerUnStartProductPrice;
    }

    public void setOfferUnStartProductPrice(BigDecimal offerUnStartProductPrice) {
        this.offerUnStartProductPrice = offerUnStartProductPrice;
    }
    public BigDecimal getOfferUnReachImgGoalProductPrice() {
        return offerUnReachImgGoalProductPrice;
    }

    public void setOfferUnReachImgGoalProductPrice(BigDecimal offerUnReachImgGoalProductPrice) {
        this.offerUnReachImgGoalProductPrice = offerUnReachImgGoalProductPrice;
    }
    public BigDecimal getOfferUnGetCardProductPrice() {
        return offerUnGetCardProductPrice;
    }

    public void setOfferUnGetCardProductPrice(BigDecimal offerUnGetCardProductPrice) {
        this.offerUnGetCardProductPrice = offerUnGetCardProductPrice;
    }
    public BigDecimal getOfferUnSaleProductPrice() {
        return offerUnSaleProductPrice;
    }

    public void setOfferUnSaleProductPrice(BigDecimal offerUnSaleProductPrice) {
        this.offerUnSaleProductPrice = offerUnSaleProductPrice;
    }
    public BigDecimal getOfferSaledProductPrice() {
        return offerSaledProductPrice;
    }

    public void setOfferSaledProductPrice(BigDecimal offerSaledProductPrice) {
        this.offerSaledProductPrice = offerSaledProductPrice;
    }
    public BigDecimal getOfferSaledUnGetCardProductPrice() {
        return offerSaledUnGetCardProductPrice;
    }

    public void setOfferSaledUnGetCardProductPrice(BigDecimal offerSaledUnGetCardProductPrice) {
        this.offerSaledUnGetCardProductPrice = offerSaledUnGetCardProductPrice;
    }
    public BigDecimal getOfferSaledGetCardProductPrice() {
        return offerSaledGetCardProductPrice;
    }

    public void setOfferSaledGetCardProductPrice(BigDecimal offerSaledGetCardProductPrice) {
        this.offerSaledGetCardProductPrice = offerSaledGetCardProductPrice;
    }
    public BigDecimal getOfferAgeFirstProductPrice() {
        return offerAgeFirstProductPrice;
    }

    public void setOfferAgeFirstProductPrice(BigDecimal offerAgeFirstProductPrice) {
        this.offerAgeFirstProductPrice = offerAgeFirstProductPrice;
    }
    public BigDecimal getOfferAgeSencondProductPrice() {
        return offerAgeSencondProductPrice;
    }

    public void setOfferAgeSencondProductPrice(BigDecimal offerAgeSencondProductPrice) {
        this.offerAgeSencondProductPrice = offerAgeSencondProductPrice;
    }
    public BigDecimal getOfferAgeThirdProductPrice() {
        return offerAgeThirdProductPrice;
    }

    public void setOfferAgeThirdProductPrice(BigDecimal offerAgeThirdProductPrice) {
        this.offerAgeThirdProductPrice = offerAgeThirdProductPrice;
    }
    public BigDecimal getOfferAgeFourthProductPrice() {
        return offerAgeFourthProductPrice;
    }

    public void setOfferAgeFourthProductPrice(BigDecimal offerAgeFourthProductPrice) {
        this.offerAgeFourthProductPrice = offerAgeFourthProductPrice;
    }
    public BigDecimal getOfferAgeFifthProductPrice() {
        return offerAgeFifthProductPrice;
    }

    public void setOfferAgeFifthProductPrice(BigDecimal offerAgeFifthProductPrice) {
        this.offerAgeFifthProductPrice = offerAgeFifthProductPrice;
    }
    public BigDecimal getOfferUnDevelopAvgPrice() {
        return offerUnDevelopAvgPrice;
    }

    public void setOfferUnDevelopAvgPrice(BigDecimal offerUnDevelopAvgPrice) {
        this.offerUnDevelopAvgPrice = offerUnDevelopAvgPrice;
    }
    public BigDecimal getOfferUnSaleAvgPrice() {
        return offerUnSaleAvgPrice;
    }

    public void setOfferUnSaleAvgPrice(BigDecimal offerUnSaleAvgPrice) {
        this.offerUnSaleAvgPrice = offerUnSaleAvgPrice;
    }
    public BigDecimal getOfferSaledAvgPrice() {
        return offerSaledAvgPrice;
    }

    public void setOfferSaledAvgPrice(BigDecimal offerSaledAvgPrice) {
        this.offerSaledAvgPrice = offerSaledAvgPrice;
    }
    public BigDecimal getOfferUnStartArea() {
        return offerUnStartArea;
    }

    public void setOfferUnStartArea(BigDecimal offerUnStartArea) {
        this.offerUnStartArea = offerUnStartArea;
    }
    public BigDecimal getOfferUnReachImgGoalArea() {
        return offerUnReachImgGoalArea;
    }

    public void setOfferUnReachImgGoalArea(BigDecimal offerUnReachImgGoalArea) {
        this.offerUnReachImgGoalArea = offerUnReachImgGoalArea;
    }
    public BigDecimal getOfferUnGetCardArea() {
        return offerUnGetCardArea;
    }

    public void setOfferUnGetCardArea(BigDecimal offerUnGetCardArea) {
        this.offerUnGetCardArea = offerUnGetCardArea;
    }
    public BigDecimal getOfferUnSaleArea() {
        return offerUnSaleArea;
    }

    public void setOfferUnSaleArea(BigDecimal offerUnSaleArea) {
        this.offerUnSaleArea = offerUnSaleArea;
    }
    public BigDecimal getOfferSaledArea() {
        return offerSaledArea;
    }

    public void setOfferSaledArea(BigDecimal offerSaledArea) {
        this.offerSaledArea = offerSaledArea;
    }
    public BigDecimal getOfferSaledUnGetCardArea() {
        return offerSaledUnGetCardArea;
    }

    public void setOfferSaledUnGetCardArea(BigDecimal offerSaledUnGetCardArea) {
        this.offerSaledUnGetCardArea = offerSaledUnGetCardArea;
    }
    public BigDecimal getOfferSaledGetCardArea() {
        return offerSaledGetCardArea;
    }

    public void setOfferSaledGetCardArea(BigDecimal offerSaledGetCardArea) {
        this.offerSaledGetCardArea = offerSaledGetCardArea;
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
        return "BoManageOverviewPrice{" +
        "id=" + id +
        ", orgId=" + orgId +
        ", orgName=" + orgName +
        ", orgType=" + orgType +
        ", parentOrgId=" + parentOrgId +
        ", parentOrgName=" + parentOrgName +
        ", productTypeCode=" + productTypeCode +
        ", overallProductPrice=" + overallProductPrice +
        ", investDecisionProductPrice=" + investDecisionProductPrice +
        ", manageDecisionProductPrice=" + manageDecisionProductPrice +
        ", unDevelopProductPrice=" + unDevelopProductPrice +
        ", unStartProductPrice=" + unStartProductPrice +
        ", unReachImgGoalProductPrice=" + unReachImgGoalProductPrice +
        ", unGetCardProductPrice=" + unGetCardProductPrice +
        ", unSaleProductPrice=" + unSaleProductPrice +
        ", saledProductPrice=" + saledProductPrice +
        ", saledUnGetCardProductPrice=" + saledUnGetCardProductPrice +
        ", saledGetCardProductPrice=" + saledGetCardProductPrice +
        ", ageFirstProductPrice=" + ageFirstProductPrice +
        ", ageSencondProductPrice=" + ageSencondProductPrice +
        ", ageThirdProductPrice=" + ageThirdProductPrice +
        ", ageFourthProductPrice=" + ageFourthProductPrice +
        ", ageFifthProductPrice=" + ageFifthProductPrice +
        ", unDevelopAvgPrice=" + unDevelopAvgPrice +
        ", unSaleAvgPrice=" + unSaleAvgPrice +
        ", saledAvgPrice=" + saledAvgPrice +
        ", unDevelopArea=" + unDevelopArea +
        ", unStartArea=" + unStartArea +
        ", unReachImgGoalArea=" + unReachImgGoalArea +
        ", unGetCardArea=" + unGetCardArea +
        ", unSaleArea=" + unSaleArea +
        ", saledArea=" + saledArea +
        ", saledUnGetCardArea=" + saledUnGetCardArea +
        ", saledGetCardArea=" + saledGetCardArea +
        ", offerUnStartProductPrice=" + offerUnStartProductPrice +
        ", offerUnReachImgGoalProductPrice=" + offerUnReachImgGoalProductPrice +
        ", offerUnGetCardProductPrice=" + offerUnGetCardProductPrice +
        ", offerUnSaleProductPrice=" + offerUnSaleProductPrice +
        ", offerSaledProductPrice=" + offerSaledProductPrice +
        ", offerSaledUnGetCardProductPrice=" + offerSaledUnGetCardProductPrice +
        ", offerSaledGetCardProductPrice=" + offerSaledGetCardProductPrice +
        ", offerAgeFirstProductPrice=" + offerAgeFirstProductPrice +
        ", offerAgeSencondProductPrice=" + offerAgeSencondProductPrice +
        ", offerAgeThirdProductPrice=" + offerAgeThirdProductPrice +
        ", offerAgeFourthProductPrice=" + offerAgeFourthProductPrice +
        ", offerAgeFifthProductPrice=" + offerAgeFifthProductPrice +
        ", offerUnDevelopAvgPrice=" + offerUnDevelopAvgPrice +
        ", offerUnSaleAvgPrice=" + offerUnSaleAvgPrice +
        ", offerSaledAvgPrice=" + offerSaledAvgPrice +
        ", offerUnStartArea=" + offerUnStartArea +
        ", offerUnReachImgGoalArea=" + offerUnReachImgGoalArea +
        ", offerUnGetCardArea=" + offerUnGetCardArea +
        ", offerUnSaleArea=" + offerUnSaleArea +
        ", offerSaledArea=" + offerSaledArea +
        ", offerSaledUnGetCardArea=" + offerSaledUnGetCardArea +
        ", offerSaledGetCardArea=" + offerSaledGetCardArea +
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
