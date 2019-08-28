package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThBusinessInfoTb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 商务信息id，原ID
     */
    private Integer businessInfoId;

    /**
     * 土地ID
     */
    private Integer landId;

    /**
     * 操盘方
     */
    private String operator;

    /**
     * 并表方
     */
    private String parallelOperator;

    /**
     * 合作方式ID
     */
    private Integer cooperationMoodId;

    /**
     * 我方股权比例（%）
     */
    private Double ourShareRatio;

    /**
     * 总对价（万元）
     */
    private BigDecimal totalPrice;

    /**
     * 股权对价（万元）
     */
    private BigDecimal equityPrice;

    /**
     * 负债（万元）
     */
    private BigDecimal liabilities;

    /**
     * 股权溢价（万元）
     */
    private BigDecimal equityPremium;

    /**
     * 原始土地成本（万元）
     */
    private BigDecimal originalLandCost;

    /**
     * 有票楼面价（万元）
     */
    private BigDecimal ticketFloorPrice;

    /**
     * 收购楼面价（万元）
     */
    private BigDecimal buyFloorPrice;

    /**
     * 管理费包干（万元）
     */
    private BigDecimal managementFee;

    /**
     * 营销费（万元）
     */
    private BigDecimal marketingFee;

    /**
     * 品牌使用费（万元）
     */
    private BigDecimal brandUseFee;

    /**
     * 融资金额（万元）
     */
    private BigDecimal financingAmount;

    /**
     * 自有资金（万元）
     */
    private BigDecimal ownFunds;

    /**
     * 核心商务条款
     */
    private String coreBusinessTerms;

    /**
     * 交易路径说明
     */
    private String pathDescription;

    /**
     * 股权结构图路径
     */
    private String path;

    /**
     * 1-报意向;2-区域投决会;3-集团投决会
     */
    private Integer type;

    /**
     * 我方是否体内公司签约  0否 1是
     */
    private Integer companyContract;

    /**
     * 其中1：土地出让金
     */
    private BigDecimal landGrant;

    /**
     * 其中2：拆迁费用
     */
    private BigDecimal demolitionCost;

    /**
     * 其中3：契税
     */
    private BigDecimal deedTax;

    /**
     * 其中4：其他有票成本
     */
    private BigDecimal otherCostTickets;

    /**
     * 我方签约公司全称
     */
    private String weContractCompanyName;

    /**
     * 我方商务负责人
     */
    private String weCommerceLeader;

    /**
     * 对方签约公司全称
     */
    private String otherContractCompanyName;

    /**
     * 我方承办人
     */
    private String ourContractor;

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
    public Integer getBusinessInfoId() {
        return businessInfoId;
    }

    public void setBusinessInfoId(Integer businessInfoId) {
        this.businessInfoId = businessInfoId;
    }
    public Integer getLandId() {
        return landId;
    }

    public void setLandId(Integer landId) {
        this.landId = landId;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getParallelOperator() {
        return parallelOperator;
    }

    public void setParallelOperator(String parallelOperator) {
        this.parallelOperator = parallelOperator;
    }
    public Integer getCooperationMoodId() {
        return cooperationMoodId;
    }

    public void setCooperationMoodId(Integer cooperationMoodId) {
        this.cooperationMoodId = cooperationMoodId;
    }
    public Double getOurShareRatio() {
        return ourShareRatio;
    }

    public void setOurShareRatio(Double ourShareRatio) {
        this.ourShareRatio = ourShareRatio;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public BigDecimal getEquityPrice() {
        return equityPrice;
    }

    public void setEquityPrice(BigDecimal equityPrice) {
        this.equityPrice = equityPrice;
    }
    public BigDecimal getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(BigDecimal liabilities) {
        this.liabilities = liabilities;
    }
    public BigDecimal getEquityPremium() {
        return equityPremium;
    }

    public void setEquityPremium(BigDecimal equityPremium) {
        this.equityPremium = equityPremium;
    }
    public BigDecimal getOriginalLandCost() {
        return originalLandCost;
    }

    public void setOriginalLandCost(BigDecimal originalLandCost) {
        this.originalLandCost = originalLandCost;
    }
    public BigDecimal getTicketFloorPrice() {
        return ticketFloorPrice;
    }

    public void setTicketFloorPrice(BigDecimal ticketFloorPrice) {
        this.ticketFloorPrice = ticketFloorPrice;
    }
    public BigDecimal getBuyFloorPrice() {
        return buyFloorPrice;
    }

    public void setBuyFloorPrice(BigDecimal buyFloorPrice) {
        this.buyFloorPrice = buyFloorPrice;
    }
    public BigDecimal getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(BigDecimal managementFee) {
        this.managementFee = managementFee;
    }
    public BigDecimal getMarketingFee() {
        return marketingFee;
    }

    public void setMarketingFee(BigDecimal marketingFee) {
        this.marketingFee = marketingFee;
    }
    public BigDecimal getBrandUseFee() {
        return brandUseFee;
    }

    public void setBrandUseFee(BigDecimal brandUseFee) {
        this.brandUseFee = brandUseFee;
    }
    public BigDecimal getFinancingAmount() {
        return financingAmount;
    }

    public void setFinancingAmount(BigDecimal financingAmount) {
        this.financingAmount = financingAmount;
    }
    public BigDecimal getOwnFunds() {
        return ownFunds;
    }

    public void setOwnFunds(BigDecimal ownFunds) {
        this.ownFunds = ownFunds;
    }
    public String getCoreBusinessTerms() {
        return coreBusinessTerms;
    }

    public void setCoreBusinessTerms(String coreBusinessTerms) {
        this.coreBusinessTerms = coreBusinessTerms;
    }
    public String getPathDescription() {
        return pathDescription;
    }

    public void setPathDescription(String pathDescription) {
        this.pathDescription = pathDescription;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getCompanyContract() {
        return companyContract;
    }

    public void setCompanyContract(Integer companyContract) {
        this.companyContract = companyContract;
    }
    public BigDecimal getLandGrant() {
        return landGrant;
    }

    public void setLandGrant(BigDecimal landGrant) {
        this.landGrant = landGrant;
    }
    public BigDecimal getDemolitionCost() {
        return demolitionCost;
    }

    public void setDemolitionCost(BigDecimal demolitionCost) {
        this.demolitionCost = demolitionCost;
    }
    public BigDecimal getDeedTax() {
        return deedTax;
    }

    public void setDeedTax(BigDecimal deedTax) {
        this.deedTax = deedTax;
    }
    public BigDecimal getOtherCostTickets() {
        return otherCostTickets;
    }

    public void setOtherCostTickets(BigDecimal otherCostTickets) {
        this.otherCostTickets = otherCostTickets;
    }
    public String getWeContractCompanyName() {
        return weContractCompanyName;
    }

    public void setWeContractCompanyName(String weContractCompanyName) {
        this.weContractCompanyName = weContractCompanyName;
    }
    public String getWeCommerceLeader() {
        return weCommerceLeader;
    }

    public void setWeCommerceLeader(String weCommerceLeader) {
        this.weCommerceLeader = weCommerceLeader;
    }
    public String getOtherContractCompanyName() {
        return otherContractCompanyName;
    }

    public void setOtherContractCompanyName(String otherContractCompanyName) {
        this.otherContractCompanyName = otherContractCompanyName;
    }
    public String getOurContractor() {
        return ourContractor;
    }

    public void setOurContractor(String ourContractor) {
        this.ourContractor = ourContractor;
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
        return "TutouThBusinessInfoTb{" +
        "id=" + id +
        ", businessInfoId=" + businessInfoId +
        ", landId=" + landId +
        ", operator=" + operator +
        ", parallelOperator=" + parallelOperator +
        ", cooperationMoodId=" + cooperationMoodId +
        ", ourShareRatio=" + ourShareRatio +
        ", totalPrice=" + totalPrice +
        ", equityPrice=" + equityPrice +
        ", liabilities=" + liabilities +
        ", equityPremium=" + equityPremium +
        ", originalLandCost=" + originalLandCost +
        ", ticketFloorPrice=" + ticketFloorPrice +
        ", buyFloorPrice=" + buyFloorPrice +
        ", managementFee=" + managementFee +
        ", marketingFee=" + marketingFee +
        ", brandUseFee=" + brandUseFee +
        ", financingAmount=" + financingAmount +
        ", ownFunds=" + ownFunds +
        ", coreBusinessTerms=" + coreBusinessTerms +
        ", pathDescription=" + pathDescription +
        ", path=" + path +
        ", type=" + type +
        ", companyContract=" + companyContract +
        ", landGrant=" + landGrant +
        ", demolitionCost=" + demolitionCost +
        ", deedTax=" + deedTax +
        ", otherCostTickets=" + otherCostTickets +
        ", weContractCompanyName=" + weContractCompanyName +
        ", weCommerceLeader=" + weCommerceLeader +
        ", otherContractCompanyName=" + otherContractCompanyName +
        ", ourContractor=" + ourContractor +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
