package com.tahoecn.bo.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 经营概览 - 货值
 *
 * @author panglx
 * @date 2019-7-2
 */
@ApiModel("经营概览 - 货值")
@Data
public class ManageOverviewPriceVO implements Serializable {

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
     * 全盘动态货值
     */
    @ApiModelProperty("全盘动态货值")
    private BigDecimal overallProductPrice;

    /**
     * 投资决策会货值
     */
    @ApiModelProperty("投资决策会货值")
    private BigDecimal investDecisionProductPrice;

    /**
     * 经营决策会货值
     */
    @ApiModelProperty("经营决策会货值")
    private BigDecimal manageDecisionProductPrice;

    /**
     * 未开发（土储）货值
     */
    @ApiModelProperty("未开发（土储）货值")
    private BigDecimal unDevelopProductPrice;

    /**
     * 未开工货值
     */
    @ApiModelProperty("未开工货值")
    private BigDecimal unStartProductPrice;

    /**
     * 已开工未达形象进度货值
     */
    @ApiModelProperty("已开工未达形象进度货值")
    private BigDecimal unReachImgGoalProductPrice;

    /**
     * 已达形象进度未取证货值
     */
    @ApiModelProperty("已达形象进度未取证货值")
    private BigDecimal unGetCardProductPrice;

    /**
     * 已取证未售（存货）货值
     */
    @ApiModelProperty("已取证未售（存货）货值")
    private BigDecimal unSaleProductPrice;

    /**
     * 未售=未开工+已开工未达形象进度+已达形象进度未取证+已取证未售
     */
    @ApiModelProperty("未售=未开工+已开工未达形象进度+已达形象进度未取证+已取证未售")
    private BigDecimal realUnSaleProductPrice;

    /**
     * 已售货值
     */
    @ApiModelProperty("已售货值")
    private BigDecimal saledProductPrice;

    /**
     * 3个月内存货货值
     */
    @ApiModelProperty("3个月内存货货值")
    private BigDecimal ageFirstProductPrice;

    /**
     * 3-6个月内存货货值
     */
    @ApiModelProperty("3-6个月内存货货值")
    private BigDecimal ageSencondProductPrice;

    /**
     * 6-12个月内存货货值
     */
    @ApiModelProperty("6-12个月内存货货值")
    private BigDecimal ageThirdProductPrice;

    /**
     * 12-24个月内存货货值
     */
    @ApiModelProperty("12-24个月内存货货值")
    private BigDecimal ageFourthProductPrice;

    /**
     * 24个月以上
     */
    @ApiModelProperty("24个月以上")
    private BigDecimal ageFifthProductPrice;

    /**
     * 未开发均价
     */
    @ApiModelProperty("未开发均价")
    private BigDecimal unDevelopAvgPrice;

    /**
     * 未售均价
     */
    @ApiModelProperty("未售均价")
    private BigDecimal unSaleAvgPrice;

    /**
     * 已售均价
     */
    @ApiModelProperty("已售均价")
    private BigDecimal saledAvgPrice;

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
     * 已售面积
     */
    @ApiModelProperty("已售面积")
    private BigDecimal saledArea;

    /**
     * 已售未取证面积
     */
    @ApiModelProperty("已售未取证面积")
    private BigDecimal saledUnGetCardArea;

    /**
     * 已售已取证面积
     */
    @ApiModelProperty("已售已取证面积")
    private BigDecimal saledGetCardArea;

    /**
     * 未开工货值-认购口径
     */
    @ApiModelProperty("未开工货值-认购口径")
    private BigDecimal offerUnStartProductPrice;

    /**
     * 已开工未达形象进度货值-认购口径
     */
    @ApiModelProperty("已开工未达形象进度货值-认购口径")
    private BigDecimal offerUnReachImgGoalProductPrice;

    /**
     * 已达形象进度未取证货值-认购口径
     */
    @ApiModelProperty("已达形象进度未取证货值-认购口径")
    private BigDecimal offerUnGetCardProductPrice;

    /**
     * 已取证未售货值-认购口径
     */
    @ApiModelProperty("已取证未售货值-认购口径")
    private BigDecimal offerUnSaleProductPrice;

    /**
     * 已售货值-认购口径
     */
    @ApiModelProperty("已售货值-认购口径")
    private BigDecimal offerSaledProductPrice;

    /**
     * 已售未取证货值-认购口径
     */
    @ApiModelProperty("已售未取证货值-认购口径")
    private BigDecimal offerSaledUnGetCardProductPrice;

    /**
     * 已售已取证货值-认购口径
     */
    @ApiModelProperty("已售已取证货值-认购口径")
    private BigDecimal offerSaledGetCardProductPrice;

    /**
     * 三个月内存货货值-认购口径
     */
    @ApiModelProperty("三个月内存货货值-认购口径")
    private BigDecimal offerAgeFirstProductPrice;

    /**
     * 3-6个月内存货货值-认购口径
     */
    @ApiModelProperty("3-6个月内存货货值-认购口径")
    private BigDecimal offerAgeSencondProductPrice;

    /**
     * 6-12个月内存货货值-认购口径
     */
    @ApiModelProperty("6-12个月内存货货值-认购口径")
    private BigDecimal offerAgeThirdProductPrice;

    /**
     * 12-24个月内存货货值-认购口径
     */
    @ApiModelProperty("12-24个月内存货货值-认购口径")
    private BigDecimal offerAgeFourthProductPrice;

    /**
     * 24个月以上-认购口径
     */
    @ApiModelProperty("24个月以上-认购口径")
    private BigDecimal offerAgeFifthProductPrice;

    /**
     * 未开发均价-认购口径
     */
    @ApiModelProperty("未开发均价-认购口径")
    private BigDecimal offerUnDevelopAvgPrice;

    /**
     * 未售均价-认购口径
     */
    @ApiModelProperty("未售均价-认购口径")
    private BigDecimal offerUnSaleAvgPrice;

    /**
     * 已售均价-认购口径
     */
    @ApiModelProperty("已售均价-认购口径")
    private BigDecimal offerSaledAvgPrice;

    /**
     * 未开工面积-认购口径
     */
    @ApiModelProperty("未开工面积-认购口径")
    private BigDecimal offerUnStartArea;

    /**
     * 已开工未达形象进度面积-认购口径
     */
    @ApiModelProperty("已开工未达形象进度面积-认购口径")
    private BigDecimal offerUnReachImgGoalArea;

    /**
     * 已达形象进度未取证面积-认购口径
     */
    @ApiModelProperty("已达形象进度未取证面积-认购口径")
    private BigDecimal offerUnGetCardArea;

    /**
     * 已取证未售（存货）面积-认购口径
     */
    @ApiModelProperty("已取证未售（存货）面积-认购口径")
    private BigDecimal offerUnSaleArea;

    /**
     * 已售面积-认购口径
     */
    @ApiModelProperty("已售面积-认购口径")
    private BigDecimal offerSaledArea;

    /**
     * 已售未取证面积-认购口径
     */
    @ApiModelProperty("已售未取证面积-认购口径")
    private BigDecimal offerSaledUnGetCardArea;

    /**
     * 已售已取证面积-认购口径
     */
    @ApiModelProperty("已售已取证面积-认购口径")
    private BigDecimal offerSaledGetCardArea;

    /**
     * 排序号
     */
    private Integer sortNo;

}
