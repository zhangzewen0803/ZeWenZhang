package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 经营概览 - 面积
 *
 * @author panglx
 * @date 2019-7-2
 */
@ApiModel("经营概览 - 面积")
@Data
public class ManageOverviewAreaVO implements Serializable {

    /**
     * 组织ID
     */
    @ApiModelProperty("组织ID")
    private String orgId;

    /**
     * 组织名称
     */
    @ApiModelProperty("组织名称")
    private String orgName;

    /**
     * 组织类型
     */
    @ApiModelProperty("组织类型")
    private String orgType;

    /**
     * 父组织ID
     */
    @ApiModelProperty("父组织ID")
    private String parentOrgId;

    /**
     * 父组织名称
     */
    @ApiModelProperty("父组织名称")
    private String parentOrgName;

    /**
     * 全盘动态可售建筑面积 
     */
    @ApiModelProperty("全盘动态可售建筑面积")
    private BigDecimal overallSaleArea;

    /**
     * 投资决策会可售建筑面积
     */
    @ApiModelProperty("投资决策会可售建筑面积")
    private BigDecimal investDecisionSaleArea;

    /**
     * 经营决策会可售建筑面积
     */
    @ApiModelProperty("经营决策会可售建筑面积")
    private BigDecimal manageDecisionSaleArea;

    /**
     * 未开发（土储）面积
     */
    @ApiModelProperty("未开发（土储）面积")
    private BigDecimal unDevelopArea;

    /**
     * 未开工面积
     */
    @ApiModelProperty("未开工面积")
    private BigDecimal unStartArea;

    /**
     * 已开工未达形象进度面积
     */
    @ApiModelProperty("已开工未达形象进度面积")
    private BigDecimal unReachImgGoalArea;

    /**
     * 已达形象进度未取证面积
     */
    @ApiModelProperty("已达形象进度未取证面积")
    private BigDecimal unGetCardArea;

    /**
     * 已取证未售（存货）面积
     */
    @ApiModelProperty("已取证未售（存货）面积")
    private BigDecimal unSaleArea;

    /**
     * 地上总实际建筑面积
     */
    @ApiModelProperty("地上总实际建筑面积")
    private BigDecimal totalActualAreaAbove;

    /**
     * 地下总实际建筑面积
     */
    @ApiModelProperty("地下总实际建筑面积")
    private BigDecimal totalActualAreaUnder;

    /**
     * 地上总建筑面积
     */
    @ApiModelProperty("地上总建筑面积")
    private BigDecimal totalAreaAbove;

    /**
     * 地下总建筑面积
     */
    @ApiModelProperty("地下总建筑面积")
    private BigDecimal totalAreaUnder;

    /**
     * 地上总可售建筑面积
     */
    @ApiModelProperty("地上总可售建筑面积")
    private BigDecimal totalSaleAreaAbove;

    /**
     * 地下总可售建筑面积
     */
    @ApiModelProperty("地下总可售建筑面积")
    private BigDecimal totalSaleAreaUnder;

    /**
     * 地上总可租建筑面积
     */
    @ApiModelProperty("地上总可租建筑面积")
    private BigDecimal totalRentAreaAbove;

    /**
     * 地下总可租建筑面积
     */
    @ApiModelProperty("地下总可租建筑面积")
    private BigDecimal totalRentAreaUnder;

    /**
     * 地上总赠送建筑面积
     */
    @ApiModelProperty("地上总赠送建筑面积")
    private BigDecimal totalGiveAreaAbove;

    /**
     * 地下总赠送建筑面积
     */
    @ApiModelProperty("地下总赠送建筑面积")
    private BigDecimal totalGiveAreaUnder;

    /**
     * 地上总非可租售建筑面积
     */
    @ApiModelProperty("地上总非可租售建筑面积")
    private BigDecimal totalUnRentAreaAbove;

    /**
     * 地下总非可租售建筑面积
     */
    @ApiModelProperty("地下总非可租售建筑面积")
    private BigDecimal totalUnRentAreaUnder;

    /**
     * 总用地面积
     */
    @ApiModelProperty("总用地面积")
    private BigDecimal totalUsedArea;

    /**
     * 建设用地面积
     */
    @ApiModelProperty("建设用地面积")
    private BigDecimal buildingUsedArea;

    /**
     * 建设占地面积
     */
    @ApiModelProperty("建设占地面积")
    private BigDecimal buildingCoverdArea;

    /**
     * 基底面积
     */
    @ApiModelProperty("基底面积")
    private BigDecimal basalArea;

    /**
     * 地上计容建筑面积
     */
    @ApiModelProperty("地上计容建筑面积")
    private BigDecimal capacityAreaAbove;

    /**
     * 地下计容建筑面积
     */
    @ApiModelProperty("地下计容建筑面积")
    private BigDecimal capacityAreaUnder;

    /**
     * 人防面积
     */
    @ApiModelProperty("人防面积")
    private BigDecimal civilDefenceArea;

    /**
     * 非人防面积
     */
    @ApiModelProperty("非人防面积")
    private BigDecimal unCivilDefenceArea;

    /**
     * 易地人防面积
     */
    @ApiModelProperty("易地人防面积")
    private BigDecimal elesCivilDefenceArea;

    /**
     * 易地绿地面积
     */
    @ApiModelProperty("易地绿地面积")
    private BigDecimal elesGreenArea;

    /**
     * 地上总代征用地面积
     */
    @ApiModelProperty("地上总代征用地面积")
    private BigDecimal totalExpropriationAreaAbove;

    /**
     * 地下总代征用地面积
     */
    @ApiModelProperty("地下总代征用地面积")
    private BigDecimal totalExpropriationAreaUnder;

    /**
     * 批量精装修套内面积
     */
    @ApiModelProperty("批量精装修套内面积")
    private BigDecimal batchDecorationArea;

    /**
     * 典型户型精装修套内面积
     */
    @ApiModelProperty("典型户型精装修套内面积")
    private BigDecimal baseApartmentArea;

    /**
     * 公区精装修面积
     */
    @ApiModelProperty("公区精装修面积")
    private BigDecimal publicDecorationArea;

    /**
     * 精装修对应实际建筑面积
     */
    @ApiModelProperty("精装修对应实际建筑面积")
    private BigDecimal decorationForActualArea;

    /**
     * 代征硬景面积
     */
    @ApiModelProperty("代征硬景面积")
    private BigDecimal substituteHardArea;

    /**
     * 区内硬景面积
     */
    @ApiModelProperty("区内硬景面积")
    private BigDecimal intraHardArea;

    /**
     * 代征软景面积
     */
    @ApiModelProperty("代征软景面积")
    private BigDecimal substituteSoftArea;

    /**
     * 区内软景面积
     */
    @ApiModelProperty("区内软景面积")
    private BigDecimal intraSoftArea;

    /**
     * 已售面积
     */
    @ApiModelProperty("已售面积")
    private BigDecimal saledArea;



    /**
     * 排序号
     */
    private Integer sortNo;

}
