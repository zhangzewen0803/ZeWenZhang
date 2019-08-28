package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * TUTOU-土地信息表-主表
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThLandinformationTb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 土地ID
     */
    private Integer landId;

    /**
     * 地块名称/项目名称
     */
    private String projectName;

    /**
     * 取得方式-合作方式表id
     */
    private Integer purposeId;

    /**
     * 总建筑面积（m2）
     */
    private BigDecimal totalConstruction;

    /**
     * 容积率
     */
    private BigDecimal volumetricRate;

    /**
     * 签约货值
     */
    private BigDecimal contractValue;

    /**
     * 货值
     */
    private BigDecimal landVale;

    /**
     * 土地状态 默认为1
     */
    private Integer stateId;

    /**
     * 负责人/项目负责人
     */
    private String personCharge;

    /**
     * 最近跟进日期
     */
    private LocalDateTime followTime;

    /**
     * 区域code---CRG3
     */
    private String regionCode;

    /**
     * 城市code--CRG4
     */
    private String cityCode;

    /**
     * 预留字段
     */
    private Integer steateId;

    /**
     * 坐标
     */
    private String coordinate;

    /**
     * 区域投决会-总货值
     */
    private BigDecimal regionTotalValue;

    /**
     * 集团投决会-总货值
     */
    private BigDecimal groupTotalValue;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 土地性质id
     */
    private String landNatureId;

    /**
     * 确权时间
     */
    private LocalDateTime confirmTime;

    /**
     * 当前系统登录用户名
     */
    private String userName;

    /**
     * 是否签约 默认1  1-否 0-是
     */
    private Integer isContract;

    /**
     * 0-待审批(可以修改数据);1-审批中(不可修改数据)
     */
    private Integer isApprove;

    /**
     * 签约时间
     */
    private LocalDateTime contractTime;

    /**
     * 是否是历史数据：0-是;1-不是
     */
    private Integer isHistory;

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
    public Integer getLandId() {
        return landId;
    }

    public void setLandId(Integer landId) {
        this.landId = landId;
    }
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public Integer getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(Integer purposeId) {
        this.purposeId = purposeId;
    }
    public BigDecimal getTotalConstruction() {
        return totalConstruction;
    }

    public void setTotalConstruction(BigDecimal totalConstruction) {
        this.totalConstruction = totalConstruction;
    }
    public BigDecimal getVolumetricRate() {
        return volumetricRate;
    }

    public void setVolumetricRate(BigDecimal volumetricRate) {
        this.volumetricRate = volumetricRate;
    }
    public BigDecimal getContractValue() {
        return contractValue;
    }

    public void setContractValue(BigDecimal contractValue) {
        this.contractValue = contractValue;
    }
    public BigDecimal getLandVale() {
        return landVale;
    }

    public void setLandVale(BigDecimal landVale) {
        this.landVale = landVale;
    }
    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }
    public String getPersonCharge() {
        return personCharge;
    }

    public void setPersonCharge(String personCharge) {
        this.personCharge = personCharge;
    }
    public LocalDateTime getFollowTime() {
        return followTime;
    }

    public void setFollowTime(LocalDateTime followTime) {
        this.followTime = followTime;
    }
    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
    public Integer getSteateId() {
        return steateId;
    }

    public void setSteateId(Integer steateId) {
        this.steateId = steateId;
    }
    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }
    public BigDecimal getRegionTotalValue() {
        return regionTotalValue;
    }

    public void setRegionTotalValue(BigDecimal regionTotalValue) {
        this.regionTotalValue = regionTotalValue;
    }
    public BigDecimal getGroupTotalValue() {
        return groupTotalValue;
    }

    public void setGroupTotalValue(BigDecimal groupTotalValue) {
        this.groupTotalValue = groupTotalValue;
    }
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public String getLandNatureId() {
        return landNatureId;
    }

    public void setLandNatureId(String landNatureId) {
        this.landNatureId = landNatureId;
    }
    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Integer getIsContract() {
        return isContract;
    }

    public void setIsContract(Integer isContract) {
        this.isContract = isContract;
    }
    public Integer getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(Integer isApprove) {
        this.isApprove = isApprove;
    }
    public LocalDateTime getContractTime() {
        return contractTime;
    }

    public void setContractTime(LocalDateTime contractTime) {
        this.contractTime = contractTime;
    }
    public Integer getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(Integer isHistory) {
        this.isHistory = isHistory;
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
        return "TutouThLandinformationTb{" +
        "id=" + id +
        ", landId=" + landId +
        ", projectName=" + projectName +
        ", purposeId=" + purposeId +
        ", totalConstruction=" + totalConstruction +
        ", volumetricRate=" + volumetricRate +
        ", contractValue=" + contractValue +
        ", landVale=" + landVale +
        ", stateId=" + stateId +
        ", personCharge=" + personCharge +
        ", followTime=" + followTime +
        ", regionCode=" + regionCode +
        ", cityCode=" + cityCode +
        ", steateId=" + steateId +
        ", coordinate=" + coordinate +
        ", regionTotalValue=" + regionTotalValue +
        ", groupTotalValue=" + groupTotalValue +
        ", regionName=" + regionName +
        ", cityName=" + cityName +
        ", landNatureId=" + landNatureId +
        ", confirmTime=" + confirmTime +
        ", userName=" + userName +
        ", isContract=" + isContract +
        ", isApprove=" + isApprove +
        ", contractTime=" + contractTime +
        ", isHistory=" + isHistory +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
