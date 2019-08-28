package com.tahoecn.bo.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * TUTOU-地块信息-新
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public class TutouThLandareaNewTb implements Serializable {

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
     * 地块编号
     */
    private String landNum;

    /**
     * 用地面积(m²)
     */
    private BigDecimal landArea;

    /**
     * 建设用地面积(m²)
     */
    private BigDecimal constructionUseLand;

    /**
     * 用地性质---取土地性质表
     */
    private Integer landNatureId;

    /**
     * 容积率(%)
     */
    private BigDecimal abovegroundPlanning;

    /**
     * 建筑密度
     */
    private String buildingLimit;

    /**
     * 绿地率(%)
     */
    private BigDecimal greeningRate;

    /**
     * 控高（m）
     */
    private String planningDensity;

    /**
     * 总建筑面积A(m²)
     */
    private BigDecimal constructionAreaA;

    /**
     * 计容建面（m²）
     */
    private BigDecimal planningRate;

    /**
     * 配套
     */
    private String matching;

    /**
     * 车位配比
     */
    private String parkingRatio;

    /**
     * 交通
     */
    private String traffic;

    /**
     * 大市政
     */
    private String municipal;

    /**
     * 四至
     */
    private String location;

    /**
     * 业态配比
     */
    private String formatRatio;

    /**
     * 地上可售住宅-独栋别墅
     */
    private BigDecimal singleFamilyVillas;

    /**
     * 地上可售住宅-合院别墅
     */
    private BigDecimal courtyardHouse;

    /**
     * 地上可售住宅-叠拼别墅
     */
    private BigDecimal stackedTownhouse;

    /**
     * 地上可售住宅-洋房
     */
    private BigDecimal villa;

    /**
     * 地上可售住宅-普通住宅
     */
    private BigDecimal ordinaryHouse;

    /**
     * 地上可售住宅-超高层
     */
    private BigDecimal superHighRise;

    /**
     * 地上可售住宅-保障房（有偿）
     */
    private BigDecimal guaranteeRoom;

    /**
     * 地上可售住宅-回迁房（无偿）
     */
    private BigDecimal returnRoom;

    /**
     * 地上可售住宅-其他
     */
    private BigDecimal onGroundOther;

    /**
     * 地下可售部分-住宅人防车库
     */
    private BigDecimal residentialAirDefen;

    /**
     * 地下可售部分-普宅人防车库
     */
    private BigDecimal mansionAirDefense;

    /**
     * 地下可售部分-非普宅人防车库
     */
    private BigDecimal nonCommonHouseCivil;

    /**
     * 地下可售部分-住宅非人防车位
     */
    private BigDecimal noResidentialAirDefen;

    /**
     * 地下可售部分-普宅非人防车库
     */
    private BigDecimal mansionNonAirDefense;

    /**
     * 地下可售部分-非普宅非人防车库
     */
    private BigDecimal noMansionAirDefense;

    /**
     * 地下可售部分-商办人防车库
     */
    private BigDecimal commercialCivilAirDefe;

    /**
     * 地下可售部分-商办非人防车库
     */
    private BigDecimal commercialNonCivilAirDefe;

    /**
     * 地下可售部分-地下商业（可售）
     */
    private BigDecimal undergroundCommer;

    /**
     * 地下可售部分-地下办公（可售）
     */
    private BigDecimal undergroundOffice;

    /**
     * 地下可售部分-地下仓储（可售）
     */
    private BigDecimal undergroundStorage;

    /**
     * 地下可售部分-设备用房（不可售）
     */
    private BigDecimal installationRoom;

    /**
     * 地下可售部分-其他（不可售）
     */
    private BigDecimal undergroundOther;

    /**
     * 地上可售商办-商业街
     */
    private BigDecimal commercialStreet;

    /**
     * 地上可售商办-写字楼
     */
    private BigDecimal officeBuilding;

    /**
     * 地上可售商办-平层公寓
     */
    private BigDecimal flatFlat;

    /**
     * 地上可售商办-LOFT
     */
    private BigDecimal loft;

    /**
     * 地上可售商办-其他
     */
    private BigDecimal onGroundBusinessOther;

    /**
     * 地上自持-住宅（自持）
     */
    private BigDecimal residence;

    /**
     * 地上自持-酒店
     */
    private BigDecimal hotel;

    /**
     * 地上自持-购物中心
     */
    private BigDecimal shoppingMall;

    /**
     * 地上自持-写字楼
     */
    private BigDecimal holdingOfficeBuilding;

    /**
     * 地上自持-其他
     */
    private BigDecimal holdingOther;

    /**
     * 地下自持部分-人防车库（自持）
     */
    private BigDecimal civilAirDefenseGarag;

    /**
     * 地下自持部分-非人防车库（自持）
     */
    private BigDecimal nonCivilAirDefenseGarag;

    /**
     * 地下自持部分-地下其他
     */
    private BigDecimal holdingDownOther;

    /**
     * 地上公共配套-公建配套
     */
    private BigDecimal publicConstruction;

    /**
     * 地上公共配套-学校
     */
    private BigDecimal school;

    /**
     * 地上公共配套-医院
     */
    private BigDecimal hospital;

    /**
     * 地上公共配套-其他
     */
    private BigDecimal publicMatchingOther;

    /**
     * 赠送面积-地上赠送面积
     */
    private BigDecimal acreageOnGroundArea;

    /**
     * 赠送面积-地下赠送面积
     */
    private BigDecimal undergroundGrantArea;

    /**
     * 赠送面积-其他
     */
    private BigDecimal giftAreaOther;

    /**
     * 1-报意向;2-区域投决会;3-集团投决会;
     */
    private Integer type;

    /**
     * 是否确权  0未确权  1确权中  2已确权
     */
    private Integer confirmStatus;

    /**
     * 地块确权金额
     */
    private BigDecimal confirmValue;

    /**
     * 确权时间
     */
    private LocalDateTime confirmDate;

    /**
     * 确权人
     */
    private String confirmPerson;

    /**
     * 确权批次
     */
    private Integer confirmNum;

    /**
     * 本次确权股份份额(%)
     */
    private BigDecimal currentSharesRate;

    /**
     * 是否填写运营方案 1-否 2-是
     */
    private Integer isWrite;

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
    public String getLandNum() {
        return landNum;
    }

    public void setLandNum(String landNum) {
        this.landNum = landNum;
    }
    public BigDecimal getLandArea() {
        return landArea;
    }

    public void setLandArea(BigDecimal landArea) {
        this.landArea = landArea;
    }
    public BigDecimal getConstructionUseLand() {
        return constructionUseLand;
    }

    public void setConstructionUseLand(BigDecimal constructionUseLand) {
        this.constructionUseLand = constructionUseLand;
    }
    public Integer getLandNatureId() {
        return landNatureId;
    }

    public void setLandNatureId(Integer landNatureId) {
        this.landNatureId = landNatureId;
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
    public BigDecimal getGreeningRate() {
        return greeningRate;
    }

    public void setGreeningRate(BigDecimal greeningRate) {
        this.greeningRate = greeningRate;
    }
    public String getPlanningDensity() {
        return planningDensity;
    }

    public void setPlanningDensity(String planningDensity) {
        this.planningDensity = planningDensity;
    }
    public BigDecimal getConstructionAreaA() {
        return constructionAreaA;
    }

    public void setConstructionAreaA(BigDecimal constructionAreaA) {
        this.constructionAreaA = constructionAreaA;
    }
    public BigDecimal getPlanningRate() {
        return planningRate;
    }

    public void setPlanningRate(BigDecimal planningRate) {
        this.planningRate = planningRate;
    }
    public String getMatching() {
        return matching;
    }

    public void setMatching(String matching) {
        this.matching = matching;
    }
    public String getParkingRatio() {
        return parkingRatio;
    }

    public void setParkingRatio(String parkingRatio) {
        this.parkingRatio = parkingRatio;
    }
    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }
    public String getMunicipal() {
        return municipal;
    }

    public void setMunicipal(String municipal) {
        this.municipal = municipal;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getFormatRatio() {
        return formatRatio;
    }

    public void setFormatRatio(String formatRatio) {
        this.formatRatio = formatRatio;
    }
    public BigDecimal getSingleFamilyVillas() {
        return singleFamilyVillas;
    }

    public void setSingleFamilyVillas(BigDecimal singleFamilyVillas) {
        this.singleFamilyVillas = singleFamilyVillas;
    }
    public BigDecimal getCourtyardHouse() {
        return courtyardHouse;
    }

    public void setCourtyardHouse(BigDecimal courtyardHouse) {
        this.courtyardHouse = courtyardHouse;
    }
    public BigDecimal getStackedTownhouse() {
        return stackedTownhouse;
    }

    public void setStackedTownhouse(BigDecimal stackedTownhouse) {
        this.stackedTownhouse = stackedTownhouse;
    }
    public BigDecimal getVilla() {
        return villa;
    }

    public void setVilla(BigDecimal villa) {
        this.villa = villa;
    }
    public BigDecimal getOrdinaryHouse() {
        return ordinaryHouse;
    }

    public void setOrdinaryHouse(BigDecimal ordinaryHouse) {
        this.ordinaryHouse = ordinaryHouse;
    }
    public BigDecimal getSuperHighRise() {
        return superHighRise;
    }

    public void setSuperHighRise(BigDecimal superHighRise) {
        this.superHighRise = superHighRise;
    }
    public BigDecimal getGuaranteeRoom() {
        return guaranteeRoom;
    }

    public void setGuaranteeRoom(BigDecimal guaranteeRoom) {
        this.guaranteeRoom = guaranteeRoom;
    }
    public BigDecimal getReturnRoom() {
        return returnRoom;
    }

    public void setReturnRoom(BigDecimal returnRoom) {
        this.returnRoom = returnRoom;
    }
    public BigDecimal getOnGroundOther() {
        return onGroundOther;
    }

    public void setOnGroundOther(BigDecimal onGroundOther) {
        this.onGroundOther = onGroundOther;
    }
    public BigDecimal getResidentialAirDefen() {
        return residentialAirDefen;
    }

    public void setResidentialAirDefen(BigDecimal residentialAirDefen) {
        this.residentialAirDefen = residentialAirDefen;
    }
    public BigDecimal getMansionAirDefense() {
        return mansionAirDefense;
    }

    public void setMansionAirDefense(BigDecimal mansionAirDefense) {
        this.mansionAirDefense = mansionAirDefense;
    }
    public BigDecimal getNonCommonHouseCivil() {
        return nonCommonHouseCivil;
    }

    public void setNonCommonHouseCivil(BigDecimal nonCommonHouseCivil) {
        this.nonCommonHouseCivil = nonCommonHouseCivil;
    }
    public BigDecimal getNoResidentialAirDefen() {
        return noResidentialAirDefen;
    }

    public void setNoResidentialAirDefen(BigDecimal noResidentialAirDefen) {
        this.noResidentialAirDefen = noResidentialAirDefen;
    }
    public BigDecimal getMansionNonAirDefense() {
        return mansionNonAirDefense;
    }

    public void setMansionNonAirDefense(BigDecimal mansionNonAirDefense) {
        this.mansionNonAirDefense = mansionNonAirDefense;
    }
    public BigDecimal getNoMansionAirDefense() {
        return noMansionAirDefense;
    }

    public void setNoMansionAirDefense(BigDecimal noMansionAirDefense) {
        this.noMansionAirDefense = noMansionAirDefense;
    }
    public BigDecimal getCommercialCivilAirDefe() {
        return commercialCivilAirDefe;
    }

    public void setCommercialCivilAirDefe(BigDecimal commercialCivilAirDefe) {
        this.commercialCivilAirDefe = commercialCivilAirDefe;
    }
    public BigDecimal getCommercialNonCivilAirDefe() {
        return commercialNonCivilAirDefe;
    }

    public void setCommercialNonCivilAirDefe(BigDecimal commercialNonCivilAirDefe) {
        this.commercialNonCivilAirDefe = commercialNonCivilAirDefe;
    }
    public BigDecimal getUndergroundCommer() {
        return undergroundCommer;
    }

    public void setUndergroundCommer(BigDecimal undergroundCommer) {
        this.undergroundCommer = undergroundCommer;
    }
    public BigDecimal getUndergroundOffice() {
        return undergroundOffice;
    }

    public void setUndergroundOffice(BigDecimal undergroundOffice) {
        this.undergroundOffice = undergroundOffice;
    }
    public BigDecimal getUndergroundStorage() {
        return undergroundStorage;
    }

    public void setUndergroundStorage(BigDecimal undergroundStorage) {
        this.undergroundStorage = undergroundStorage;
    }
    public BigDecimal getInstallationRoom() {
        return installationRoom;
    }

    public void setInstallationRoom(BigDecimal installationRoom) {
        this.installationRoom = installationRoom;
    }
    public BigDecimal getUndergroundOther() {
        return undergroundOther;
    }

    public void setUndergroundOther(BigDecimal undergroundOther) {
        this.undergroundOther = undergroundOther;
    }
    public BigDecimal getCommercialStreet() {
        return commercialStreet;
    }

    public void setCommercialStreet(BigDecimal commercialStreet) {
        this.commercialStreet = commercialStreet;
    }
    public BigDecimal getOfficeBuilding() {
        return officeBuilding;
    }

    public void setOfficeBuilding(BigDecimal officeBuilding) {
        this.officeBuilding = officeBuilding;
    }
    public BigDecimal getFlatFlat() {
        return flatFlat;
    }

    public void setFlatFlat(BigDecimal flatFlat) {
        this.flatFlat = flatFlat;
    }
    public BigDecimal getLoft() {
        return loft;
    }

    public void setLoft(BigDecimal loft) {
        this.loft = loft;
    }
    public BigDecimal getOnGroundBusinessOther() {
        return onGroundBusinessOther;
    }

    public void setOnGroundBusinessOther(BigDecimal onGroundBusinessOther) {
        this.onGroundBusinessOther = onGroundBusinessOther;
    }
    public BigDecimal getResidence() {
        return residence;
    }

    public void setResidence(BigDecimal residence) {
        this.residence = residence;
    }
    public BigDecimal getHotel() {
        return hotel;
    }

    public void setHotel(BigDecimal hotel) {
        this.hotel = hotel;
    }
    public BigDecimal getShoppingMall() {
        return shoppingMall;
    }

    public void setShoppingMall(BigDecimal shoppingMall) {
        this.shoppingMall = shoppingMall;
    }
    public BigDecimal getHoldingOfficeBuilding() {
        return holdingOfficeBuilding;
    }

    public void setHoldingOfficeBuilding(BigDecimal holdingOfficeBuilding) {
        this.holdingOfficeBuilding = holdingOfficeBuilding;
    }
    public BigDecimal getHoldingOther() {
        return holdingOther;
    }

    public void setHoldingOther(BigDecimal holdingOther) {
        this.holdingOther = holdingOther;
    }
    public BigDecimal getCivilAirDefenseGarag() {
        return civilAirDefenseGarag;
    }

    public void setCivilAirDefenseGarag(BigDecimal civilAirDefenseGarag) {
        this.civilAirDefenseGarag = civilAirDefenseGarag;
    }
    public BigDecimal getNonCivilAirDefenseGarag() {
        return nonCivilAirDefenseGarag;
    }

    public void setNonCivilAirDefenseGarag(BigDecimal nonCivilAirDefenseGarag) {
        this.nonCivilAirDefenseGarag = nonCivilAirDefenseGarag;
    }
    public BigDecimal getHoldingDownOther() {
        return holdingDownOther;
    }

    public void setHoldingDownOther(BigDecimal holdingDownOther) {
        this.holdingDownOther = holdingDownOther;
    }
    public BigDecimal getPublicConstruction() {
        return publicConstruction;
    }

    public void setPublicConstruction(BigDecimal publicConstruction) {
        this.publicConstruction = publicConstruction;
    }
    public BigDecimal getSchool() {
        return school;
    }

    public void setSchool(BigDecimal school) {
        this.school = school;
    }
    public BigDecimal getHospital() {
        return hospital;
    }

    public void setHospital(BigDecimal hospital) {
        this.hospital = hospital;
    }
    public BigDecimal getPublicMatchingOther() {
        return publicMatchingOther;
    }

    public void setPublicMatchingOther(BigDecimal publicMatchingOther) {
        this.publicMatchingOther = publicMatchingOther;
    }
    public BigDecimal getAcreageOnGroundArea() {
        return acreageOnGroundArea;
    }

    public void setAcreageOnGroundArea(BigDecimal acreageOnGroundArea) {
        this.acreageOnGroundArea = acreageOnGroundArea;
    }
    public BigDecimal getUndergroundGrantArea() {
        return undergroundGrantArea;
    }

    public void setUndergroundGrantArea(BigDecimal undergroundGrantArea) {
        this.undergroundGrantArea = undergroundGrantArea;
    }
    public BigDecimal getGiftAreaOther() {
        return giftAreaOther;
    }

    public void setGiftAreaOther(BigDecimal giftAreaOther) {
        this.giftAreaOther = giftAreaOther;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }
    public BigDecimal getConfirmValue() {
        return confirmValue;
    }

    public void setConfirmValue(BigDecimal confirmValue) {
        this.confirmValue = confirmValue;
    }
    public LocalDateTime getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(LocalDateTime confirmDate) {
        this.confirmDate = confirmDate;
    }
    public String getConfirmPerson() {
        return confirmPerson;
    }

    public void setConfirmPerson(String confirmPerson) {
        this.confirmPerson = confirmPerson;
    }
    public Integer getConfirmNum() {
        return confirmNum;
    }

    public void setConfirmNum(Integer confirmNum) {
        this.confirmNum = confirmNum;
    }
    public BigDecimal getCurrentSharesRate() {
        return currentSharesRate;
    }

    public void setCurrentSharesRate(BigDecimal currentSharesRate) {
        this.currentSharesRate = currentSharesRate;
    }
    public Integer getIsWrite() {
        return isWrite;
    }

    public void setIsWrite(Integer isWrite) {
        this.isWrite = isWrite;
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
        return "TutouThLandareaNewTb{" +
        "id=" + id +
        ", sourceId=" + sourceId +
        ", landId=" + landId +
        ", landNum=" + landNum +
        ", landArea=" + landArea +
        ", constructionUseLand=" + constructionUseLand +
        ", landNatureId=" + landNatureId +
        ", abovegroundPlanning=" + abovegroundPlanning +
        ", buildingLimit=" + buildingLimit +
        ", greeningRate=" + greeningRate +
        ", planningDensity=" + planningDensity +
        ", constructionAreaA=" + constructionAreaA +
        ", planningRate=" + planningRate +
        ", matching=" + matching +
        ", parkingRatio=" + parkingRatio +
        ", traffic=" + traffic +
        ", municipal=" + municipal +
        ", location=" + location +
        ", formatRatio=" + formatRatio +
        ", singleFamilyVillas=" + singleFamilyVillas +
        ", courtyardHouse=" + courtyardHouse +
        ", stackedTownhouse=" + stackedTownhouse +
        ", villa=" + villa +
        ", ordinaryHouse=" + ordinaryHouse +
        ", superHighRise=" + superHighRise +
        ", guaranteeRoom=" + guaranteeRoom +
        ", returnRoom=" + returnRoom +
        ", onGroundOther=" + onGroundOther +
        ", residentialAirDefen=" + residentialAirDefen +
        ", mansionAirDefense=" + mansionAirDefense +
        ", nonCommonHouseCivil=" + nonCommonHouseCivil +
        ", noResidentialAirDefen=" + noResidentialAirDefen +
        ", mansionNonAirDefense=" + mansionNonAirDefense +
        ", noMansionAirDefense=" + noMansionAirDefense +
        ", commercialCivilAirDefe=" + commercialCivilAirDefe +
        ", commercialNonCivilAirDefe=" + commercialNonCivilAirDefe +
        ", undergroundCommer=" + undergroundCommer +
        ", undergroundOffice=" + undergroundOffice +
        ", undergroundStorage=" + undergroundStorage +
        ", installationRoom=" + installationRoom +
        ", undergroundOther=" + undergroundOther +
        ", commercialStreet=" + commercialStreet +
        ", officeBuilding=" + officeBuilding +
        ", flatFlat=" + flatFlat +
        ", loft=" + loft +
        ", onGroundBusinessOther=" + onGroundBusinessOther +
        ", residence=" + residence +
        ", hotel=" + hotel +
        ", shoppingMall=" + shoppingMall +
        ", holdingOfficeBuilding=" + holdingOfficeBuilding +
        ", holdingOther=" + holdingOther +
        ", civilAirDefenseGarag=" + civilAirDefenseGarag +
        ", nonCivilAirDefenseGarag=" + nonCivilAirDefenseGarag +
        ", holdingDownOther=" + holdingDownOther +
        ", publicConstruction=" + publicConstruction +
        ", school=" + school +
        ", hospital=" + hospital +
        ", publicMatchingOther=" + publicMatchingOther +
        ", acreageOnGroundArea=" + acreageOnGroundArea +
        ", undergroundGrantArea=" + undergroundGrantArea +
        ", giftAreaOther=" + giftAreaOther +
        ", type=" + type +
        ", confirmStatus=" + confirmStatus +
        ", confirmValue=" + confirmValue +
        ", confirmDate=" + confirmDate +
        ", confirmPerson=" + confirmPerson +
        ", confirmNum=" + confirmNum +
        ", currentSharesRate=" + currentSharesRate +
        ", isWrite=" + isWrite +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
