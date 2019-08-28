package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * TUTOU-土地面积表
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThLandareaTb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 原表ID
     */
    private Integer sourceId;

    /**
     * 土地ID
     */
    private Integer landId;

    /**
     * 总用地面积(m²)
     */
    private BigDecimal totalArea;

    /**
     * 建设用地面积(m²)
     */
    private BigDecimal constructionLand;

    /**
     * 容积率(%)
     */
    private BigDecimal abovegroundPlanning;

    /**
     * 建筑密度
     */
    private String buildingLimit;

    /**
     * 其1:地上可售住宅
     */
    private BigDecimal gaugeDensity;

    /**
     * 控高（m）
     */
    private String planningDensity;

    /**
     * 其2:地上可售商办
     */
    private BigDecimal gaugeRate;

    /**
     * 计容建面（m²）
     */
    private BigDecimal planningRate;

    /**
     * 绿地率(%)
     */
    private BigDecimal greeningRate;

    /**
     * 用地性质---取土地性质表
     */
    private Integer commerciaArea;

    /**
     * 其5:地下可售部分
     */
    private BigDecimal guaranteeRoom;

    /**
     * 其3:地上自持
     */
    private BigDecimal publicRentalArea;

    /**
     * 其6:地下自持部分
     */
    private BigDecimal limitedRoomArea;

    /**
     * 其7:赠送面积（m²）
     */
    private BigDecimal propertyRightsHousing;

    /**
     * 其8:其他
     */
    private BigDecimal otherHousing;

    /**
     * 其4:地上公共配套
     */
    private BigDecimal otherArea;

    /**
     * 1-报意向;2-区域投决会;3-集团投决会;
     */
    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }
    public Integer getLandId() {
        return landId;
    }

    public void setLandId(Integer landId) {
        this.landId = landId;
    }
    public BigDecimal getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(BigDecimal totalArea) {
        this.totalArea = totalArea;
    }
    public BigDecimal getConstructionLand() {
        return constructionLand;
    }

    public void setConstructionLand(BigDecimal constructionLand) {
        this.constructionLand = constructionLand;
    }
    public BigDecimal getAbovegroundPlanning() {
        return abovegroundPlanning;
    }

    public void setAbovegroundPlanning(BigDecimal abovegroundPlanning) {
        this.abovegroundPlanning = abovegroundPlanning;
    }
    public String getBuildingLimit() {
        return buildingLimit;
    }

    public void setBuildingLimit(String buildingLimit) {
        this.buildingLimit = buildingLimit;
    }
    public BigDecimal getGaugeDensity() {
        return gaugeDensity;
    }

    public void setGaugeDensity(BigDecimal gaugeDensity) {
        this.gaugeDensity = gaugeDensity;
    }
    public String getPlanningDensity() {
        return planningDensity;
    }

    public void setPlanningDensity(String planningDensity) {
        this.planningDensity = planningDensity;
    }
    public BigDecimal getGaugeRate() {
        return gaugeRate;
    }

    public void setGaugeRate(BigDecimal gaugeRate) {
        this.gaugeRate = gaugeRate;
    }
    public BigDecimal getPlanningRate() {
        return planningRate;
    }

    public void setPlanningRate(BigDecimal planningRate) {
        this.planningRate = planningRate;
    }
    public BigDecimal getGreeningRate() {
        return greeningRate;
    }

    public void setGreeningRate(BigDecimal greeningRate) {
        this.greeningRate = greeningRate;
    }
    public Integer getCommerciaArea() {
        return commerciaArea;
    }

    public void setCommerciaArea(Integer commerciaArea) {
        this.commerciaArea = commerciaArea;
    }
    public BigDecimal getGuaranteeRoom() {
        return guaranteeRoom;
    }

    public void setGuaranteeRoom(BigDecimal guaranteeRoom) {
        this.guaranteeRoom = guaranteeRoom;
    }
    public BigDecimal getPublicRentalArea() {
        return publicRentalArea;
    }

    public void setPublicRentalArea(BigDecimal publicRentalArea) {
        this.publicRentalArea = publicRentalArea;
    }
    public BigDecimal getLimitedRoomArea() {
        return limitedRoomArea;
    }

    public void setLimitedRoomArea(BigDecimal limitedRoomArea) {
        this.limitedRoomArea = limitedRoomArea;
    }
    public BigDecimal getPropertyRightsHousing() {
        return propertyRightsHousing;
    }

    public void setPropertyRightsHousing(BigDecimal propertyRightsHousing) {
        this.propertyRightsHousing = propertyRightsHousing;
    }
    public BigDecimal getOtherHousing() {
        return otherHousing;
    }

    public void setOtherHousing(BigDecimal otherHousing) {
        this.otherHousing = otherHousing;
    }
    public BigDecimal getOtherArea() {
        return otherArea;
    }

    public void setOtherArea(BigDecimal otherArea) {
        this.otherArea = otherArea;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TutouThLandareaTb{" +
        "id=" + id +
        ", sourceId=" + sourceId +
        ", landId=" + landId +
        ", totalArea=" + totalArea +
        ", constructionLand=" + constructionLand +
        ", abovegroundPlanning=" + abovegroundPlanning +
        ", buildingLimit=" + buildingLimit +
        ", gaugeDensity=" + gaugeDensity +
        ", planningDensity=" + planningDensity +
        ", gaugeRate=" + gaugeRate +
        ", planningRate=" + planningRate +
        ", greeningRate=" + greeningRate +
        ", commerciaArea=" + commerciaArea +
        ", guaranteeRoom=" + guaranteeRoom +
        ", publicRentalArea=" + publicRentalArea +
        ", limitedRoomArea=" + limitedRoomArea +
        ", propertyRightsHousing=" + propertyRightsHousing +
        ", otherHousing=" + otherHousing +
        ", otherArea=" + otherArea +
        ", type=" + type +
        "}";
    }
}
