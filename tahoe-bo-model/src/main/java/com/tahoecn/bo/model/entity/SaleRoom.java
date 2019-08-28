package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 房间信息表
 * </p>
 *
 * @author panglx
 * @since 2019-07-03
 */
public class SaleRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 房间GUID
     */
    private String roomGuid;

    /**
     * 单位GUID
     */
    private String buGuid;

    /**
     * 分期GUID
     */
    private String projGuid;

    /**
     * 楼栋GUID
     */
    private String bldGuid;

    /**
     * 主房间GUID
     */
    private String mainRoomGuid;

    /**
     * 单元
     */
    private String unit;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 号码
     */
    private String no;

    /**
     * 房号
     */
    private String room;

    /**
     * 房间CODE
     */
    private String roomCode;

    /**
     * 户型
     */
    private String huXing;

    /**
     * 销售状态
     */
    private String status;

    /**
     * 是否虚拟房间
     */
    private Integer isVirtualRoom;

    /**
     * 建筑面积
     */
    private BigDecimal bldArea;

    /**
     * 套内面积
     */
    private BigDecimal tnArea;

    /**
     * 实际交付日期
     */
    private LocalDateTime blrhDate;

    /**
     * 建筑单价
     */
    private BigDecimal price;

    /**
     * 套内单价
     */
    private BigDecimal tnPrice;

    /**
     * 价格审批底总价
     */
    private BigDecimal baseTotal;

    /**
     * 标准总价
     */
    private BigDecimal total;

    /**
     * 退房日期
     */
    private LocalDateTime tfDate;

    /**
     * 定价面积
     */
    private String djArea;

    /**
     * 交付通知日期
     */
    private LocalDateTime rhDate;

    /**
     * 面积状态
     */
    private String areaStatus;

    /**
     * 房间结构
     */
    private String roomStru;

    /**
     * 产品类型全代码
     */
    private String bProductTypeCode;

    /**
     * 预售建筑面积
     */
    private BigDecimal ysBldArea;

    /**
     * 预售套内面积
     */
    private BigDecimal ysTnArea;

    /**
     * 实测建筑面积
     */
    private BigDecimal scBldArea;

    /**
     * 实测套内面积
     */
    private BigDecimal scTnArea;

    /**
     * 定金
     */
    private BigDecimal earnest;

    /**
     * 预售证号
     */
    private String yszNum;

    /**
     * 标准总价（不含税）
     */
    private BigDecimal noTaxAmount;

    /**
     * 标准总价税额
     */
    private BigDecimal taxAmount;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 底价总价（不含税）
     */
    private BigDecimal noTaxAmountDj;

    /**
     * 底价总价税额
     */
    private BigDecimal taxAmountDj;

    /**
     * 是否主房间
     */
    private Integer isMainRoom;

    /**
     * 主数据业态CODE
     */
    private String mainDataProductTypeCode;

    /**
     * 主数据楼栋ID
     */
    private String mainDataBldId;

    /**
     * 预计取证时间
     */
    private LocalDateTime preTakeCardTime;

    /**
     * 实际取证时间
     */
    private LocalDateTime realTakeCardTime;

    /**
     * 原表创建时间
     */
    private LocalDateTime sourceCreateTime;

    /**
     * 原表更新时间
     */
    private LocalDateTime sourceUpdateTime;

    /**
     * 是否删除（1-是；0-否）
     */
    private Integer isDelete;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

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

    public String getRoomGuid() {
        return roomGuid;
    }

    public void setRoomGuid(String roomGuid) {
        this.roomGuid = roomGuid;
    }
    public String getBuGuid() {
        return buGuid;
    }

    public void setBuGuid(String buGuid) {
        this.buGuid = buGuid;
    }
    public String getProjGuid() {
        return projGuid;
    }

    public void setProjGuid(String projGuid) {
        this.projGuid = projGuid;
    }
    public String getBldGuid() {
        return bldGuid;
    }

    public void setBldGuid(String bldGuid) {
        this.bldGuid = bldGuid;
    }
    public String getMainRoomGuid() {
        return mainRoomGuid;
    }

    public void setMainRoomGuid(String mainRoomGuid) {
        this.mainRoomGuid = mainRoomGuid;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    public String getHuXing() {
        return huXing;
    }

    public void setHuXing(String huXing) {
        this.huXing = huXing;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getIsVirtualRoom() {
        return isVirtualRoom;
    }

    public void setIsVirtualRoom(Integer isVirtualRoom) {
        this.isVirtualRoom = isVirtualRoom;
    }
    public BigDecimal getBldArea() {
        return bldArea;
    }

    public void setBldArea(BigDecimal bldArea) {
        this.bldArea = bldArea;
    }
    public BigDecimal getTnArea() {
        return tnArea;
    }

    public void setTnArea(BigDecimal tnArea) {
        this.tnArea = tnArea;
    }
    public LocalDateTime getBlrhDate() {
        return blrhDate;
    }

    public void setBlrhDate(LocalDateTime blrhDate) {
        this.blrhDate = blrhDate;
    }
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getTnPrice() {
        return tnPrice;
    }

    public void setTnPrice(BigDecimal tnPrice) {
        this.tnPrice = tnPrice;
    }
    public BigDecimal getBaseTotal() {
        return baseTotal;
    }

    public void setBaseTotal(BigDecimal baseTotal) {
        this.baseTotal = baseTotal;
    }
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public LocalDateTime getTfDate() {
        return tfDate;
    }

    public void setTfDate(LocalDateTime tfDate) {
        this.tfDate = tfDate;
    }
    public String getDjArea() {
        return djArea;
    }

    public void setDjArea(String djArea) {
        this.djArea = djArea;
    }
    public LocalDateTime getRhDate() {
        return rhDate;
    }

    public void setRhDate(LocalDateTime rhDate) {
        this.rhDate = rhDate;
    }
    public String getAreaStatus() {
        return areaStatus;
    }

    public void setAreaStatus(String areaStatus) {
        this.areaStatus = areaStatus;
    }
    public String getRoomStru() {
        return roomStru;
    }

    public void setRoomStru(String roomStru) {
        this.roomStru = roomStru;
    }
    public String getbProductTypeCode() {
        return bProductTypeCode;
    }

    public void setbProductTypeCode(String bProductTypeCode) {
        this.bProductTypeCode = bProductTypeCode;
    }
    public BigDecimal getYsBldArea() {
        return ysBldArea;
    }

    public void setYsBldArea(BigDecimal ysBldArea) {
        this.ysBldArea = ysBldArea;
    }
    public BigDecimal getYsTnArea() {
        return ysTnArea;
    }

    public void setYsTnArea(BigDecimal ysTnArea) {
        this.ysTnArea = ysTnArea;
    }
    public BigDecimal getScBldArea() {
        return scBldArea;
    }

    public void setScBldArea(BigDecimal scBldArea) {
        this.scBldArea = scBldArea;
    }
    public BigDecimal getScTnArea() {
        return scTnArea;
    }

    public void setScTnArea(BigDecimal scTnArea) {
        this.scTnArea = scTnArea;
    }
    public BigDecimal getEarnest() {
        return earnest;
    }

    public void setEarnest(BigDecimal earnest) {
        this.earnest = earnest;
    }
    public String getYszNum() {
        return yszNum;
    }

    public void setYszNum(String yszNum) {
        this.yszNum = yszNum;
    }
    public BigDecimal getNoTaxAmount() {
        return noTaxAmount;
    }

    public void setNoTaxAmount(BigDecimal noTaxAmount) {
        this.noTaxAmount = noTaxAmount;
    }
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
    public BigDecimal getNoTaxAmountDj() {
        return noTaxAmountDj;
    }

    public void setNoTaxAmountDj(BigDecimal noTaxAmountDj) {
        this.noTaxAmountDj = noTaxAmountDj;
    }
    public BigDecimal getTaxAmountDj() {
        return taxAmountDj;
    }

    public void setTaxAmountDj(BigDecimal taxAmountDj) {
        this.taxAmountDj = taxAmountDj;
    }
    public Integer getIsMainRoom() {
        return isMainRoom;
    }

    public void setIsMainRoom(Integer isMainRoom) {
        this.isMainRoom = isMainRoom;
    }
    public String getMainDataProductTypeCode() {
        return mainDataProductTypeCode;
    }

    public void setMainDataProductTypeCode(String mainDataProductTypeCode) {
        this.mainDataProductTypeCode = mainDataProductTypeCode;
    }
    public String getMainDataBldId() {
        return mainDataBldId;
    }

    public void setMainDataBldId(String mainDataBldId) {
        this.mainDataBldId = mainDataBldId;
    }
    public LocalDateTime getPreTakeCardTime() {
        return preTakeCardTime;
    }

    public void setPreTakeCardTime(LocalDateTime preTakeCardTime) {
        this.preTakeCardTime = preTakeCardTime;
    }
    public LocalDateTime getRealTakeCardTime() {
        return realTakeCardTime;
    }

    public void setRealTakeCardTime(LocalDateTime realTakeCardTime) {
        this.realTakeCardTime = realTakeCardTime;
    }
    public LocalDateTime getSourceCreateTime() {
        return sourceCreateTime;
    }

    public void setSourceCreateTime(LocalDateTime sourceCreateTime) {
        this.sourceCreateTime = sourceCreateTime;
    }
    public LocalDateTime getSourceUpdateTime() {
        return sourceUpdateTime;
    }

    public void setSourceUpdateTime(LocalDateTime sourceUpdateTime) {
        this.sourceUpdateTime = sourceUpdateTime;
    }
    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
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
        return "SaleRoom{" +
        "roomGuid=" + roomGuid +
        ", buGuid=" + buGuid +
        ", projGuid=" + projGuid +
        ", bldGuid=" + bldGuid +
        ", mainRoomGuid=" + mainRoomGuid +
        ", unit=" + unit +
        ", floor=" + floor +
        ", no=" + no +
        ", room=" + room +
        ", roomCode=" + roomCode +
        ", huXing=" + huXing +
        ", status=" + status +
        ", isVirtualRoom=" + isVirtualRoom +
        ", bldArea=" + bldArea +
        ", tnArea=" + tnArea +
        ", blrhDate=" + blrhDate +
        ", price=" + price +
        ", tnPrice=" + tnPrice +
        ", baseTotal=" + baseTotal +
        ", total=" + total +
        ", tfDate=" + tfDate +
        ", djArea=" + djArea +
        ", rhDate=" + rhDate +
        ", areaStatus=" + areaStatus +
        ", roomStru=" + roomStru +
        ", bProductTypeCode=" + bProductTypeCode +
        ", ysBldArea=" + ysBldArea +
        ", ysTnArea=" + ysTnArea +
        ", scBldArea=" + scBldArea +
        ", scTnArea=" + scTnArea +
        ", earnest=" + earnest +
        ", yszNum=" + yszNum +
        ", noTaxAmount=" + noTaxAmount +
        ", taxAmount=" + taxAmount +
        ", taxRate=" + taxRate +
        ", noTaxAmountDj=" + noTaxAmountDj +
        ", taxAmountDj=" + taxAmountDj +
        ", isMainRoom=" + isMainRoom +
        ", mainDataProductTypeCode=" + mainDataProductTypeCode +
        ", mainDataBldId=" + mainDataBldId +
        ", preTakeCardTime=" + preTakeCardTime +
        ", realTakeCardTime=" + realTakeCardTime +
        ", sourceCreateTime=" + sourceCreateTime +
        ", sourceUpdateTime=" + sourceUpdateTime +
        ", isDelete=" + isDelete +
        ", deleteTime=" + deleteTime +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
