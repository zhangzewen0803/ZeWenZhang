package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.mapper.BoProjectQuotaMapMapper;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.thread.ThreadUtil;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 项目分期指标数据表/映射表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
@Service
public class BoProjectQuotaMapServiceImpl extends ServiceImpl<BoProjectQuotaMapMapper, BoProjectQuotaMap> implements BoProjectQuotaMapService {

    private static final Log logger = LogFactory.get();

    @Autowired
    private BoProjectLandPartMapService boProjectLandPartMapService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoBuildingQuotaMapService boBuildingQuotaMapService;

    @Autowired
    private MdmProjectInfoService mdmProjectInfoService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<BoProjectQuotaMap> getProjectQuotaMapList(String projectQuotaExtendId) {
        QueryWrapper<BoProjectQuotaMap> quotaMapQueryWrapper = new QueryWrapper<>();
        quotaMapQueryWrapper
                .eq("project_quota_extend_id", projectQuotaExtendId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return baseMapper.selectList(quotaMapQueryWrapper);
    }

    @Override
    public List<BoProjectQuotaMap> getProjectQuotaMapListWithCalcSum(String projectQuotaExtendId) {
        QueryWrapper<MdmProjectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("sid", "select project_id from bo_project_quota_extend where id='" + projectQuotaExtendId.replace("'", "") + "'");
        MdmProjectInfo mdmProjectInfo = mdmProjectInfoService.getOne(queryWrapper);
        if (mdmProjectInfo != null) {
            LevelTypeEnum levelTypeEnum = LevelTypeEnum.getByKey(mdmProjectInfo.getLevelType());
            if (levelTypeEnum != null) {
                switch (levelTypeEnum) {
                    case PROJECT:
                        return getProjectQuotaMapListWithCalcSumProject(projectQuotaExtendId);
                    case PROJECT_SUB:
                        return getProjectQuotaMapListWithCalcSumProjectSub(projectQuotaExtendId);
                    default:
                }
            }
        }
        return new ArrayList<>(0);
    }

    @Override
    public List<BoProjectQuotaMap> getProjectQuotaMapListWithCalcSumProject(String projectQuotaExtendId) {
        //項目规划指标
        List<BoProjectQuotaMap> projectQuotaMapList = getProjectQuotaMapList(projectQuotaExtendId);
        if (!projectQuotaMapList.isEmpty()) {
            //从项目地块信息中汇总规划指标
            calcProjectLandPartData(projectQuotaExtendId, projectQuotaMapList);

            //从 地块业态中汇总项目规划指标
            calcLandPartProcuctTypeData(projectQuotaExtendId, projectQuotaMapList);
        }

        return projectQuotaMapList;
    }

    /**
     * 从 地块业态中汇总项目规划指标
     *
     * @param projectQuotaExtendId
     * @param projectQuotaMapList
     */
    private void calcLandPartProcuctTypeData(String projectQuotaExtendId, List<BoProjectQuotaMap> projectQuotaMapList) {
        List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeList(projectQuotaExtendId);
        if (!landPartProductTypeList.isEmpty()) {
            List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaList(projectQuotaExtendId);
            projectQuotaMapList.parallelStream().forEach(x -> {
                QuotaCodeEnum quotaCodeEnum = QuotaCodeEnum.getByKey(x.getQuotaCode());
                if (quotaCodeEnum == null) {
                    return;
                }
                switch (quotaCodeEnum) {
                    case HOUSE_ABOVE_GROUND_BUILDING_AREA:
                        //住宅地上建筑面积 - 来自“住宅”业态的地上总建筑面积汇总
                        Set<String> aboveHouseMapIdSet = landPartProductTypeList.parallelStream()
                                .filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.HOUSE.getKey()))
                                .map(y -> y.getId())
                                .collect(Collectors.toSet());
                        if (!aboveHouseMapIdSet.isEmpty()) {
                            List<BoLandPartProductTypeQuotaMap> tmpAboveHouseQuotaList = landPartProductTypeQuotaList.parallelStream().filter(y -> aboveHouseMapIdSet.contains(y.getLandPartProductTypeId()))
                                    .collect(Collectors.toList());
                            if (!tmpAboveHouseQuotaList.isEmpty()) {
                                Set<QuotaCodeEnum> aboveHouseAreaEnums = Stream.of(
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA
                                ).collect(Collectors.toSet());
                                commonCalcLandPartProductTypeArea(tmpAboveHouseQuotaList, x, aboveHouseAreaEnums);
                            }
                        }
                        break;
                    case POLICY_HOUSE_BUILDING_AREA:
                        //政策房建筑面积 - 来自“住宅->政策房”业态的总建筑面积汇总
                        Set<String> poicyHouseMapIdSet = landPartProductTypeList.parallelStream()
                                .filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.POLICY_HOUSE.getKey()))
                                .map(y -> y.getId())
                                .collect(Collectors.toSet());
                        if (!poicyHouseMapIdSet.isEmpty()) {
                            List<BoLandPartProductTypeQuotaMap> tmpPolicyHouseQuotaList = landPartProductTypeQuotaList.parallelStream().filter(y -> poicyHouseMapIdSet.contains(y.getLandPartProductTypeId()))
                                    .collect(Collectors.toList());
                            if (!tmpPolicyHouseQuotaList.isEmpty()) {
                                Set<QuotaCodeEnum> policyHouseAreaEnums = Stream.of(
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                                ).collect(Collectors.toSet());
                                commonCalcLandPartProductTypeArea(tmpPolicyHouseQuotaList, x, policyHouseAreaEnums);
                            }
                        }
                        break;
                    case COMPLETE_BUSINESS_BUILDING_AREA:
                        //配套商业建筑面积 - 来自"公建 -> 商业 -> 可售类型 -> 销售商业" 业态的总建筑面积
                        Set<String> completeBusinessMapIdSet = landPartProductTypeList.parallelStream()
                                .filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.SALE_BUSINESS.getKey()))
                                .map(y -> y.getId())
                                .collect(Collectors.toSet());
                        if (!completeBusinessMapIdSet.isEmpty()) {
                            List<BoLandPartProductTypeQuotaMap> tmpCompleteBuinessQuotaList = landPartProductTypeQuotaList.parallelStream().filter(y -> completeBusinessMapIdSet.contains(y.getLandPartProductTypeId()))
                                    .collect(Collectors.toList());
                            if (!tmpCompleteBuinessQuotaList.isEmpty()) {
                                Set<QuotaCodeEnum> completeBusinessAreaEnums = Stream.of(
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                                ).collect(Collectors.toSet());
                                commonCalcLandPartProductTypeArea(tmpCompleteBuinessQuotaList, x, completeBusinessAreaEnums);
                            }
                        }
                        break;
                    case OTHER_COMPLETE_BUILDING_AREA:
                        //其他配建建筑面积 - 来自“配套”业态的总建筑面积汇总
                        Set<String> otherCompleteMapIdSet = landPartProductTypeList.parallelStream()
                                .filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.COMPLETE.getKey()))
                                .map(y -> y.getId())
                                .collect(Collectors.toSet());
                        if (!otherCompleteMapIdSet.isEmpty()) {
                            List<BoLandPartProductTypeQuotaMap> tmpOtherCompleteQuotaList = landPartProductTypeQuotaList.parallelStream().filter(y -> otherCompleteMapIdSet.contains(y.getLandPartProductTypeId()))
                                    .collect(Collectors.toList());
                            if (!tmpOtherCompleteQuotaList.isEmpty()) {
                                Set<QuotaCodeEnum> otherCompleteAreaEnums = Stream.of(
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                                ).collect(Collectors.toSet());
                                commonCalcLandPartProductTypeArea(tmpOtherCompleteQuotaList, x, otherCompleteAreaEnums);
                            }
                        }
                        break;
                    case OWNER_PROPERT_BUILDING_AREA:
                        //自持物业建筑面积 - 来自 地块 总可租面积汇总
                        Set<QuotaCodeEnum> otherCompleteAreaEnums = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, otherCompleteAreaEnums);
                        break;
                    case OVERHEAD_REFUGE_CHANGE_FLOOR_AREA:
                        //架空层\避难层\转换层建筑面积 - 来自 "架空层\避难层\转换层" 业态的总建筑面积
                        Set<String> changeFloorMapIdSet = landPartProductTypeList.parallelStream()
                                .filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.CHANGE_FLOOR.getKey()))
                                .map(y -> y.getId())
                                .collect(Collectors.toSet());
                        if (!changeFloorMapIdSet.isEmpty()) {
                            List<BoLandPartProductTypeQuotaMap> tmpChangeFloorQuotaList = landPartProductTypeQuotaList.parallelStream().filter(y -> changeFloorMapIdSet.contains(y.getLandPartProductTypeId()))
                                    .collect(Collectors.toList());
                            if (!tmpChangeFloorQuotaList.isEmpty()) {
                                Set<QuotaCodeEnum> changeFloorAreaEnums = Stream.of(
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                        QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                                ).collect(Collectors.toSet());
                                commonCalcLandPartProductTypeArea(tmpChangeFloorQuotaList, x, changeFloorAreaEnums);
                            }
                        }
                        break;
                    case TOTAL_BUILDING_AREA:
                        //总建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> totalBuildingAreaEnums = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, totalBuildingAreaEnums);
                        break;
                    case ABOVE_GROUND_TOTAL_BUILDING_AREA:
                        //地上总建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveTotalBuildingAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveTotalBuildingAreaEnumSet);
                        break;
                    case UNDER_GROUND_TOTAL_BUILDING_AREA:
                        //地下总建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underTotalBuildingAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underTotalBuildingAreaEnumSet);
                        break;
                    case TOTAL_REAL_BUILDING_AREA:
                        //实际建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> realBuildingAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_GIVE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, realBuildingAreaEnumSet);
                        break;
                    case ABOVE_GROUND_REAL_BUILDING_AREA:
                        //地上实际建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveRealBuildingAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveRealBuildingAreaEnumSet);
                        break;
                    case UNDER_GROUND_REAL_BUILDING_AREA:
                        //地下实际建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underRealBuildingAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_GIVE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underRealBuildingAreaEnumSet);
                        break;
                    case TOTAL_CAN_RENT_SALE_AREA:
                        //总可租售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> totalCanRentSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, totalCanRentSaleAreaEnumSet);
                        break;
                    case ABOVE_GROUND_CAN_RENT_SALE_AREA:
                        //地上可租售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveCanRentSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveCanRentSaleAreaEnumSet);
                        break;
                    case UNDER_GROUND_CAN_RENT_SALE_AREA:
                        //地下可租售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underCanRentSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underCanRentSaleAreaEnumSet);
                        break;
                    case TOTAL_CAN_SALE_AREA:
                        //总可售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> totalCanSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, totalCanSaleAreaEnumSet);
                        break;
                    case ABOVE_GROUND_CAN_SALE_AREA:
                        //地上可售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveCanSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveCanSaleAreaEnumSet);
                        break;
                    case UNDER_GROUND_CAN_SALE_AREA:
                        //地下可售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underCanSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underCanSaleAreaEnumSet);
                        break;
                    case TOTAL_CAN_RENT_AREA:
                        //总可租面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> totalCanRentAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, totalCanRentAreaEnumSet);
                        break;
                    case ABOVE_GROUND_CAN_RENT_AREA:
                        //地上可租面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveCanRentAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveCanRentAreaEnumSet);
                        break;
                    case UNDER_GROUND_CAN_RENT_AREA:
                        //地下可租面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underCanRentAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underCanRentAreaEnumSet);
                        break;
                    case TOTAL_CAN_NOT_RENT_SALE_AREA:
                        //总非可租售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> totalNotCanRentSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, totalNotCanRentSaleAreaEnumSet);
                        break;
                    case ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA:
                        //地上非可租售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveNotCanRentSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveNotCanRentSaleAreaEnumSet);
                        break;
                    case UNDER_GROUND_CAN_NOT_RENT_SALE_AREA:
                        //地下非可租售面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underNotCanRentSaleAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underNotCanRentSaleAreaEnumSet);
                        break;
                    case ABOVE_GROUND_GIVE_AREA:
                        //地上赠送面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> aboveGiveAreaEnumSet = Stream.of(
                                QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, aboveGiveAreaEnumSet);
                        break;
                    case UNDER_GROUND_GIVE_AREA:
                        //地下赠送面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> underGiveAreaEnumSet = Stream.of(
                                QuotaCodeEnum.UNDER_GROUND_GIVE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, underGiveAreaEnumSet);
                        break;
                    case BATCH_REFINE_INNER_SET_AREA:
                        //批量精装修套内面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> batchRefineAreaEnumSet = Stream.of(
                                QuotaCodeEnum.BATCH_REFINE_INNER_SET_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, batchRefineAreaEnumSet);
                        break;
                    case TYPICAL_HOUSE_REFINE_INNER_SET_AREA:
                        //典型户型精装修套内面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> typeRefineAreaEnumSet = Stream.of(
                                QuotaCodeEnum.TYPICAL_HOUSE_REFINE_INNER_SET_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, typeRefineAreaEnumSet);
                        break;
                    case STANDARD_FLOOR_REFINE_AREA:
                        //标准层精装修面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> standardRefineAreaEnumSet = Stream.of(
                                QuotaCodeEnum.STANDARD_FLOOR_REFINE_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, standardRefineAreaEnumSet);
                        break;
                    case REFINE_MAP_REAL_BUILDING_AREA:
                        //精装修对应实际建筑面积 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> realRefineAreaEnumSet = Stream.of(
                                QuotaCodeEnum.REFINE_MAP_REAL_BUILDING_AREA
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, realRefineAreaEnumSet);
                        break;
                    case REFINE_HOUSE_COUNT:
                        //精装修户数 - 来自 地块业态信息汇总
                        Set<QuotaCodeEnum> refineHouseCountEnumSet = Stream.of(
                                QuotaCodeEnum.REFINE_HOUSE_COUNT
                        ).collect(Collectors.toSet());
                        commonCalcLandPartProductTypeArea(landPartProductTypeQuotaList, x, refineHouseCountEnumSet);
                        break;
                    default:
                }
            });
        }
    }

    private void commonCalcLandPartProductTypeArea(List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList, BoProjectQuotaMap x, Set<QuotaCodeEnum> quotaCodeEnums) {
        Optional<BigDecimal> reduce = landPartProductTypeQuotaList.stream().filter(y -> {
            QuotaCodeEnum codeEnum = QuotaCodeEnum.getByKey(y.getQuotaCode());
            if (codeEnum == null) {
                return false;
            }
            return quotaCodeEnums.contains(codeEnum);
        }).map(y -> new BigDecimal(StringUtils.isBlank(y.getQuotaValue()) ? "0" : y.getQuotaValue()))
                .reduce((a, b) -> a.add(b));
        if (reduce.isPresent()) {
            BigDecimal aboveHouseArea = reduce
                    .get();
            x.setQuotaValue(aboveHouseArea.toString());
        }
    }

    @Override
    public List<BoProjectQuotaMap> getProjectQuotaMapListWithCalcSumProjectSub(String projectQuotaExtendId) {
        //分期规划指标
        List<BoProjectQuotaMap> projectQuotaMapList = getProjectQuotaMapList(projectQuotaExtendId);
        if (!projectQuotaMapList.isEmpty()) {
            //从分期地块信息中汇总分期规划指标信息
            ForkJoinTask<Integer> job1 = ForkJoinPool.commonPool().submit(() -> {
                calcProjectLandPartData(projectQuotaExtendId, projectQuotaMapList);
                return 0;
            });


            //从楼栋业态中汇总分期规划指标信息
            ForkJoinTask<Integer> job2 = ForkJoinPool.commonPool().submit(() -> {
                calcBuildingProductTypeData(projectQuotaExtendId, projectQuotaMapList);
                return 0;
            });
            try {
                job1.get();
                job2.get();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return projectQuotaMapList;
    }

    @Override
    public void noticeCalcProjectQuota(String... projectQuotaExtendId) {
        redisTemplate.opsForList().rightPushAll(RedisConstants.PROJECT_QUOTA_CALC_LIST, Arrays.asList(projectQuotaExtendId));
    }

    @Override
    public void processCalcProjectQuota() {
        Object obj;
        while ((obj = redisTemplate.opsForList().leftPop(RedisConstants.PROJECT_QUOTA_CALC_LIST, 5, TimeUnit.MINUTES)) != null) {
            if (obj instanceof String) {
                String id = (String) obj;
                logger.info("processCalcProjectQuota 收到ID: " + id);
                ThreadUtil.execute(() -> {
                    try {
                        List<BoProjectQuotaMap> projectQuotaMaps = getProjectQuotaMapListWithCalcSum(id);
                        if (!projectQuotaMaps.isEmpty()) {
                            projectQuotaMaps.parallelStream().forEach(x -> x.setUpdateTime(LocalDateTime.now()));
                            updateBatchById(projectQuotaMaps, 100);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                });
            }
        }
    }

    /**
     * 从楼栋业态中汇总分期规划指标信息
     *
     * @param projectQuotaExtendId
     * @param projectQuotaMapList
     */
    private void calcBuildingProductTypeData(String projectQuotaExtendId, List<BoProjectQuotaMap> projectQuotaMapList) {
        //查询楼栋业态关系列表
        List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(projectQuotaExtendId);
        if (!buildingProductTypeList.isEmpty()) {
            List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(projectQuotaExtendId);
            if (!buildingProductTypeQuotaList.isEmpty()) {
                List<BoBuildingQuotaMap> buildingQuotaList = boBuildingQuotaMapService.getBuildingQuotaList(projectQuotaExtendId);
                projectQuotaMapList.parallelStream().forEach(x -> {
                    QuotaCodeEnum quotaCodeEnum = QuotaCodeEnum.getByKey(x.getQuotaCode());
                    if (quotaCodeEnum == null) {
                        return;
                    }
                    switch (quotaCodeEnum) {
                        case HOUSE_ABOVE_GROUND_BUILDING_AREA:
                            //住宅地上建筑面积 - 来自“住宅”业态的地上总建筑面积汇总
                            calcHouseAboveGroundBuildingArea(buildingProductTypeList, buildingProductTypeQuotaList, x);
                            break;
                        case POLICY_HOUSE_BUILDING_AREA:
                            //政策房建筑面积 - 来自“住宅->政策房”业态的总建筑面积汇总
                            calcPolicyHouseTotalBuildingArea(buildingProductTypeList, buildingProductTypeQuotaList, x);
                            break;
                        case COMPLETE_BUSINESS_BUILDING_AREA:
                            //配套商业建筑面积 - 来自"公建 -> 商业 -> 可售类型 -> 销售商业" 业态的总建筑面积
                            calcCompleteBusinessBuildingArea(buildingProductTypeList, buildingProductTypeQuotaList, x);
                            break;
                        case OTHER_COMPLETE_BUILDING_AREA:
                            //其他配建建筑面积 - 来自“配套”业态的总建筑面积汇总
                            calcOtherCompleteBuildingArea(buildingProductTypeList, buildingProductTypeQuotaList, x);
                            break;
                        case OWNER_PROPERT_BUILDING_AREA:
                            //自持物业建筑面积 - 来自 楼栋 总可租面积汇总
                            calcOwnerPropertBuildingArea(buildingProductTypeList, buildingProductTypeQuotaList, x);
                            break;
                        case OVERHEAD_REFUGE_CHANGE_FLOOR_AREA:
                            //架空层\避难层\转换层建筑面积 - 来自 "架空层\避难层\转换层" 业态的总建筑面积
                            calcChangeFloorBuildingArea(buildingProductTypeList, buildingProductTypeQuotaList, x);
                            break;
                        case TOTAL_BUILDING_AREA:
                            //总建筑面积 - 来自 楼栋信息汇总
                            calcTotalBuildingArea(buildingProductTypeQuotaList, x, buildingProductTypeList);
                            break;
                        case ABOVE_GROUND_TOTAL_BUILDING_AREA:
                            //地上总建筑面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> aboveTotalBuildingAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA
                            )
                                    .collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveTotalBuildingAreaEnumSet);
                            break;
                        case UNDER_GROUND_TOTAL_BUILDING_AREA:
                            //地下总建筑面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underTotalBuildingAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                            )
                                    .collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underTotalBuildingAreaEnumSet);
                            break;
                        case TOTAL_REAL_BUILDING_AREA:
                            //实际建筑面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> realBuildingAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_GIVE_AREA
                            )
                                    .collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, realBuildingAreaEnumSet);
                            break;
                        case ABOVE_GROUND_REAL_BUILDING_AREA:
                            //地上实际建筑面积 - 来自 楼栋信息汇总 TODO
                            Set<QuotaCodeEnum> aboveRealBuildingAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA
                            )
                                    .collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveRealBuildingAreaEnumSet);
                            break;
                        case UNDER_GROUND_REAL_BUILDING_AREA:
                            //地下实际建筑面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underRealBuildingAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_GIVE_AREA
                            )
                                    .collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underRealBuildingAreaEnumSet);
                            break;
                        case TOTAL_CAN_RENT_SALE_AREA:
                            //总可租售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> totalCanRentSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, totalCanRentSaleAreaEnumSet);
                            break;
                        case ABOVE_GROUND_CAN_RENT_SALE_AREA:
                            //地上可租售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> aboveCanRentSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveCanRentSaleAreaEnumSet);
                            break;
                        case UNDER_GROUND_CAN_RENT_SALE_AREA:
                            //地下可租售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underCanRentSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underCanRentSaleAreaEnumSet);
                            break;
                        case TOTAL_CAN_SALE_AREA:
                            //总可售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> totalCanSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, totalCanSaleAreaEnumSet);
                            break;
                        case ABOVE_GROUND_CAN_SALE_AREA:
                            //地上可售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> aboveCanSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveCanSaleAreaEnumSet);
                            break;
                        case UNDER_GROUND_CAN_SALE_AREA:
                            //地下可售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underCanSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underCanSaleAreaEnumSet);
                            break;
                        case TOTAL_CAN_RENT_AREA:
                            //总可租面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> totalCanRentAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, totalCanRentAreaEnumSet);
                            break;
                        case ABOVE_GROUND_CAN_RENT_AREA:
                            //地上可租面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> aboveCanRentAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveCanRentAreaEnumSet);
                            break;
                        case UNDER_GROUND_CAN_RENT_AREA:
                            //地下可租面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underCanRentAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underCanRentAreaEnumSet);
                            break;
                        case TOTAL_CAN_NOT_RENT_SALE_AREA:
                            //总非可租售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> totalNotCanRentSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA,
                                    QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, totalNotCanRentSaleAreaEnumSet);
                            break;
                        case ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA:
                            //地上非可租售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> aboveNotCanRentSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveNotCanRentSaleAreaEnumSet);
                            break;
                        case UNDER_GROUND_CAN_NOT_RENT_SALE_AREA:
                            //地下非可租售面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underNotCanRentSaleAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underNotCanRentSaleAreaEnumSet);
                            break;
                        case ABOVE_GROUND_GIVE_AREA:
                            //地上赠送面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> aboveGiveAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, aboveGiveAreaEnumSet);
                            break;
                        case UNDER_GROUND_GIVE_AREA:
                            //地下赠送面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> underGiveAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.UNDER_GROUND_GIVE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, underGiveAreaEnumSet);
                            break;
                        case SINGLE_LAND_COVER_AREA:
                            //单体占地面积 - 来自 楼栋信息汇总
                            if (!buildingQuotaList.isEmpty()) {
                                BigDecimal singleArea = buildingQuotaList.stream().filter(y -> QuotaCodeEnum.SINGLE_LAND_COVER_AREA.getKey().equals(y.getQuotaCode()))
                                        .map(y -> new BigDecimal(StringUtils.isBlank(y.getQuotaValue()) ? "0" : y.getQuotaValue())).reduce((a, b) -> a.add(b)).get();
                                x.setQuotaValue(singleArea.toString());
                            }
                            break;
                        case BATCH_REFINE_INNER_SET_AREA:
                            //批量精装修套内面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> batchRefineAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.BATCH_REFINE_INNER_SET_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, batchRefineAreaEnumSet);
                            break;
                        case TYPICAL_HOUSE_REFINE_INNER_SET_AREA:
                            //典型户型精装修套内面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> typeRefineAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.TYPICAL_HOUSE_REFINE_INNER_SET_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, typeRefineAreaEnumSet);
                            break;
                        case STANDARD_FLOOR_REFINE_AREA:
                            //标准层精装修面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> standardRefineAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.STANDARD_FLOOR_REFINE_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, standardRefineAreaEnumSet);
                            break;
                        case REFINE_MAP_REAL_BUILDING_AREA:
                            //精装修对应实际建筑面积 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> realRefineAreaEnumSet = Stream.of(
                                    QuotaCodeEnum.REFINE_MAP_REAL_BUILDING_AREA
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, realRefineAreaEnumSet);
                            break;
                        case REFINE_HOUSE_COUNT:
                            //精装修户数 - 来自 楼栋信息汇总
                            Set<QuotaCodeEnum> refineHouseCountEnumSet = Stream.of(
                                    QuotaCodeEnum.REFINE_HOUSE_COUNT
                            ).collect(Collectors.toSet());
                            commonBuildingProductTypeCalcArea(buildingProductTypeQuotaList, x, buildingProductTypeList, refineHouseCountEnumSet);
                            break;
                        case BASE_FLOOR_AREA:
                            //基底面积 - 来自 楼栋信息汇总
                            if (!buildingQuotaList.isEmpty()) {
                                BigDecimal baseFloorArea = buildingQuotaList.parallelStream().filter(y -> QuotaCodeEnum.BASE_FLOOR_AREA.getKey().equals(y.getQuotaCode()))
                                        .map(y -> new BigDecimal(StringUtils.isBlank(y.getQuotaValue()) ? "0" : y.getQuotaValue())).reduce((a, b) -> a.add(b)).get();
                                x.setQuotaValue(baseFloorArea.toString());
                            }
                        default:
                    }
                });
            }
        }
    }

    /**
     * 架空层\避难层\转换层建筑面积 - 来自 "架空层\避难层\转换层" 业态的总建筑面积
     *
     * @param buildingProductTypeList
     * @param buildingProductTypeQuotaList
     * @param x
     */
    private void calcChangeFloorBuildingArea(List<BoBuildingProductTypeMap> buildingProductTypeList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList, BoProjectQuotaMap x) {
        List<BoBuildingProductTypeMap> changeFloorList = buildingProductTypeList.parallelStream().filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.CHANGE_FLOOR.getKey())).collect(Collectors.toList());
        calcTotalBuildingArea(buildingProductTypeQuotaList, x, changeFloorList);
    }

    /**
     * 自持物业建筑面积 - 来自 楼栋 总可租面积汇总
     *
     * @param buildingProductTypeList
     * @param buildingProductTypeQuotaList
     * @param x
     */
    private void calcOwnerPropertBuildingArea(List<BoBuildingProductTypeMap> buildingProductTypeList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList, BoProjectQuotaMap x) {
        if (!buildingProductTypeList.isEmpty()) {
            BigDecimal canRentAreaSum = buildingProductTypeList.parallelStream().map(y -> {
                List<BoBuildingProductTypeQuotaMap> tmpQuotaList = buildingProductTypeQuotaList.parallelStream().filter(j -> StringUtils.isBlank(j.getBuildingProductTypeId()) ? false : j.getBuildingProductTypeId().equals(y.getId())).collect(Collectors.toList());
                if (tmpQuotaList.isEmpty()) {
                    return new BigDecimal("0");
                }
                Optional<BigDecimal> reduce = tmpQuotaList.parallelStream().filter(a -> {
                    QuotaCodeEnum tmpQuotaCodeEnum = QuotaCodeEnum.getByKey(a.getQuotaCode());
                    if (tmpQuotaCodeEnum == null) {
                        return false;
                    }
                    switch (tmpQuotaCodeEnum) {
                        case ABOVE_GROUND_CAN_RENT_AREA:
                            return true;
                        case UNDER_GROUND_CAN_RENT_AREA:
                            return true;
                        default:
                            return false;
                    }
                }).map(a -> new BigDecimal(StringUtils.isBlank(a.getQuotaValue()) ? "0" : a.getQuotaValue()))
                        .reduce((a, b) -> a.add(b));
                return reduce.isPresent() ? reduce.get() : new BigDecimal("0");
            }).reduce((a, b) -> a.add(b)).get();
            x.setQuotaValue(canRentAreaSum.toString());
        }
    }

    /**
     * 其他配建建筑面积 - 来自“配套”业态的总建筑面积汇总
     *
     * @param buildingProductTypeMapList
     * @param buildingProductTypeQuotaMapList
     * @param x
     */
    private void calcOtherCompleteBuildingArea(List<BoBuildingProductTypeMap> buildingProductTypeMapList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaMapList, BoProjectQuotaMap x) {
        List<BoBuildingProductTypeMap> completeList = buildingProductTypeMapList.parallelStream().filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.COMPLETE.getKey())).collect(Collectors.toList());
        calcTotalBuildingArea(buildingProductTypeQuotaMapList, x, completeList);
    }

    /**
     * 计算总建筑面积
     *
     * @param boBuildingProductTypeQuotaMapList
     * @param x
     * @param completeList
     */
    private void calcTotalBuildingArea(List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapList, BoProjectQuotaMap x, List<BoBuildingProductTypeMap> completeList) {
        if (!completeList.isEmpty()) {
            BigDecimal otherCompleteBuildingArea = completeList.parallelStream().map(y -> {
                List<BoBuildingProductTypeQuotaMap> tmpQuotaList = boBuildingProductTypeQuotaMapList.parallelStream().filter(j -> StringUtils.isBlank(j.getBuildingProductTypeId()) ? false : j.getBuildingProductTypeId().equals(y.getId())).collect(Collectors.toList());
                if (tmpQuotaList.isEmpty()) {
                    return new BigDecimal("0");
                }
                Optional<BigDecimal> reduce = tmpQuotaList.parallelStream().filter(a -> {
                    QuotaCodeEnum tmpQuotaCodeEnum = QuotaCodeEnum.getByKey(a.getQuotaCode());
                    if (tmpQuotaCodeEnum == null) {
                        return false;
                    }
                    switch (tmpQuotaCodeEnum) {
                        case ABOVE_GROUND_CAN_SALE_AREA:
                            return true;
                        case ABOVE_GROUND_CAN_RENT_AREA:
                            return true;
                        case ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA:
                            return true;
                        case UNDER_GROUND_CAN_SALE_AREA:
                            return true;
                        case UNDER_GROUND_CAN_RENT_AREA:
                            return true;
                        case UNDER_GROUND_CAN_NOT_RENT_SALE_AREA:
                            return true;
                        default:
                            return false;
                    }
                }).map(a -> new BigDecimal(StringUtils.isBlank(a.getQuotaValue()) ? "0" : a.getQuotaValue()))
                        .reduce((a, b) -> a.add(b));
                return reduce.isPresent() ? reduce.get() : new BigDecimal("0");
            }).reduce((a, b) -> a.add(b)).get();
            x.setQuotaValue(otherCompleteBuildingArea.toString());
        }
    }

    /**
     * 通用计算面积和
     *
     * @param boBuildingProductTypeQuotaMapList
     * @param x
     * @param completeList
     * @param quotaCodeEnumSet
     */
    private void commonBuildingProductTypeCalcArea(List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapList, BoProjectQuotaMap x, List<BoBuildingProductTypeMap> completeList, Set<QuotaCodeEnum> quotaCodeEnumSet) {
        if (!completeList.isEmpty()) {
            BigDecimal otherCompleteBuildingArea = completeList.parallelStream().map(y -> {
                List<BoBuildingProductTypeQuotaMap> tmpQuotaList = boBuildingProductTypeQuotaMapList.parallelStream().filter(j -> StringUtils.isBlank(j.getBuildingProductTypeId()) ? false : j.getBuildingProductTypeId().equals(y.getId())).collect(Collectors.toList());
                if (tmpQuotaList.isEmpty()) {
                    return new BigDecimal("0");
                }
                Optional<BigDecimal> reduce = tmpQuotaList.parallelStream().filter(a -> {
                    QuotaCodeEnum tmpQuotaCodeEnum = QuotaCodeEnum.getByKey(a.getQuotaCode());
                    if (tmpQuotaCodeEnum == null) {
                        return false;
                    }
                    return quotaCodeEnumSet.contains(tmpQuotaCodeEnum);
                }).map(a -> new BigDecimal(StringUtils.isBlank(a.getQuotaValue()) ? "0" : a.getQuotaValue()))
                        .reduce((a, b) -> a.add(b));
                return reduce.isPresent() ? reduce.get() : new BigDecimal("0");
            }).reduce((a, b) -> a.add(b)).get();
            x.setQuotaValue(otherCompleteBuildingArea.toString());
        }
    }

    /**
     * 计算配套商业建筑面积 - 来自"公建 -> 商业 -> 可售类型 -> 销售商业" 业态的总建筑面积
     *
     * @param buildingProductTypeMapList
     * @param boBuildingProductTypeQuotaMapList
     * @param x
     */
    private void calcCompleteBusinessBuildingArea(List<BoBuildingProductTypeMap> buildingProductTypeMapList, List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapList, BoProjectQuotaMap x) {
        List<BoBuildingProductTypeMap> saleBusinessList = buildingProductTypeMapList.parallelStream().filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.SALE_BUSINESS.getKey())).collect(Collectors.toList());
        calcTotalBuildingArea(boBuildingProductTypeQuotaMapList, x, saleBusinessList);
    }

    /**
     * 计算政策房建筑面积 - 来自“住宅->政策房”业态的总建筑面积汇总
     *
     * @param buildingProductTypeMapList
     * @param buildingProductTypeQuotaMapList
     * @param x
     */
    private void calcPolicyHouseTotalBuildingArea(List<BoBuildingProductTypeMap> buildingProductTypeMapList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaMapList, BoProjectQuotaMap x) {
        List<BoBuildingProductTypeMap> policyHouseList = buildingProductTypeMapList.parallelStream().filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.POLICY_HOUSE.getKey())).collect(Collectors.toList());
        calcTotalBuildingArea(buildingProductTypeQuotaMapList, x, policyHouseList);
    }

    /**
     * 计算住宅地上建筑面积 - 来自“住宅”业态的地上总建筑面积汇总
     *
     * @param buildingProductTypeMapList
     * @param buildingProductTypeQuotaMapList
     * @param x
     */
    private void calcHouseAboveGroundBuildingArea(List<BoBuildingProductTypeMap> buildingProductTypeMapList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaMapList, BoProjectQuotaMap x) {
        List<BoBuildingProductTypeMap> houseList = buildingProductTypeMapList.parallelStream().filter(y -> StringUtils.isBlank(y.getProductTypeCode()) ? false : y.getProductTypeCode().startsWith(ProductTypeEnum.HOUSE.getKey())).collect(Collectors.toList());
        if (!houseList.isEmpty()) {
            BigDecimal houseAboveArea = houseList.parallelStream().map(y -> {
                List<BoBuildingProductTypeQuotaMap> tmpQuotaList = buildingProductTypeQuotaMapList.parallelStream().filter(j -> StringUtils.isBlank(j.getBuildingProductTypeId()) ? false : j.getBuildingProductTypeId().equals(y.getId())).collect(Collectors.toList());
                if (tmpQuotaList.isEmpty()) {
                    return new BigDecimal("0");
                }
                Optional<BigDecimal> reduce = tmpQuotaList.parallelStream().filter(a -> {
                    QuotaCodeEnum tmpQuotaCodeEnum = QuotaCodeEnum.getByKey(a.getQuotaCode());
                    if (tmpQuotaCodeEnum == null) {
                        return false;
                    }
                    switch (tmpQuotaCodeEnum) {
                        case ABOVE_GROUND_CAN_SALE_AREA:
                            return true;
                        case ABOVE_GROUND_CAN_RENT_AREA:
                            return true;
                        case ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA:
                            return true;
                        default:
                            return false;
                    }
                }).map(a -> new BigDecimal(StringUtils.isBlank(a.getQuotaValue()) ? "0" : a.getQuotaValue()))
                        .reduce((a, b) -> a.add(b));
                return reduce.isPresent() ? reduce.get() : new BigDecimal("0");
            }).reduce((a, b) -> a.add(b)).get();
            x.setQuotaValue(houseAboveArea.toString());
        }
    }

    /**
     * 从分期地块信息中汇总分期规划指标信息
     *
     * @param projectQuotaExtendId
     * @param projectQuotaMapList
     */
    private void calcProjectLandPartData(String projectQuotaExtendId, List<BoProjectQuotaMap> projectQuotaMapList) {
        //分期地块信息
        List<BoProjectLandPartMap> projectLandPartList = boProjectLandPartMapService.getProjectLandPartListByProjectQuotaExtendId(projectQuotaExtendId);
        //地块汇总信息
        if (!projectLandPartList.isEmpty()) {
            projectQuotaMapList.parallelStream().forEach(x -> {
                QuotaCodeEnum quotaCodeEnum = QuotaCodeEnum.getByKey(x.getQuotaCode());
                if (quotaCodeEnum == null) {
                    return;
                }
                switch (quotaCodeEnum) {
                    case TOTAL_USE_AREA:
                        //总用地面积 - 来自地块
                        Optional<BigDecimal> totalAreaOpt = projectLandPartList.parallelStream()
                                .map(y -> StringUtils.isBlank(y.getTotalUseLandArea()) ? new BigDecimal("0") : new BigDecimal(y.getTotalUseLandArea()))
                                .reduce((a, b) -> a.add(b));
                        if (totalAreaOpt.isPresent()) {
                            x.setQuotaValue(totalAreaOpt.get().toString());
                        }
                        break;
                    case BUILD_LAND_USE_AREA:
                        //建设用地面积 - 来自地块
                        Optional<BigDecimal> buildUseOpt = projectLandPartList.parallelStream()
                                .map(y -> StringUtils.isBlank(y.getCanUseLandArea()) ? new BigDecimal("0") : new BigDecimal(y.getCanUseLandArea()))
                                .reduce((a, b) -> a.add(b));
                        if (buildUseOpt.isPresent()) {
                            x.setQuotaValue(buildUseOpt.get().toString());
                        }
                        break;
                    case TOTAL_CALC_VOLUME_BUILDING_AREA:
                        //总计容面积 - 来自地块
                        Optional<BigDecimal> totalCalcVolume = projectLandPartList.parallelStream()
                                .map(y -> StringUtils.isBlank(y.getCapacityBuildingArea()) ? new BigDecimal("0") : new BigDecimal(y.getCapacityBuildingArea()))
                                .reduce((a, b) -> a.add(b));
                        if (totalCalcVolume.isPresent()) {
                            x.setQuotaValue(totalCalcVolume.get().toString());
                        }
                        break;
                    case ABOVE_GROUND_CALC_VOLUME_AREA:
                        //地上计容面积 - 来自地块
                        Optional<BigDecimal> aboveCalcVolumeOpt = projectLandPartList.parallelStream()
                                .map(y -> StringUtils.isBlank(y.getAboveGroundCalcVolumeArea()) ? new BigDecimal("0") : new BigDecimal(y.getAboveGroundCalcVolumeArea()))
                                .reduce((a, b) -> a.add(b));
                        if (aboveCalcVolumeOpt.isPresent()) {
                            x.setQuotaValue(aboveCalcVolumeOpt.get().toString());
                        }
                        break;
                    case UNDER_GROUND_CALC_VOLUME_AREA:
                        //地下计容面积 - 来自地块
                        Optional<BigDecimal> underCalcVolume = projectLandPartList.parallelStream()
                                .map(y -> StringUtils.isBlank(y.getUnderGroundCalcVolumeArea()) ? new BigDecimal("0") : new BigDecimal(y.getUnderGroundCalcVolumeArea()))
                                .reduce((a, b) -> a.add(b));
                        if (underCalcVolume.isPresent()) {
                            x.setQuotaValue(underCalcVolume.get().toString());
                        }
                        break;
                    default:
                }
            });
        }
    }

}
