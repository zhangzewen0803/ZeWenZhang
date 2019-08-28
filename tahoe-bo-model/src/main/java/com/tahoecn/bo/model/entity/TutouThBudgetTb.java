package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * TUTOU-测算对比表
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThBudgetTb implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 原表ID
     */
    private Integer budgetId;

    /**
     * 土地ID
     */
    private Integer landId;

    /**
     * 总货值-总额 
     */
    private BigDecimal totalValue;

    /**
     * 总货值-可售单方
     */
    private BigDecimal totalValueUse;

    /**
     * 直接成本变前期费用
     */
    private BigDecimal directCost;

    /**
     * 前期费用-可售单方
     */
    private BigDecimal directCostUse;

    /**
     * 土地成本
     */
    private BigDecimal landCost;

    /**
     * 土地成本-可售单方
     */
    private BigDecimal landCostPlan;

    /**
     * 开发成本
     */
    private BigDecimal devCost;

    /**
     * 开发成本-可售单方
     */
    private BigDecimal devCostPlan;

    /**
     * 建安成本
     */
    private BigDecimal constructionCost;

    /**
     * 建安成本-可售单方
     */
    private BigDecimal constructionCostPlan;

    /**
     * 开发间接费变其他直接费
     */
    private BigDecimal developmentIndirectFee;

    /**
     * 其他直接费-可售单方
     */
    private BigDecimal developmentIndirectFeePlan;

    /**
     * 不可预见费变资本化费用
     */
    private BigDecimal unforeseeableFee;

    /**
     * 资本化费用-可售单方
     */
    private BigDecimal unforeseeableFeePlan;

    /**
     * 期间费用
     */
    private BigDecimal costPeriod;

    /**
     * 期间费用-可售单方
     */
    private BigDecimal costPeriodUse;

    /**
     * 销售费用变营销费用
     */
    private BigDecimal sellingExpenses;

    /**
     * 营销费用-可售单方
     */
    private BigDecimal sellingExpensesPlan;

    /**
     * 管理费用
     */
    private BigDecimal managementCost;

    /**
     * 管理费用-可售单方
     */
    private BigDecimal managementCostPlan;

    /**
     * 财务费用 
     */
    private BigDecimal financialCost;

    /**
     * 财务费用-可售单方
     */
    private BigDecimal financialCostPlan;

    /**
     * 税费
     */
    private BigDecimal taxation;

    /**
     * 税费-可售单方
     */
    private BigDecimal taxationUse;

    /**
     * 增值税
     */
    private BigDecimal valueAddedTax;

    /**
     * 增值税-可售单方
     */
    private BigDecimal valueAddedTaxPlan;

    /**
     * 增值税附加
     */
    private BigDecimal valueAddedTaxAdded;

    /**
     * 增值税附加-可售单方
     */
    private BigDecimal valueAddedTaxAddedPlan;

    /**
     * 土地增值税
     */
    private BigDecimal landValueAddedTax;

    /**
     * 土地增值税-可售单方
     */
    private BigDecimal landValueAddedTaxPlan;

    /**
     * 企业所得税
     */
    private BigDecimal corporateIncomeTax;

    /**
     * 企业所得税-可售单方
     */
    private BigDecimal corporateIncomeTaxPlan;

    /**
     * 净利润额
     */
    private BigDecimal shareholderNetProfit;

    /**
     * 净利润额-可售单方
     */
    private BigDecimal netMargin;

    /**
     * 净利润率（%）
     */
    private Double shareholderNterestRate;

    /**
     * 经营性现金流峰值
     */
    private BigDecimal managementPeakCapital;

    /**
     * 融资后现金流峰值
     */
    private BigDecimal financingPeakCapital;

    /**
     * 经营性现金流回正周期
     */
    private String cashCycle;

    /**
     * 融资后现金流回正周期（月）
     */
    private String financingPeakCycle;

    /**
     * 项目公司IRR
     */
    private BigDecimal itemCompanyIrr;

    /**
     * 项目公司lv-IRR
     */
    private BigDecimal itemCompanyLvIrr;

    /**
     * 泰禾股权比例% 
     */
    private BigDecimal shareRatio;

    /**
     * 收购对价
     */
    private BigDecimal purchasePrice;

    /**
     * 收购对价-可售单方 
     */
    private BigDecimal purchasePriceUse;

    /**
     * 溢价
     */
    private BigDecimal premium;

    /**
     * 溢价-可售单方
     */
    private BigDecimal premiumUse;

    /**
     * 归属泰禾总货值
     */
    private BigDecimal tahoeTotalValue;

    /**
     * 归属泰禾总货值-可售单方
     */
    private BigDecimal tahoeTotalValueUse;

    /**
     * 归属泰禾净利润额
     */
    private BigDecimal tahoeNetProfit;

    /**
     * 归属泰禾净利润额-可售单方
     */
    private BigDecimal tahoeNetProfitUse;

    /**
     * 泰禾净利润率
     */
    private BigDecimal tahoeNetProfitMargin;

    /**
     * 泰禾净利润率-可售单方 
     */
    private BigDecimal tahoeNetProfitMarginUse;

    /**
     * 泰禾自有资金投入
     */
    private BigDecimal shareholderCapitalInvestment;

    /**
     * 泰禾自有资金投入-可售单方
     */
    private BigDecimal shareholderCapitalInvestmentUse;

    /**
     * 货地比（%）
     */
    private Double landRatio;

    /**
     * 自有资金回正周期
     */
    private String capitalCycle;

    /**
     * 股东IRR 
     */
    private BigDecimal shareIrr;

    /**
     * 货值(财务规划后)
     */
    private BigDecimal landValue;

    /**
     * 货值(财务规划后)-可售单方
     */
    private BigDecimal landValueUse;

    /**
     * 直接成本(财务规划后)
     */
    private BigDecimal directCostAccounts;

    /**
     * 直接成本(财务规划后)-可售单方
     */
    private BigDecimal directCostAccountsUse;

    /**
     * 间接费用(财务规划后)
     */
    private BigDecimal indirectFee;

    /**
     * 间接费用(财务规划后)-可售单方
     */
    private BigDecimal indirectFeeUse;

    /**
     * 税费(财务规划后)
     */
    private BigDecimal taxationAccounts;

    /**
     * 税费(财务规划后)-可售单方
     */
    private BigDecimal taxationAccountsUse;

    /**
     * 归属泰禾总货值(财务规划后)
     */
    private BigDecimal tahoeTotalValueAccounts;

    /**
     * 归属泰禾总货值(财务规划后)-可售单方
     */
    private BigDecimal tahoeTotalValueAccountsUse;

    /**
     * 归属泰禾净利润额(财务规划后)
     */
    private BigDecimal tahoeNetProfitAccounts;

    /**
     * 归属泰禾净利润额(财务规划后)-可售单方
     */
    private BigDecimal tahoeNetProfitAccountsUse;

    /**
     * 泰禾净利润率(财务规划后)
     */
    private BigDecimal tahoeNetProfitMarginAccounts;

    /**
     * 附件本地名称
     */
    private String appendixLocalName;

    /**
     * 附件数据库名称
     */
    private String appendixDatabaseName;

    /**
     * 附件url地址
     */
    private String appendixUrl;

    /**
     * 阶段类型  1-报意向;2-区域投决会;3-集团投决会
     */
    private Integer type;

    /**
     * 其他税金-可售单方
     */
    private BigDecimal otherTaxesSquare;

    /**
     * 其他税金
     */
    private BigDecimal otherTaxes;

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
    public Integer getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }
    public Integer getLandId() {
        return landId;
    }

    public void setLandId(Integer landId) {
        this.landId = landId;
    }
    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    public BigDecimal getTotalValueUse() {
        return totalValueUse;
    }

    public void setTotalValueUse(BigDecimal totalValueUse) {
        this.totalValueUse = totalValueUse;
    }
    public BigDecimal getDirectCost() {
        return directCost;
    }

    public void setDirectCost(BigDecimal directCost) {
        this.directCost = directCost;
    }
    public BigDecimal getDirectCostUse() {
        return directCostUse;
    }

    public void setDirectCostUse(BigDecimal directCostUse) {
        this.directCostUse = directCostUse;
    }
    public BigDecimal getLandCost() {
        return landCost;
    }

    public void setLandCost(BigDecimal landCost) {
        this.landCost = landCost;
    }
    public BigDecimal getLandCostPlan() {
        return landCostPlan;
    }

    public void setLandCostPlan(BigDecimal landCostPlan) {
        this.landCostPlan = landCostPlan;
    }
    public BigDecimal getDevCost() {
        return devCost;
    }

    public void setDevCost(BigDecimal devCost) {
        this.devCost = devCost;
    }
    public BigDecimal getDevCostPlan() {
        return devCostPlan;
    }

    public void setDevCostPlan(BigDecimal devCostPlan) {
        this.devCostPlan = devCostPlan;
    }
    public BigDecimal getConstructionCost() {
        return constructionCost;
    }

    public void setConstructionCost(BigDecimal constructionCost) {
        this.constructionCost = constructionCost;
    }
    public BigDecimal getConstructionCostPlan() {
        return constructionCostPlan;
    }

    public void setConstructionCostPlan(BigDecimal constructionCostPlan) {
        this.constructionCostPlan = constructionCostPlan;
    }
    public BigDecimal getDevelopmentIndirectFee() {
        return developmentIndirectFee;
    }

    public void setDevelopmentIndirectFee(BigDecimal developmentIndirectFee) {
        this.developmentIndirectFee = developmentIndirectFee;
    }
    public BigDecimal getDevelopmentIndirectFeePlan() {
        return developmentIndirectFeePlan;
    }

    public void setDevelopmentIndirectFeePlan(BigDecimal developmentIndirectFeePlan) {
        this.developmentIndirectFeePlan = developmentIndirectFeePlan;
    }
    public BigDecimal getUnforeseeableFee() {
        return unforeseeableFee;
    }

    public void setUnforeseeableFee(BigDecimal unforeseeableFee) {
        this.unforeseeableFee = unforeseeableFee;
    }
    public BigDecimal getUnforeseeableFeePlan() {
        return unforeseeableFeePlan;
    }

    public void setUnforeseeableFeePlan(BigDecimal unforeseeableFeePlan) {
        this.unforeseeableFeePlan = unforeseeableFeePlan;
    }
    public BigDecimal getCostPeriod() {
        return costPeriod;
    }

    public void setCostPeriod(BigDecimal costPeriod) {
        this.costPeriod = costPeriod;
    }
    public BigDecimal getCostPeriodUse() {
        return costPeriodUse;
    }

    public void setCostPeriodUse(BigDecimal costPeriodUse) {
        this.costPeriodUse = costPeriodUse;
    }
    public BigDecimal getSellingExpenses() {
        return sellingExpenses;
    }

    public void setSellingExpenses(BigDecimal sellingExpenses) {
        this.sellingExpenses = sellingExpenses;
    }
    public BigDecimal getSellingExpensesPlan() {
        return sellingExpensesPlan;
    }

    public void setSellingExpensesPlan(BigDecimal sellingExpensesPlan) {
        this.sellingExpensesPlan = sellingExpensesPlan;
    }
    public BigDecimal getManagementCost() {
        return managementCost;
    }

    public void setManagementCost(BigDecimal managementCost) {
        this.managementCost = managementCost;
    }
    public BigDecimal getManagementCostPlan() {
        return managementCostPlan;
    }

    public void setManagementCostPlan(BigDecimal managementCostPlan) {
        this.managementCostPlan = managementCostPlan;
    }
    public BigDecimal getFinancialCost() {
        return financialCost;
    }

    public void setFinancialCost(BigDecimal financialCost) {
        this.financialCost = financialCost;
    }
    public BigDecimal getFinancialCostPlan() {
        return financialCostPlan;
    }

    public void setFinancialCostPlan(BigDecimal financialCostPlan) {
        this.financialCostPlan = financialCostPlan;
    }
    public BigDecimal getTaxation() {
        return taxation;
    }

    public void setTaxation(BigDecimal taxation) {
        this.taxation = taxation;
    }
    public BigDecimal getTaxationUse() {
        return taxationUse;
    }

    public void setTaxationUse(BigDecimal taxationUse) {
        this.taxationUse = taxationUse;
    }
    public BigDecimal getValueAddedTax() {
        return valueAddedTax;
    }

    public void setValueAddedTax(BigDecimal valueAddedTax) {
        this.valueAddedTax = valueAddedTax;
    }
    public BigDecimal getValueAddedTaxPlan() {
        return valueAddedTaxPlan;
    }

    public void setValueAddedTaxPlan(BigDecimal valueAddedTaxPlan) {
        this.valueAddedTaxPlan = valueAddedTaxPlan;
    }
    public BigDecimal getValueAddedTaxAdded() {
        return valueAddedTaxAdded;
    }

    public void setValueAddedTaxAdded(BigDecimal valueAddedTaxAdded) {
        this.valueAddedTaxAdded = valueAddedTaxAdded;
    }
    public BigDecimal getValueAddedTaxAddedPlan() {
        return valueAddedTaxAddedPlan;
    }

    public void setValueAddedTaxAddedPlan(BigDecimal valueAddedTaxAddedPlan) {
        this.valueAddedTaxAddedPlan = valueAddedTaxAddedPlan;
    }
    public BigDecimal getLandValueAddedTax() {
        return landValueAddedTax;
    }

    public void setLandValueAddedTax(BigDecimal landValueAddedTax) {
        this.landValueAddedTax = landValueAddedTax;
    }
    public BigDecimal getLandValueAddedTaxPlan() {
        return landValueAddedTaxPlan;
    }

    public void setLandValueAddedTaxPlan(BigDecimal landValueAddedTaxPlan) {
        this.landValueAddedTaxPlan = landValueAddedTaxPlan;
    }
    public BigDecimal getCorporateIncomeTax() {
        return corporateIncomeTax;
    }

    public void setCorporateIncomeTax(BigDecimal corporateIncomeTax) {
        this.corporateIncomeTax = corporateIncomeTax;
    }
    public BigDecimal getCorporateIncomeTaxPlan() {
        return corporateIncomeTaxPlan;
    }

    public void setCorporateIncomeTaxPlan(BigDecimal corporateIncomeTaxPlan) {
        this.corporateIncomeTaxPlan = corporateIncomeTaxPlan;
    }
    public BigDecimal getShareholderNetProfit() {
        return shareholderNetProfit;
    }

    public void setShareholderNetProfit(BigDecimal shareholderNetProfit) {
        this.shareholderNetProfit = shareholderNetProfit;
    }
    public BigDecimal getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(BigDecimal netMargin) {
        this.netMargin = netMargin;
    }
    public Double getShareholderNterestRate() {
        return shareholderNterestRate;
    }

    public void setShareholderNterestRate(Double shareholderNterestRate) {
        this.shareholderNterestRate = shareholderNterestRate;
    }
    public BigDecimal getManagementPeakCapital() {
        return managementPeakCapital;
    }

    public void setManagementPeakCapital(BigDecimal managementPeakCapital) {
        this.managementPeakCapital = managementPeakCapital;
    }
    public BigDecimal getFinancingPeakCapital() {
        return financingPeakCapital;
    }

    public void setFinancingPeakCapital(BigDecimal financingPeakCapital) {
        this.financingPeakCapital = financingPeakCapital;
    }
    public String getCashCycle() {
        return cashCycle;
    }

    public void setCashCycle(String cashCycle) {
        this.cashCycle = cashCycle;
    }
    public String getFinancingPeakCycle() {
        return financingPeakCycle;
    }

    public void setFinancingPeakCycle(String financingPeakCycle) {
        this.financingPeakCycle = financingPeakCycle;
    }
    public BigDecimal getItemCompanyIrr() {
        return itemCompanyIrr;
    }

    public void setItemCompanyIrr(BigDecimal itemCompanyIrr) {
        this.itemCompanyIrr = itemCompanyIrr;
    }
    public BigDecimal getItemCompanyLvIrr() {
        return itemCompanyLvIrr;
    }

    public void setItemCompanyLvIrr(BigDecimal itemCompanyLvIrr) {
        this.itemCompanyLvIrr = itemCompanyLvIrr;
    }
    public BigDecimal getShareRatio() {
        return shareRatio;
    }

    public void setShareRatio(BigDecimal shareRatio) {
        this.shareRatio = shareRatio;
    }
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    public BigDecimal getPurchasePriceUse() {
        return purchasePriceUse;
    }

    public void setPurchasePriceUse(BigDecimal purchasePriceUse) {
        this.purchasePriceUse = purchasePriceUse;
    }
    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }
    public BigDecimal getPremiumUse() {
        return premiumUse;
    }

    public void setPremiumUse(BigDecimal premiumUse) {
        this.premiumUse = premiumUse;
    }
    public BigDecimal getTahoeTotalValue() {
        return tahoeTotalValue;
    }

    public void setTahoeTotalValue(BigDecimal tahoeTotalValue) {
        this.tahoeTotalValue = tahoeTotalValue;
    }
    public BigDecimal getTahoeTotalValueUse() {
        return tahoeTotalValueUse;
    }

    public void setTahoeTotalValueUse(BigDecimal tahoeTotalValueUse) {
        this.tahoeTotalValueUse = tahoeTotalValueUse;
    }
    public BigDecimal getTahoeNetProfit() {
        return tahoeNetProfit;
    }

    public void setTahoeNetProfit(BigDecimal tahoeNetProfit) {
        this.tahoeNetProfit = tahoeNetProfit;
    }
    public BigDecimal getTahoeNetProfitUse() {
        return tahoeNetProfitUse;
    }

    public void setTahoeNetProfitUse(BigDecimal tahoeNetProfitUse) {
        this.tahoeNetProfitUse = tahoeNetProfitUse;
    }
    public BigDecimal getTahoeNetProfitMargin() {
        return tahoeNetProfitMargin;
    }

    public void setTahoeNetProfitMargin(BigDecimal tahoeNetProfitMargin) {
        this.tahoeNetProfitMargin = tahoeNetProfitMargin;
    }
    public BigDecimal getTahoeNetProfitMarginUse() {
        return tahoeNetProfitMarginUse;
    }

    public void setTahoeNetProfitMarginUse(BigDecimal tahoeNetProfitMarginUse) {
        this.tahoeNetProfitMarginUse = tahoeNetProfitMarginUse;
    }
    public BigDecimal getShareholderCapitalInvestment() {
        return shareholderCapitalInvestment;
    }

    public void setShareholderCapitalInvestment(BigDecimal shareholderCapitalInvestment) {
        this.shareholderCapitalInvestment = shareholderCapitalInvestment;
    }
    public BigDecimal getShareholderCapitalInvestmentUse() {
        return shareholderCapitalInvestmentUse;
    }

    public void setShareholderCapitalInvestmentUse(BigDecimal shareholderCapitalInvestmentUse) {
        this.shareholderCapitalInvestmentUse = shareholderCapitalInvestmentUse;
    }
    public Double getLandRatio() {
        return landRatio;
    }

    public void setLandRatio(Double landRatio) {
        this.landRatio = landRatio;
    }
    public String getCapitalCycle() {
        return capitalCycle;
    }

    public void setCapitalCycle(String capitalCycle) {
        this.capitalCycle = capitalCycle;
    }
    public BigDecimal getShareIrr() {
        return shareIrr;
    }

    public void setShareIrr(BigDecimal shareIrr) {
        this.shareIrr = shareIrr;
    }
    public BigDecimal getLandValue() {
        return landValue;
    }

    public void setLandValue(BigDecimal landValue) {
        this.landValue = landValue;
    }
    public BigDecimal getLandValueUse() {
        return landValueUse;
    }

    public void setLandValueUse(BigDecimal landValueUse) {
        this.landValueUse = landValueUse;
    }
    public BigDecimal getDirectCostAccounts() {
        return directCostAccounts;
    }

    public void setDirectCostAccounts(BigDecimal directCostAccounts) {
        this.directCostAccounts = directCostAccounts;
    }
    public BigDecimal getDirectCostAccountsUse() {
        return directCostAccountsUse;
    }

    public void setDirectCostAccountsUse(BigDecimal directCostAccountsUse) {
        this.directCostAccountsUse = directCostAccountsUse;
    }
    public BigDecimal getIndirectFee() {
        return indirectFee;
    }

    public void setIndirectFee(BigDecimal indirectFee) {
        this.indirectFee = indirectFee;
    }
    public BigDecimal getIndirectFeeUse() {
        return indirectFeeUse;
    }

    public void setIndirectFeeUse(BigDecimal indirectFeeUse) {
        this.indirectFeeUse = indirectFeeUse;
    }
    public BigDecimal getTaxationAccounts() {
        return taxationAccounts;
    }

    public void setTaxationAccounts(BigDecimal taxationAccounts) {
        this.taxationAccounts = taxationAccounts;
    }
    public BigDecimal getTaxationAccountsUse() {
        return taxationAccountsUse;
    }

    public void setTaxationAccountsUse(BigDecimal taxationAccountsUse) {
        this.taxationAccountsUse = taxationAccountsUse;
    }
    public BigDecimal getTahoeTotalValueAccounts() {
        return tahoeTotalValueAccounts;
    }

    public void setTahoeTotalValueAccounts(BigDecimal tahoeTotalValueAccounts) {
        this.tahoeTotalValueAccounts = tahoeTotalValueAccounts;
    }
    public BigDecimal getTahoeTotalValueAccountsUse() {
        return tahoeTotalValueAccountsUse;
    }

    public void setTahoeTotalValueAccountsUse(BigDecimal tahoeTotalValueAccountsUse) {
        this.tahoeTotalValueAccountsUse = tahoeTotalValueAccountsUse;
    }
    public BigDecimal getTahoeNetProfitAccounts() {
        return tahoeNetProfitAccounts;
    }

    public void setTahoeNetProfitAccounts(BigDecimal tahoeNetProfitAccounts) {
        this.tahoeNetProfitAccounts = tahoeNetProfitAccounts;
    }
    public BigDecimal getTahoeNetProfitAccountsUse() {
        return tahoeNetProfitAccountsUse;
    }

    public void setTahoeNetProfitAccountsUse(BigDecimal tahoeNetProfitAccountsUse) {
        this.tahoeNetProfitAccountsUse = tahoeNetProfitAccountsUse;
    }
    public BigDecimal getTahoeNetProfitMarginAccounts() {
        return tahoeNetProfitMarginAccounts;
    }

    public void setTahoeNetProfitMarginAccounts(BigDecimal tahoeNetProfitMarginAccounts) {
        this.tahoeNetProfitMarginAccounts = tahoeNetProfitMarginAccounts;
    }
    public String getAppendixLocalName() {
        return appendixLocalName;
    }

    public void setAppendixLocalName(String appendixLocalName) {
        this.appendixLocalName = appendixLocalName;
    }
    public String getAppendixDatabaseName() {
        return appendixDatabaseName;
    }

    public void setAppendixDatabaseName(String appendixDatabaseName) {
        this.appendixDatabaseName = appendixDatabaseName;
    }
    public String getAppendixUrl() {
        return appendixUrl;
    }

    public void setAppendixUrl(String appendixUrl) {
        this.appendixUrl = appendixUrl;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public BigDecimal getOtherTaxesSquare() {
        return otherTaxesSquare;
    }

    public void setOtherTaxesSquare(BigDecimal otherTaxesSquare) {
        this.otherTaxesSquare = otherTaxesSquare;
    }
    public BigDecimal getOtherTaxes() {
        return otherTaxes;
    }

    public void setOtherTaxes(BigDecimal otherTaxes) {
        this.otherTaxes = otherTaxes;
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
        return "TutouThBudgetTb{" +
        "id=" + id +
        ", budgetId=" + budgetId +
        ", landId=" + landId +
        ", totalValue=" + totalValue +
        ", totalValueUse=" + totalValueUse +
        ", directCost=" + directCost +
        ", directCostUse=" + directCostUse +
        ", landCost=" + landCost +
        ", landCostPlan=" + landCostPlan +
        ", devCost=" + devCost +
        ", devCostPlan=" + devCostPlan +
        ", constructionCost=" + constructionCost +
        ", constructionCostPlan=" + constructionCostPlan +
        ", developmentIndirectFee=" + developmentIndirectFee +
        ", developmentIndirectFeePlan=" + developmentIndirectFeePlan +
        ", unforeseeableFee=" + unforeseeableFee +
        ", unforeseeableFeePlan=" + unforeseeableFeePlan +
        ", costPeriod=" + costPeriod +
        ", costPeriodUse=" + costPeriodUse +
        ", sellingExpenses=" + sellingExpenses +
        ", sellingExpensesPlan=" + sellingExpensesPlan +
        ", managementCost=" + managementCost +
        ", managementCostPlan=" + managementCostPlan +
        ", financialCost=" + financialCost +
        ", financialCostPlan=" + financialCostPlan +
        ", taxation=" + taxation +
        ", taxationUse=" + taxationUse +
        ", valueAddedTax=" + valueAddedTax +
        ", valueAddedTaxPlan=" + valueAddedTaxPlan +
        ", valueAddedTaxAdded=" + valueAddedTaxAdded +
        ", valueAddedTaxAddedPlan=" + valueAddedTaxAddedPlan +
        ", landValueAddedTax=" + landValueAddedTax +
        ", landValueAddedTaxPlan=" + landValueAddedTaxPlan +
        ", corporateIncomeTax=" + corporateIncomeTax +
        ", corporateIncomeTaxPlan=" + corporateIncomeTaxPlan +
        ", shareholderNetProfit=" + shareholderNetProfit +
        ", netMargin=" + netMargin +
        ", shareholderNterestRate=" + shareholderNterestRate +
        ", managementPeakCapital=" + managementPeakCapital +
        ", financingPeakCapital=" + financingPeakCapital +
        ", cashCycle=" + cashCycle +
        ", financingPeakCycle=" + financingPeakCycle +
        ", itemCompanyIrr=" + itemCompanyIrr +
        ", itemCompanyLvIrr=" + itemCompanyLvIrr +
        ", shareRatio=" + shareRatio +
        ", purchasePrice=" + purchasePrice +
        ", purchasePriceUse=" + purchasePriceUse +
        ", premium=" + premium +
        ", premiumUse=" + premiumUse +
        ", tahoeTotalValue=" + tahoeTotalValue +
        ", tahoeTotalValueUse=" + tahoeTotalValueUse +
        ", tahoeNetProfit=" + tahoeNetProfit +
        ", tahoeNetProfitUse=" + tahoeNetProfitUse +
        ", tahoeNetProfitMargin=" + tahoeNetProfitMargin +
        ", tahoeNetProfitMarginUse=" + tahoeNetProfitMarginUse +
        ", shareholderCapitalInvestment=" + shareholderCapitalInvestment +
        ", shareholderCapitalInvestmentUse=" + shareholderCapitalInvestmentUse +
        ", landRatio=" + landRatio +
        ", capitalCycle=" + capitalCycle +
        ", shareIrr=" + shareIrr +
        ", landValue=" + landValue +
        ", landValueUse=" + landValueUse +
        ", directCostAccounts=" + directCostAccounts +
        ", directCostAccountsUse=" + directCostAccountsUse +
        ", indirectFee=" + indirectFee +
        ", indirectFeeUse=" + indirectFeeUse +
        ", taxationAccounts=" + taxationAccounts +
        ", taxationAccountsUse=" + taxationAccountsUse +
        ", tahoeTotalValueAccounts=" + tahoeTotalValueAccounts +
        ", tahoeTotalValueAccountsUse=" + tahoeTotalValueAccountsUse +
        ", tahoeNetProfitAccounts=" + tahoeNetProfitAccounts +
        ", tahoeNetProfitAccountsUse=" + tahoeNetProfitAccountsUse +
        ", tahoeNetProfitMarginAccounts=" + tahoeNetProfitMarginAccounts +
        ", appendixLocalName=" + appendixLocalName +
        ", appendixDatabaseName=" + appendixDatabaseName +
        ", appendixUrl=" + appendixUrl +
        ", type=" + type +
        ", otherTaxesSquare=" + otherTaxesSquare +
        ", otherTaxes=" + otherTaxes +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
