package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * TUTOU-产品定位信息表
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThProductPositionTb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 原ID
     */
    private Integer productId;

    /**
     * 土地ID
     */
    private Integer landId;

    /**
     * 类别(手填)
     */
    private String category;

    /**
     * 总面积(m2)
     */
    private BigDecimal totalArea;

    /**
     * 产权面积--字符串
     */
    private String propertyArea;

    /**
     * 使用面积
     */
    private String useArea;

    /**
     * 产权单价
     */
    private String propertyPrice;

    /**
     * 使用面积单价
     */
    private String useAreaPrice;

    /**
     * 套总价段（万元）
     */
    private String totalSection;

    /**
     * 套总价段（万元）备注
     */
    private String totalSectionMark;

    /**
     * 保本售价（万元/m2）
     */
    private String costSave;

    /**
     * 总货值（亿元）
     */
    private BigDecimal totalValue;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否删除 0否  1是
     */
    private Integer deleted;

    /**
     * 1-报意向;2-区域投决会;3-集团投决会;
     */
    private Integer type;

    /**
     * 户型（户型1：户型面积/户型2：户型面积）
     */
    private String huxing;

    /**
     * 套总价段（户型1：价格区间/户型2：价格区间）
     */
    private String totalPriceSection;

    /**
     * 保本均价
     */
    private String balancePrice;

    /**
     * 销售均价
     */
    private String averageSalePrice;

    /**
     * 套数
     */
    private String setNumber;

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
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    public Integer getLandId() {
        return landId;
    }

    public void setLandId(Integer landId) {
        this.landId = landId;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public BigDecimal getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(BigDecimal totalArea) {
        this.totalArea = totalArea;
    }
    public String getPropertyArea() {
        return propertyArea;
    }

    public void setPropertyArea(String propertyArea) {
        this.propertyArea = propertyArea;
    }
    public String getUseArea() {
        return useArea;
    }

    public void setUseArea(String useArea) {
        this.useArea = useArea;
    }
    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }
    public String getUseAreaPrice() {
        return useAreaPrice;
    }

    public void setUseAreaPrice(String useAreaPrice) {
        this.useAreaPrice = useAreaPrice;
    }
    public String getTotalSection() {
        return totalSection;
    }

    public void setTotalSection(String totalSection) {
        this.totalSection = totalSection;
    }
    public String getTotalSectionMark() {
        return totalSectionMark;
    }

    public void setTotalSectionMark(String totalSectionMark) {
        this.totalSectionMark = totalSectionMark;
    }
    public String getCostSave() {
        return costSave;
    }

    public void setCostSave(String costSave) {
        this.costSave = costSave;
    }
    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public String getHuxing() {
        return huxing;
    }

    public void setHuxing(String huxing) {
        this.huxing = huxing;
    }
    public String getTotalPriceSection() {
        return totalPriceSection;
    }

    public void setTotalPriceSection(String totalPriceSection) {
        this.totalPriceSection = totalPriceSection;
    }
    public String getBalancePrice() {
        return balancePrice;
    }

    public void setBalancePrice(String balancePrice) {
        this.balancePrice = balancePrice;
    }
    public String getAverageSalePrice() {
        return averageSalePrice;
    }

    public void setAverageSalePrice(String averageSalePrice) {
        this.averageSalePrice = averageSalePrice;
    }
    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
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
        return "TutouThProductPositionTb{" +
        "id=" + id +
        ", productId=" + productId +
        ", landId=" + landId +
        ", category=" + category +
        ", totalArea=" + totalArea +
        ", propertyArea=" + propertyArea +
        ", useArea=" + useArea +
        ", propertyPrice=" + propertyPrice +
        ", useAreaPrice=" + useAreaPrice +
        ", totalSection=" + totalSection +
        ", totalSectionMark=" + totalSectionMark +
        ", costSave=" + costSave +
        ", totalValue=" + totalValue +
        ", remarks=" + remarks +
        ", deleted=" + deleted +
        ", type=" + type +
        ", huxing=" + huxing +
        ", totalPriceSection=" + totalPriceSection +
        ", balancePrice=" + balancePrice +
        ", averageSalePrice=" + averageSalePrice +
        ", setNumber=" + setNumber +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
