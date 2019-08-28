package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 供货计划数据表
 * </p>
 *
 * @author panglx
 * @since 2019-08-08
 */
public class BoSupplyPlanData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 供货计划业态关系表ID
     */
    private String supplyPlanProductTypeMapId;

    /**
     * 供货计划面积
     */
    private BigDecimal supplyPlanArea;

    /**
     * 供货计划货值
     */
    private BigDecimal supplyPlanPrice;

    /**
     * 供货计划日期
     */
    private LocalDateTime supplyPlanDate;

    /**
     * 供货实际面积
     */
    private BigDecimal supplyActualArea;

    /**
     * 供货实际货值
     */
    private BigDecimal supplyActualPrice;

    /**
     * 供货实际套数
     */
    private BigDecimal supplyActualNumber;

    /**
     * 供货实际均价
     */
    private BigDecimal supplyActualAvgPrice;

    /**
     * 供货实际日期
     */
    private LocalDateTime supplyActualDate;

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
    public String getSupplyPlanProductTypeMapId() {
        return supplyPlanProductTypeMapId;
    }

    public void setSupplyPlanProductTypeMapId(String supplyPlanProductTypeMapId) {
        this.supplyPlanProductTypeMapId = supplyPlanProductTypeMapId;
    }
    public BigDecimal getSupplyPlanArea() {
        return supplyPlanArea;
    }

    public void setSupplyPlanArea(BigDecimal supplyPlanArea) {
        this.supplyPlanArea = supplyPlanArea;
    }
    public BigDecimal getSupplyPlanPrice() {
        return supplyPlanPrice;
    }

    public void setSupplyPlanPrice(BigDecimal supplyPlanPrice) {
        this.supplyPlanPrice = supplyPlanPrice;
    }
    public LocalDateTime getSupplyPlanDate() {
        return supplyPlanDate;
    }

    public void setSupplyPlanDate(LocalDateTime supplyPlanDate) {
        this.supplyPlanDate = supplyPlanDate;
    }
    public BigDecimal getSupplyActualArea() {
        return supplyActualArea;
    }

    public void setSupplyActualArea(BigDecimal supplyActualArea) {
        this.supplyActualArea = supplyActualArea;
    }
    public BigDecimal getSupplyActualPrice() {
        return supplyActualPrice;
    }

    public void setSupplyActualPrice(BigDecimal supplyActualPrice) {
        this.supplyActualPrice = supplyActualPrice;
    }
    public BigDecimal getSupplyActualNumber() {
        return supplyActualNumber;
    }

    public void setSupplyActualNumber(BigDecimal supplyActualNumber) {
        this.supplyActualNumber = supplyActualNumber;
    }
    public BigDecimal getSupplyActualAvgPrice() {
        return supplyActualAvgPrice;
    }

    public void setSupplyActualAvgPrice(BigDecimal supplyActualAvgPrice) {
        this.supplyActualAvgPrice = supplyActualAvgPrice;
    }
    public LocalDateTime getSupplyActualDate() {
        return supplyActualDate;
    }

    public void setSupplyActualDate(LocalDateTime supplyActualDate) {
        this.supplyActualDate = supplyActualDate;
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
        return "BoSupplyPlanData{" +
        "id=" + id +
        ", supplyPlanProductTypeMapId=" + supplyPlanProductTypeMapId +
        ", supplyPlanArea=" + supplyPlanArea +
        ", supplyPlanPrice=" + supplyPlanPrice +
        ", supplyPlanDate=" + supplyPlanDate +
        ", supplyActualArea=" + supplyActualArea +
        ", supplyActualPrice=" + supplyActualPrice +
        ", supplyActualNumber=" + supplyActualNumber +
        ", supplyActualAvgPrice=" + supplyActualAvgPrice +
        ", supplyActualDate=" + supplyActualDate +
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
