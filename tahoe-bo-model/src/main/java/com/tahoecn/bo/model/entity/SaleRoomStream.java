package com.tahoecn.bo.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 房间流水表
 * </p>
 *
 * @author panglx
 * @since 2019-07-03
 */
public class SaleRoomStream implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 房间流水GUID
     */
    private String streamGuid;
    
    /**
     * 房间GUID
     */
    private String roomGuid;

    /**
     * 房间CODE
     */
    private String roomCode;

    /**
     * 建筑面积
     */
    private BigDecimal bldArea;

    /**
     * 建筑单价
     */
    private BigDecimal price;

    /**
     * 房间货值
     */
    private BigDecimal roomTotal;

    /**
     * 更新时间-同步
     */
    private LocalDateTime gxTime;

    /**
     * 更新原因
     */
    private String gxReason;

    /**
     * 面积状态
     */
    private String areaStatus;

    /**
     * 其他款项
     */
    private BigDecimal otherItem;

    /**
     * 是否虚拟房间
     */
    private Integer isVirtualRoom;

    /**
     * 主数据楼栋ID
     */
    private String mainDataBldId;

    /**
     * 主数据业态CODE
     */
    private String mainDataProductTypeCode;
    
    /**
     * 销售状态
     */
    private String status;
    
    /**
     * 实际取证时间
     */
    private LocalDateTime realTakeCardTime;

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

    public String getStreamGuid() {
		return streamGuid;
	}

	public void setStreamGuid(String streamGuid) {
		this.streamGuid = streamGuid;
	}

	public String getRoomGuid() {
        return roomGuid;
    }

    public void setRoomGuid(String roomGuid) {
        this.roomGuid = roomGuid;
    }
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    public BigDecimal getBldArea() {
        return bldArea;
    }

    public void setBldArea(BigDecimal bldArea) {
        this.bldArea = bldArea;
    }
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getRoomTotal() {
        return roomTotal;
    }

    public void setRoomTotal(BigDecimal roomTotal) {
        this.roomTotal = roomTotal;
    }
    public LocalDateTime getGxTime() {
        return gxTime;
    }

    public void setGxTime(LocalDateTime gxTime) {
        this.gxTime = gxTime;
    }
    public String getGxReason() {
        return gxReason;
    }

    public void setGxReason(String gxReason) {
        this.gxReason = gxReason;
    }
    public String getAreaStatus() {
        return areaStatus;
    }

    public void setAreaStatus(String areaStatus) {
        this.areaStatus = areaStatus;
    }
    public BigDecimal getOtherItem() {
        return otherItem;
    }

    public void setOtherItem(BigDecimal otherItem) {
        this.otherItem = otherItem;
    }
    public Integer getIsVirtualRoom() {
        return isVirtualRoom;
    }

    public void setIsVirtualRoom(Integer isVirtualRoom) {
        this.isVirtualRoom = isVirtualRoom;
    }
    public String getMainDataBldId() {
        return mainDataBldId;
    }

    public void setMainDataBldId(String mainDataBldId) {
        this.mainDataBldId = mainDataBldId;
    }
    public String getMainDataProductTypeCode() {
        return mainDataProductTypeCode;
    }

    public void setMainDataProductTypeCode(String mainDataProductTypeCode) {
        this.mainDataProductTypeCode = mainDataProductTypeCode;
    }
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getRealTakeCardTime() {
		return realTakeCardTime;
	}

	public void setRealTakeCardTime(LocalDateTime realTakeCardTime) {
		this.realTakeCardTime = realTakeCardTime;
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
        return "SaleRoomStream{" +
		"streamGuid=" + streamGuid + 
        "roomGuid=" + roomGuid +
        ", roomCode=" + roomCode +
        ", bldArea=" + bldArea +
        ", price=" + price +
        ", roomTotal=" + roomTotal +
        ", gxTime=" + gxTime +
        ", gxReason=" + gxReason +
        ", areaStatus=" + areaStatus +
        ", otherItem=" + otherItem +
        ", isVirtualRoom=" + isVirtualRoom +
        ", mainDataBldId=" + mainDataBldId +
        ", mainDataProductTypeCode=" + mainDataProductTypeCode +
        ", status=" + status +
        ", realTakeCardTime=" + realTakeCardTime +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
