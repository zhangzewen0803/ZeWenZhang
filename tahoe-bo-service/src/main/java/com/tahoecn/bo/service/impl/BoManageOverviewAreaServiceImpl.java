package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.MathUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.BoManageOverviewAreaMapper;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.collection.ConcurrentHashSet;
import com.tahoecn.security.SecureUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 经营概览面积统计表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-07-18
 */
@Service
public class BoManageOverviewAreaServiceImpl extends ServiceImpl<BoManageOverviewAreaMapper, BoManageOverviewArea> implements BoManageOverviewAreaService {

    @Autowired
    private BoProjectPriceExtendService boProjectPriceExtendService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapService boProjectLandPartProductTypeQuotaMapService;


    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoProjectLandPartProductTypeMapService boProjectLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private BoManageOverviewVersionService boManageOverviewVersionService;

    @Autowired
    private BoProjectExtendService boProjectExtendService;

    @Autowired
    private BoBuildingService boBuildingService;

    @Autowired
    private BoBuildingSpeedService boBuildingSpeedService;

    @Autowired
    private BoProjectLandPartMapService boProjectLandPartMapService;

    @Autowired
    private UcOrgService ucOrgService;

    @Autowired
    private SaleRoomStreamService saleRoomStreamService;

    @Autowired
    private BoProjectQuotaMapService boProjectQuotaMapService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${tahoe.uc.land-plate.org.id}")
    private String landPlateId;


    @Override
    @Transactional
    public List<BoManageOverviewArea> makeData(LocalDate localDate) {
        //版本时间/截止时间，统计到此时间为止的货值
        LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

        //创建一个经营概览版本 - 面积版
        BoManageOverviewVersion version = new BoManageOverviewVersion();
        version.setId(UUIDUtils.create());
        version.setCreateTime(LocalDateTime.now());
        version.setVersionDate(localDate);
        version.setVersionType(ManageOverviewVersionTypeEnum.AREA.getKey());

        //Map<项目ID/分期ID,Map<项目KEY/业态KEY/分期KEY/地块KEY,货值>>
        Map<String, Map<String, BoManageOverviewArea>> boManageOverviewAreaMap = new ConcurrentHashMap<>();
        //查询地产板块下所有项目和分期
        List<UcOrg> ucOrgList = ucOrgService.getChildrenProjectAndSubByFdSid(landPlateId);
        if (CollectionUtils.isNotEmpty(ucOrgList)) {
            //遍历计算货值
            ucOrgList.parallelStream().forEach(ucOrg -> {
                //Map<PROJECT_OBJ(项目)/业态CODE(分期下业态)/PROJECT_SUB_OBJ(分期)/地块ID+业态CODE(地块业态)/LAND_PART_OBJ+地块ID（地块）,面积>
                Map<String, BoManageOverviewArea> entryMap = new ConcurrentHashMap<>();
                boManageOverviewAreaMap.put(ucOrg.getFdSid(), entryMap);

                //项目
                if (OrgTypeCodeEnum.UC_LAND_PROJECT.getCode().equals(ucOrg.getFdType())) {
                    //项目投决会面积
                    makeInvestDecisionProductArea(endTime, version, ucOrg, entryMap);

                    //项目下未开发地块面积
                    makeLandPartNotDevelopedArea(endTime, version, ucOrg, entryMap);

                    //未开发地块汇总至项目（包括全盘动态货值与未开发字段）
                    makeProjectAreaCalcLandPart(entryMap);
                    return;
                }

                //分期
                if (OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode().equals(ucOrg.getFdType())) {
                    //业态-全盘动态可售面积、未开工、已开工未达形象进度、已达形象进度未取证、已售、已取证未售
                    makeOverallProductArea(endTime, version, ucOrg, entryMap);

                    //业态-经营决策会面积
                    makeManageDecisionProductArea(endTime, version, ucOrg, entryMap);

                    //业态-规范方案指标
                    makeProductTypeAreaPlanData(endTime,ucOrg,entryMap);

                    //分期-业态面积汇总（取业态数据的和,规划方案指标不汇总）
                    makeProjectSubArea(version, ucOrg, entryMap);

                    //分期 - 规划方案指标
                    makeProjectSubPlanArea(endTime, ucOrg, entryMap);
                    return;
                }

            });

            //项目-将分期货值汇总至项目 - 放最后
            makeProjectPriceCalcProjectSub(boManageOverviewAreaMap, ucOrgList);
        }


        List<BoManageOverviewArea> BoManageOverviewAreaList = boManageOverviewAreaMap.values()
                .parallelStream()
                .flatMap(x -> x.values().parallelStream())
                .collect(Collectors.toList());
        if (localDate.isBefore(LocalDate.now())) {
            //保存入库 - 非当天
            saveManageOverviewVersionAndArea(version, BoManageOverviewAreaList);
        } else {
            //当天存入redis
            redisTemplate.opsForValue().set(RedisConstants.MANAGE_OVERVIEW_AREA_TODAY, BoManageOverviewAreaList);
        }
        return BoManageOverviewAreaList;
    }

    private void makeProjectSubPlanArea(LocalDateTime endTime, UcOrg ucOrg, Map<String, BoManageOverviewArea> entryMap) {
        BoManageOverviewArea boManageOverviewArea = entryMap.get(ManageOverviewPriceKeyTypeEnum.PROJECT_SUB.getKey());
        if (boManageOverviewArea == null) {
            //不存在分期面积对象，则不继续执行
            return;
        }
        List<String> stageCodes = Stream.of(StageCodeEnum.STAGE_02.getKey(), StageCodeEnum.STAGE_03.getKey(), StageCodeEnum.STAGE_04.getKey()).collect(Collectors.toList());
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), stageCodes, endTime);
        if (priceLastPassedVersion == null) {
            //不存在价格版本的不统计
            return;
        }
        List<BoProjectQuotaMap> projectQuotaMapList = boProjectQuotaMapService.getProjectQuotaMapList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(projectQuotaMapList)) {
            //不存在规划方案指标不统计
            return;
        }
        Map<String, BoProjectQuotaMap> quotaCodeAndEntityMap = projectQuotaMapList.parallelStream().collect(Collectors.toMap(BoProjectQuotaMap::getQuotaCode, x -> x, (o, n) -> n));
        //地上、地下实际建筑面积
        BoProjectQuotaMap common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_REAL_BUILDING_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalActualAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_REAL_BUILDING_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalActualAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下建筑面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_TOTAL_BUILDING_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_TOTAL_BUILDING_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下总可售建筑面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalSaleAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalSaleAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下总可租面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalRentAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalRentAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下总赠送面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalGiveAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_GIVE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalGiveAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下总非可租售面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalUnRentAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalUnRentAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //总用地面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TOTAL_USE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalUsedArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //建设用地面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.BUILD_LAND_USE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setBuildingUsedArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //建设占地面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.BUILDING_LAND_COVER_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setBuildingCoverdArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //基地面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.BASE_FLOOR_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setBasalArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下计容建筑面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.ABOVE_GROUND_CALC_VOLUME_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setCapacityAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.UNDER_GROUND_CALC_VOLUME_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setCapacityAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //人防面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.PERSON_PROTECT_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setCivilDefenceArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //非人防面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.NOT_PERSON_PROTECT_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setUnCivilDefenceArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //非人防面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.NOT_PERSON_PROTECT_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setUnCivilDefenceArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //易地人防面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.CHANGE_PLACE_PERSON_PROTECT_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setElesCivilDefenceArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //易地绿地面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.CHANGE_PLACE_GREEN_LAND_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setElesGreenArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //地上、地下代征用地面积(应为代征绿地与代征道路面积，没有地上地下，暂时以地上存绿地，地下存道路)
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TAKE_BY_GREEN_LAND_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalExpropriationAreaAbove(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TAKE_BY_ROAD_LAND_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setTotalExpropriationAreaUnder(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //批量精装修套内面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.BATCH_REFINE_INNER_SET_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setBatchDecorationArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //典型户型精装修套内面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TYPICAL_HOUSE_REFINE_INNER_SET_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setBaseApartmentArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //公区精装修面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.PUBLIC_REGION_REFINE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setPublicDecorationArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //精装修对应实际建筑面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.REFINE_MAP_REAL_BUILDING_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setDecorationForActualArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //代征硬景面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TAKE_BY_HARD_LANDSCAPE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setSubstituteHardArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //区内硬景面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.INNER_REGION_HARD_LANDSCAPE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setIntraHardArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //代征软景面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TAKE_BY_SOFT_LANDSCAPE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setSubstituteSoftArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }

        //区内软景面积
        common = quotaCodeAndEntityMap.get(QuotaCodeEnum.TAKE_BY_SOFT_LANDSCAPE_AREA.getKey());
        if (common != null) {
            boManageOverviewArea.setIntraSoftArea(MathUtils.newBigDecimal(common.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
        }
    }

    @Override
    public void saveManageOverviewVersionAndArea(BoManageOverviewVersion boManageOverviewVersion, Collection<BoManageOverviewArea> boManageOverviewAreas) {
        boManageOverviewVersionService.save(boManageOverviewVersion);

        if (CollectionUtils.isNotEmpty(boManageOverviewAreas)) {
            saveBatch(boManageOverviewAreas);
        }
    }

    private void makeProjectPriceCalcProjectSub(Map<String, Map<String, BoManageOverviewArea>> boManageOverviewAreaMap, List<UcOrg> ucOrgList) {
        ucOrgList.parallelStream().forEach(ucOrg -> {
            //分期
            if (OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode().equals(ucOrg.getFdType())) {
                Map<String, BoManageOverviewArea> parentMap = boManageOverviewAreaMap.get(ucOrg.getFdPsid());
                if (parentMap != null && !parentMap.isEmpty()) {
                    BoManageOverviewArea parentArea = parentMap.get(ManageOverviewPriceKeyTypeEnum.PROJECT.getKey());
                    if (parentArea != null) {
                        Map<String, BoManageOverviewArea> entryMap = boManageOverviewAreaMap.get(ucOrg.getFdSid());
                        entryMap.keySet().parallelStream().filter(x -> x.startsWith(ManageOverviewPriceKeyTypeEnum.PROJECT_SUB.getKey())).map(x -> entryMap.get(x)).forEach(x -> {
                            synchronized (parentArea) {

                                //汇总 - 全盘
                                parentArea.setOverallSaleArea(MathUtils.nullDefaultZero(parentArea.getOverallSaleArea()).add(MathUtils.nullDefaultZero(x.getOverallSaleArea())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 经营决策会
                                parentArea.setManageDecisionSaleArea(MathUtils.nullDefaultZero(parentArea.getManageDecisionSaleArea()).add(MathUtils.nullDefaultZero(x.getManageDecisionSaleArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 未开工 面积
                                parentArea.setUnStartArea(MathUtils.nullDefaultZero(parentArea.getUnStartArea()).add(MathUtils.nullDefaultZero(x.getUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已开工未达形象进度面积
                                parentArea.setUnReachImgGoalArea(MathUtils.nullDefaultZero(parentArea.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(x.getUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已达形象进度未取证面面积
                                parentArea.setUnGetCardArea(MathUtils.nullDefaultZero(parentArea.getUnGetCardArea()).add(MathUtils.nullDefaultZero(x.getUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已取证未售 （存货）面积
                                parentArea.setUnSaleArea(MathUtils.nullDefaultZero(parentArea.getUnSaleArea()).add(MathUtils.nullDefaultZero(x.getUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已售面积
                                parentArea.setSaledArea(MathUtils.nullDefaultZero(parentArea.getSaledArea()).add(MathUtils.nullDefaultZero(x.getSaledArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总实际建筑面积
                                parentArea.setTotalActualAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalActualAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalActualAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总实际建筑面积
                                parentArea.setTotalActualAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalActualAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalActualAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总建筑面积
                                parentArea.setTotalAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总建筑面积
                                parentArea.setTotalAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总可售建筑面积
                                parentArea.setTotalSaleAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalSaleAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalSaleAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总可售建筑面积
                                parentArea.setTotalSaleAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalSaleAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalSaleAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总可租建筑面积
                                parentArea.setTotalRentAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalRentAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalRentAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总可租建筑面积
                                parentArea.setTotalRentAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalRentAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalRentAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总赠送建筑面积
                                parentArea.setTotalGiveAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalGiveAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalGiveAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总赠送建筑面积
                                parentArea.setTotalGiveAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalGiveAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalGiveAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总非可租售建筑面积
                                parentArea.setTotalUnRentAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalUnRentAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalUnRentAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总非可租售建筑面积
                                parentArea.setTotalUnRentAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalUnRentAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalUnRentAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 总用地面积
                                parentArea.setTotalUsedArea(MathUtils.nullDefaultZero(parentArea.getTotalUsedArea()).add(MathUtils.nullDefaultZero(x.getTotalUsedArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 建设用地面积
                                parentArea.setBuildingUsedArea(MathUtils.nullDefaultZero(parentArea.getBuildingUsedArea()).add(MathUtils.nullDefaultZero(x.getBuildingUsedArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 建设占地面积
                                parentArea.setBuildingCoverdArea(MathUtils.nullDefaultZero(parentArea.getBuildingCoverdArea()).add(MathUtils.nullDefaultZero(x.getBuildingCoverdArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 基底面积
                                parentArea.setBasalArea(MathUtils.nullDefaultZero(parentArea.getBasalArea()).add(MathUtils.nullDefaultZero(x.getBasalArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上计容建筑面积
                                parentArea.setCapacityAreaAbove(MathUtils.nullDefaultZero(parentArea.getCapacityAreaAbove()).add(MathUtils.nullDefaultZero(x.getCapacityAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下计容建筑面积
                                parentArea.setCapacityAreaUnder(MathUtils.nullDefaultZero(parentArea.getCapacityAreaUnder()).add(MathUtils.nullDefaultZero(x.getCapacityAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 人防面积
                                parentArea.setCivilDefenceArea(MathUtils.nullDefaultZero(parentArea.getCivilDefenceArea()).add(MathUtils.nullDefaultZero(x.getCivilDefenceArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 非人防面积
                                parentArea.setUnCivilDefenceArea(MathUtils.nullDefaultZero(parentArea.getUnCivilDefenceArea()).add(MathUtils.nullDefaultZero(x.getUnCivilDefenceArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 易地人防面积
                                parentArea.setElesCivilDefenceArea(MathUtils.nullDefaultZero(parentArea.getElesCivilDefenceArea()).add(MathUtils.nullDefaultZero(x.getElesCivilDefenceArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 易地绿地面积
                                parentArea.setElesGreenArea(MathUtils.nullDefaultZero(parentArea.getElesGreenArea()).add(MathUtils.nullDefaultZero(x.getElesGreenArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地上总代征用地面积
                                parentArea.setTotalExpropriationAreaAbove(MathUtils.nullDefaultZero(parentArea.getTotalExpropriationAreaAbove()).add(MathUtils.nullDefaultZero(x.getTotalExpropriationAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 地下总代征用地面积
                                parentArea.setTotalExpropriationAreaUnder(MathUtils.nullDefaultZero(parentArea.getTotalExpropriationAreaUnder()).add(MathUtils.nullDefaultZero(x.getTotalExpropriationAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 批量精装修套内面积
                                parentArea.setBatchDecorationArea(MathUtils.nullDefaultZero(parentArea.getBatchDecorationArea()).add(MathUtils.nullDefaultZero(x.getBatchDecorationArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 典型户型精装修套内面积
                                parentArea.setBaseApartmentArea(MathUtils.nullDefaultZero(parentArea.getBaseApartmentArea()).add(MathUtils.nullDefaultZero(x.getBaseApartmentArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 公区精装修面积
                                parentArea.setPublicDecorationArea(MathUtils.nullDefaultZero(parentArea.getPublicDecorationArea()).add(MathUtils.nullDefaultZero(x.getPublicDecorationArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 精装修对应实际建筑面积
                                parentArea.setDecorationForActualArea(MathUtils.nullDefaultZero(parentArea.getDecorationForActualArea()).add(MathUtils.nullDefaultZero(x.getDecorationForActualArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 代征硬景面积
                                parentArea.setSubstituteHardArea(MathUtils.nullDefaultZero(parentArea.getSubstituteHardArea()).add(MathUtils.nullDefaultZero(x.getSubstituteHardArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 区内硬景面积
                                parentArea.setIntraHardArea(MathUtils.nullDefaultZero(parentArea.getIntraHardArea()).add(MathUtils.nullDefaultZero(x.getIntraHardArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 代征软景面积
                                parentArea.setSubstituteSoftArea(MathUtils.nullDefaultZero(parentArea.getSubstituteSoftArea()).add(MathUtils.nullDefaultZero(x.getSubstituteSoftArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 区内软景面积
                                parentArea.setIntraSoftArea(MathUtils.nullDefaultZero(parentArea.getIntraSoftArea()).add(MathUtils.nullDefaultZero(x.getIntraSoftArea())).setScale(2, RoundingMode.HALF_UP));


                            }
                        });
                    }
                }
                return;
            }
        });
    }

    private Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>>> makeLocalPrice(List<BoBuildingProductTypeMap> buildingProductTypeList, List<BoBuilding> buildingList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList) {
        //本系统 - Map<楼栋origin_id,Map<业态code,Entry<示例楼栋业态对象,Entry<key无意义,总可售面积>>>>
        Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>>> buildingOriginIdProductTypeCodAreaMap = new ConcurrentHashMap<>();
        Set<String> canSaleAreaQuota = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
        buildingList.parallelStream().forEach(building -> {
            Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> productTypeCodePriceMap = buildingOriginIdProductTypeCodAreaMap.get(building.getOriginId());
            if (productTypeCodePriceMap == null) {
                productTypeCodePriceMap = new ConcurrentHashMap<>();
                Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> last = buildingOriginIdProductTypeCodAreaMap.putIfAbsent(building.getOriginId(), productTypeCodePriceMap);
                if (last != null) {
                    productTypeCodePriceMap = last;
                }
            }
            Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> finalProductTypeCodePriceMap = productTypeCodePriceMap;
            buildingProductTypeList.parallelStream()
                    .filter(buildingProductType -> buildingProductType.getBuildingId().equals(building.getId()))
                    .forEach(buildingProductType -> {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> areaEntry = finalProductTypeCodePriceMap.get(buildingProductType.getProductTypeCode());
                        if (areaEntry == null) {
                            areaEntry = new AbstractMap.SimpleEntry<>(buildingProductType, new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO));
                            Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> last = finalProductTypeCodePriceMap.putIfAbsent(buildingProductType.getProductTypeCode(), areaEntry);
                            if (last != null) {
                                areaEntry = last;
                            }
                        }
                        Map.Entry<String, BigDecimal> innerEntry = areaEntry.getValue();
                        buildingProductTypeQuotaList.parallelStream()
                                .filter(boBuildingProductTypeQuotaMap -> boBuildingProductTypeQuotaMap.getBuildingProductTypeId().equals(buildingProductType.getId()))
                                .filter(boBuildingProductTypeQuotaMap -> canSaleAreaQuota.contains(boBuildingProductTypeQuotaMap.getQuotaCode()))
                                .map(boBuildingProductTypeQuotaMap -> MathUtils.newBigDecimal(boBuildingProductTypeQuotaMap.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                .reduce(BigDecimal::add)
                                .ifPresent(entity -> {
                                    synchronized (innerEntry) {
                                        innerEntry.setValue(innerEntry.getValue().add(entity).setScale(2, RoundingMode.HALF_UP));
                                    }
                                });
                    });
        });
        return buildingOriginIdProductTypeCodAreaMap;
    }

    private Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>>> makeSalePrice(List<SaleRoomStream> saleRoomStreamList) {
        //营销系统 - Map<楼栋origin_id,Map<业态code,Entry<key无意义,Entry<key无意义,value为数值>[0：总面积，1：货值]>>>
        Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>>> salePrice = new ConcurrentHashMap<>();
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream().forEach(saleRoomStream -> {
                Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>> productTypeCodePriceMap = salePrice.get(saleRoomStream.getMainDataBldId());
                if (productTypeCodePriceMap == null) {
                    productTypeCodePriceMap = new ConcurrentHashMap<>();
                    Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>> last = salePrice.putIfAbsent(saleRoomStream.getMainDataBldId(), productTypeCodePriceMap);
                    if (last != null) {
                        productTypeCodePriceMap = last;
                    }
                }
                Map.Entry<String, Map.Entry<String, BigDecimal>> priceEntry = productTypeCodePriceMap.get(saleRoomStream.getMainDataProductTypeCode());
                if (priceEntry == null) {
                    priceEntry = new AbstractMap.SimpleEntry<>("", new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO));
                    Map.Entry<String, Map.Entry<String, BigDecimal>> last = productTypeCodePriceMap.putIfAbsent(saleRoomStream.getMainDataProductTypeCode(), priceEntry);
                    if (last != null) {
                        priceEntry = last;
                    }
                }
                Map.Entry<String, BigDecimal> innerEntry = priceEntry.getValue();
                synchronized (innerEntry) {
                    //面积
                    innerEntry.setValue(innerEntry.getValue().add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea())).setScale(2, RoundingMode.HALF_UP));
                }
            });
        }
        return salePrice;
    }

    private String makeBuildingProductTypeKey(SaleRoomStream saleRoomStream) {
        return saleRoomStream.getMainDataBldId() + "," + saleRoomStream.getMainDataProductTypeCode();
    }

    private String makeBuildingProductRoomKey(SaleRoomStream saleRoomStream) {
        return makeBuildingProductTypeKey(saleRoomStream) + "," + saleRoomStream.getRoomGuid();
    }

    private void mergeLocalSale(Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>>> localPriceMap, Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>>> salePriceMap) {
        //合并为动态货值（营销系统的覆盖本系统的）
        if (!salePriceMap.isEmpty()) {
            localPriceMap.keySet().parallelStream().forEach(buildingOriginId -> {
                Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>> saleProductTypeCodePriceMap = salePriceMap.get(buildingOriginId);
                if (saleProductTypeCodePriceMap != null) {
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> localProductTypeCodePriceMap = localPriceMap.get(buildingOriginId);
                    localProductTypeCodePriceMap.keySet().parallelStream().forEach(productTypeCode -> {
                        Map.Entry<String, Map.Entry<String, BigDecimal>> salePriceEntry = saleProductTypeCodePriceMap.get(productTypeCode);
                        if (salePriceEntry != null) {
                            Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> localPriceEntry = localProductTypeCodePriceMap.get(productTypeCode);
                            localPriceEntry.setValue(salePriceEntry.getValue());
                        }
                    });
                }
            });
        }
    }

    private void makeProductTypeAreaPlanData(LocalDateTime endTime, UcOrg ucOrg, Map<String, BoManageOverviewArea> subMap) {
        //业态级的数据计算
        List<String> stageCodes = Stream.of(StageCodeEnum.STAGE_02.getKey(), StageCodeEnum.STAGE_03.getKey(), StageCodeEnum.STAGE_04.getKey()).collect(Collectors.toList());
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), stageCodes, endTime);
        if (priceLastPassedVersion == null) {
            //不存在价格版本的不统计
            return;
        }
        List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(buildingProductTypeList)) {
            //楼栋业态关系数据为空，不统计
            return;
        }
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(buildingList)) {
            //楼栋数据不存在，不统计
            return;
        }
        List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(buildingProductTypeQuotaList)) {
            //楼栋业态指标不存在不统计
            return;
        }
        //Map<业态CODE,Map<指标CODE,指标值汇总值>>
        Set<String> ableQuotaSet = Stream.of(
                QuotaCodeEnum.ABOVE_GROUND_REAL_BUILDING_AREA.getKey(),
                QuotaCodeEnum.UNDER_GROUND_REAL_BUILDING_AREA.getKey(),

                QuotaCodeEnum.ABOVE_GROUND_TOTAL_BUILDING_AREA.getKey(),
                QuotaCodeEnum.UNDER_GROUND_TOTAL_BUILDING_AREA.getKey(),

                QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(),
                QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey(),

                QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA.getKey(),
                QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA.getKey(),

                QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA.getKey(),
                QuotaCodeEnum.UNDER_GROUND_GIVE_AREA.getKey(),

                QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA.getKey(),
                QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA.getKey()
        ).collect(Collectors.toSet());
        Map<String, Map<String, BigDecimal>> productTypeAreaPlanMap = new ConcurrentHashMap<>();
        buildingProductTypeQuotaList.parallelStream()
                .filter(boBuildingProductTypeQuotaMap -> ableQuotaSet.contains(boBuildingProductTypeQuotaMap.getQuotaCode()))
                .forEach(boBuildingProductTypeQuotaMap -> {
                    buildingProductTypeList.parallelStream().forEach(boBuildingProductTypeMap -> {
                        if (boBuildingProductTypeMap.getId().equals(boBuildingProductTypeQuotaMap.getBuildingProductTypeId())) {
                            Map<String, BigDecimal> quotaMap = productTypeAreaPlanMap.get(boBuildingProductTypeMap.getProductTypeCode());
                            if (quotaMap == null) {
                                quotaMap = new ConcurrentHashMap<>();
                                Map<String, BigDecimal> last = productTypeAreaPlanMap.putIfAbsent(boBuildingProductTypeMap.getProductTypeCode(), quotaMap);
                                if (last != null) {
                                    quotaMap = last;
                                }
                            }
                            synchronized (quotaMap){
                                BigDecimal last = quotaMap.get(boBuildingProductTypeQuotaMap.getQuotaCode());
                                BigDecimal newResult = MathUtils.nullDefaultZero(last).add(MathUtils.newBigDecimal(boBuildingProductTypeQuotaMap.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
                                quotaMap.put(boBuildingProductTypeQuotaMap.getQuotaCode(), newResult);
                            }
//                            BigDecimal last = quotaMap.get(boBuildingProductTypeQuotaMap.getQuotaCode());
//                            BigDecimal newResult = MathUtils.nullDefaultZero(last).add(MathUtils.newBigDecimal(boBuildingProductTypeQuotaMap.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
//                            BigDecimal last2 = quotaMap.put(boBuildingProductTypeQuotaMap.getQuotaCode(), newResult);
//                            while (last != last2){
//                                last = last2;
//                                newResult = MathUtils.nullDefaultZero(last).add(MathUtils.newBigDecimal(boBuildingProductTypeQuotaMap.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
//                                last2 = quotaMap.put(boBuildingProductTypeQuotaMap.getQuotaCode(), newResult);
//                            }
                        }
                    });
                });

        productTypeAreaPlanMap.keySet().parallelStream().forEach(productTypeCode->{
            BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + productTypeCode);
            if (boManageOverviewArea != null){
                Map<String, BigDecimal> quotaMap = productTypeAreaPlanMap.get(productTypeCode);
                //地上、地下实际建筑面积
                BigDecimal common = quotaMap.get(QuotaCodeEnum.ABOVE_GROUND_REAL_BUILDING_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalActualAreaAbove(common);
                }
                common = quotaMap.get(QuotaCodeEnum.UNDER_GROUND_REAL_BUILDING_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalActualAreaUnder(common);
                }

                //地上、地下建筑面积
                common = quotaMap.get(QuotaCodeEnum.ABOVE_GROUND_TOTAL_BUILDING_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalAreaAbove(common);
                }
                common = quotaMap.get(QuotaCodeEnum.UNDER_GROUND_TOTAL_BUILDING_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalAreaUnder(common);
                }

                //地上、地下总可售建筑面积
                common = quotaMap.get(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalSaleAreaAbove(common);
                }
                common = quotaMap.get(QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalSaleAreaUnder(common);
                }

                //地上、地下总可租面积
                common = quotaMap.get(QuotaCodeEnum.ABOVE_GROUND_CAN_RENT_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalRentAreaAbove(common);
                }
                common = quotaMap.get(QuotaCodeEnum.UNDER_GROUND_CAN_RENT_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalRentAreaUnder(common);
                }

                //地上、地下总赠送面积
                common = quotaMap.get(QuotaCodeEnum.ABOVE_GROUND_GIVE_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalGiveAreaAbove(common);
                }
                common = quotaMap.get(QuotaCodeEnum.UNDER_GROUND_GIVE_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalGiveAreaUnder(common);
                }

                //地上、地下总非可租售面积
                common = quotaMap.get(QuotaCodeEnum.ABOVE_GROUND_CAN_NOT_RENT_SALE_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalUnRentAreaAbove(common);
                }
                common = quotaMap.get(QuotaCodeEnum.UNDER_GROUND_CAN_NOT_RENT_SALE_AREA.getKey());
                if (common != null) {
                    boManageOverviewArea.setTotalUnRentAreaUnder(common);
                }
            }
        });
    }

    private void makeOverallProductArea(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> subMap) {
        //业态级的数据计算
        List<String> stageCodes = Stream.of(StageCodeEnum.STAGE_02.getKey(), StageCodeEnum.STAGE_03.getKey(), StageCodeEnum.STAGE_04.getKey()).collect(Collectors.toList());
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), stageCodes, endTime);
        if (priceLastPassedVersion == null) {
            //不存在价格版本的不统计
            return;
        }
        List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(buildingProductTypeList)) {
            //楼栋业态关系数据为空，不统计
            return;
        }
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(buildingList)) {
            //楼栋数据不存在，不统计
            return;
        }
        List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(priceLastPassedVersion.getProjectQuotaExtendId());
        if (CollectionUtils.isEmpty(buildingProductTypeQuotaList)) {
            //楼栋业态指标不存在不统计
            return;
        }
        //本系统 - Map<楼栋origin_id,Map<业态code,Entry<示例楼栋业态对象,Entry<key无意义,value为数值>[0：总可售面积/车位数，1：货值]>>>
        Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>>> localPriceMap = makeLocalPrice(buildingProductTypeList, buildingList, buildingProductTypeQuotaList);

        //查营销房间
        List<SaleRoomStream> saleRoomStreamList = saleRoomStreamService.getLastSaleRoomStreamListByBuildingOriginIds(localPriceMap.keySet(), endTime);
        //营销未完全定价的楼栋业态以及，定价部分的面积
        //Map<楼栋业态,Entry<key无意义，value:面积>>
        Map<String, Map.Entry<String, BigDecimal>> disableSaleBuildingProductTypeAreaMap = new ConcurrentHashMap<>();
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //计算未全部定价的房间货值部分
            saleRoomStreamList.parallelStream().forEach(saleRoomStream -> {
                if (saleRoomStream.getRoomTotal() == null || saleRoomStream.getRoomTotal().doubleValue() == 0) {
                    String key = makeBuildingProductTypeKey(saleRoomStream);
                    Map.Entry<String, BigDecimal> entry = disableSaleBuildingProductTypeAreaMap.get(key);
                    if (entry == null) {
                        entry = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                        Map.Entry<String, BigDecimal> last = disableSaleBuildingProductTypeAreaMap.putIfAbsent(key, entry);
                        if (last != null) {
                            entry = last;
                        }
                    }
                    synchronized (entry) {
                        entry.setValue(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()).add(entry.getValue()).setScale(2, RoundingMode.HALF_UP));
                    }
                }
            });
            //全部定价的房间
            List<SaleRoomStream> ableRoomList = saleRoomStreamList.parallelStream().filter(saleRoomStream -> !disableSaleBuildingProductTypeAreaMap.containsKey(makeBuildingProductTypeKey(saleRoomStream))).collect(Collectors.toList());
            //营销系统 - Map<楼栋origin_id,Map<业态code,Entry<key无意义,Entry<key无意义,value为数值>[0：总可售面积/车位数，1：货值]>>>
            Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>>>> salePriceMap = makeSalePrice(ableRoomList);
            mergeLocalSale(localPriceMap, salePriceMap);

        }


        //生成全盘动态货值对象
        localPriceMap.values().parallelStream().forEach(productTypeCodePriceMap -> {
            productTypeCodePriceMap.keySet().parallelStream().forEach(productTypeCode -> {
                Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> priceEntry = productTypeCodePriceMap.get(productTypeCode);
                String key = ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + productTypeCode;
                BoManageOverviewArea boManageOverviewArea = subMap.get(key);
                if (boManageOverviewArea == null) {
                    boManageOverviewArea = new BoManageOverviewArea();
                    BoManageOverviewArea last = subMap.putIfAbsent(key, boManageOverviewArea);
                    if (last == null) {
                        boManageOverviewArea.setId(UUIDUtils.create());
                        boManageOverviewArea.setCreateTime(LocalDateTime.now());
                        boManageOverviewArea.setOrgId(SecureUtil.md5(priceLastPassedVersion.getId() + productTypeCode));
                        boManageOverviewArea.setOrgName(priceEntry.getKey().getProductTypeName());
                        boManageOverviewArea.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                        boManageOverviewArea.setProductTypeCode(productTypeCode);
                        boManageOverviewArea.setParentOrgId(ucOrg.getFdSid());
                        boManageOverviewArea.setParentOrgName(ucOrg.getFdName());
                        boManageOverviewArea.setManageOverviewVersionId(version.getId());
                        boManageOverviewArea.setVersionDate(version.getVersionDate());
                    } else {
                        boManageOverviewArea = last;
                    }
                }
                //相同业态货值汇总到一块
                synchronized (boManageOverviewArea) {
                    Map.Entry<String, BigDecimal> innerEntry = priceEntry.getValue();
                    boManageOverviewArea.setOverallSaleArea(MathUtils.nullDefaultZero(boManageOverviewArea.getOverallSaleArea()).add(innerEntry.getValue()).setScale(2, RoundingMode.HALF_UP));
                }
            });
        });

        //楼栋进度
        List<BoBuildingSpeed> buildingSpeedList = boBuildingSpeedService.getLastBuildingSpeed(localPriceMap.keySet(), endTime);

        //所有填写了楼栋进度的ID
        Set<String> allSpeedBuildingIdSet = buildingSpeedList.parallelStream().map(boBuildingSpeed -> boBuildingSpeed.getBuildId()).collect(Collectors.toSet());

        //所有已达形象进度的楼栋ID
        Set<String> reachGoalBuildingIdSet = buildingSpeedList.parallelStream()
                .filter(boBuildingSpeed -> boBuildingSpeed.getIsRequirements() != null && boBuildingSpeed.getIsRequirements() == IsRequirementsEnum.YES.getKey())
                .map(boBuildingSpeed -> boBuildingSpeed.getBuildId()).collect(Collectors.toSet());

        //所有已开工未达形象进度的楼栋ID
        Set<String> startedUnReachGoalBuildingIdSet = buildingSpeedList.parallelStream()
                .filter(boBuildingSpeed -> boBuildingSpeed.getIsStart() != null && boBuildingSpeed.getIsStart() == IsStartEnum.YES.getKey())
                .filter(boBuildingSpeed -> boBuildingSpeed.getIsRequirements() == null || boBuildingSpeed.getIsRequirements() == IsRequirementsEnum.NO.getKey())
                .map(boBuildingSpeed -> boBuildingSpeed.getBuildId()).collect(Collectors.toSet());

        //所有未开工的楼栋ID
        Set<String> unStartedBuildingIdSet = buildingSpeedList.parallelStream()
                .filter(boBuildingSpeed -> boBuildingSpeed.getIsStart() == null || boBuildingSpeed.getIsStart() == IsStartEnum.NO.getKey())
                .map(boBuildingSpeed -> boBuildingSpeed.getBuildId()).collect(Collectors.toSet());


        //本系统所有的楼栋业态
        Set<String> overall = new ConcurrentHashSet<>();
        localPriceMap.keySet().parallelStream()
                .forEach(buildingId -> {
                    localPriceMap.get(buildingId).keySet().forEach(productTypeCode -> {
                        overall.add(buildingId + "," + productTypeCode);
                    });
                });

        //分已售、已拿证未售、已达形象进度未取证、已开工未达形象进度、未开工
        Set<String> saledBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> getCardUnSaleBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> reachGoalUnGetCardBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> staredUnReachGoalBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> unStartedBuildingProductTypeSet = new ConcurrentHashSet<>();

        //已售
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> SaleRoomStreamStatusEnum.SIGNING.getKey().equals(saleRoomStream.getStatus()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        saledBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewArea != null) {
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getSaledArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                boManageOverviewArea.setSaledArea(area);
                            }
                        }
                    });
        }

        //已拿证未售
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !SaleRoomStreamStatusEnum.SIGNING.getKey().equals(saleRoomStream.getStatus()))
                    .filter(saleRoomStream -> saleRoomStream.getRealTakeCardTime() != null && saleRoomStream.getRealTakeCardTime().isBefore(endTime))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        getCardUnSaleBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewArea != null) {
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnSaleArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                boManageOverviewArea.setUnSaleArea(area);
                            }
                        }
                    });

        }

        //已达形象进度未拿证
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> saleRoomStream.getRealTakeCardTime() == null || saleRoomStream.getRealTakeCardTime().isAfter(endTime))
                    .filter(saleRoomStream -> reachGoalBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        reachGoalUnGetCardBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewArea != null) {
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                boManageOverviewArea.setUnGetCardArea(area);
                            }
                        }
                    });
        }

        overall.parallelStream()
                .filter(buildingProductType -> saledBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> getCardUnSaleBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> reachGoalUnGetCardBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> reachGoalBuildingIdSet.contains(buildingProductType.split(",")[0]))
                .forEach(buildingProductType -> {
                    reachGoalUnGetCardBuildingProductTypeSet.add(buildingProductType);
                    String[] split = buildingProductType.split(",");
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            //如果是销售系统未定价部分的楼栋业态，仅处理本系统与销售系统的差值部分
                            makeLocalDisableAreaSubtract(disableSaleBuildingProductTypeAreaMap, buildingProductType, entry);
                            BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnGetCardArea()).add(entry.getValue().getValue());
                                boManageOverviewArea.setUnGetCardArea(area);
                            }
                        }
                    }
                });

        //已开工未达形象进度
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !reachGoalUnGetCardBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> startedUnReachGoalBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        staredUnReachGoalBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewArea != null) {
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                boManageOverviewArea.setUnReachImgGoalArea(area);
                            }
                        }
                    });
        }

        overall.parallelStream()
                .filter(buildingProductType -> saledBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> getCardUnSaleBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> reachGoalUnGetCardBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> staredUnReachGoalBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> startedUnReachGoalBuildingIdSet.contains(buildingProductType.split(",")[0]))
                .forEach(buildingProductType -> {
                    staredUnReachGoalBuildingProductTypeSet.add(buildingProductType);
                    String[] split = buildingProductType.split(",");
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            //如果是销售系统未定价部分的楼栋业态，仅处理本系统与销售系统的差值部分
                            makeLocalDisableAreaSubtract(disableSaleBuildingProductTypeAreaMap, buildingProductType, entry);

                            BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnReachImgGoalArea()).add(entry.getValue().getValue());
                                boManageOverviewArea.setUnReachImgGoalArea(area);
                            }
                        }
                    }
                });

        //未开工
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !reachGoalUnGetCardBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !staredUnReachGoalBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> unStartedBuildingIdSet.contains(saleRoomStream.getMainDataBldId()) || !allSpeedBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        unStartedBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewArea != null) {
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnSaleArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                boManageOverviewArea.setUnStartArea(area);
                            }
                        }
                    });
        }

        overall.parallelStream()
                .filter(buildingProductType -> saledBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> getCardUnSaleBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> reachGoalUnGetCardBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> staredUnReachGoalBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> unStartedBuildingProductTypeSet.parallelStream().noneMatch(key -> key.startsWith(buildingProductType)))
                .filter(buildingProductType -> unStartedBuildingIdSet.contains(buildingProductType.split(",")[0]) || !allSpeedBuildingIdSet.contains(buildingProductType.split(",")[0]))
                .forEach(buildingProductType -> {
                    unStartedBuildingProductTypeSet.add(buildingProductType);
                    String[] split = buildingProductType.split(",");
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            //如果是销售系统未定价部分的楼栋业态，仅处理本系统与销售系统的差值部分
                            makeLocalDisableAreaSubtract(disableSaleBuildingProductTypeAreaMap, buildingProductType, entry);

                            BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewArea) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewArea.getUnStartArea()).add(entry.getValue().getValue());
                                boManageOverviewArea.setUnStartArea(area);
                            }
                        }
                    }
                });
    }

    private void makeProjectSubArea(BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> entryMap) {
        BoManageOverviewArea boManageOverviewArea = new BoManageOverviewArea();
        boManageOverviewArea.setId(UUIDUtils.create());
        boManageOverviewArea.setCreateTime(LocalDateTime.now());
        boManageOverviewArea.setOrgId(ucOrg.getFdSid());
        boManageOverviewArea.setOrgName(ucOrg.getFdName());
        boManageOverviewArea.setParentOrgId(ucOrg.getFdPsid());
        boManageOverviewArea.setParentOrgName(ucOrg.getFdPname());
        boManageOverviewArea.setOrgType(ucOrg.getFdType());
        boManageOverviewArea.setManageOverviewVersionId(version.getId());
        boManageOverviewArea.setVersionDate(version.getVersionDate());
        entryMap.put(ManageOverviewPriceKeyTypeEnum.PROJECT_SUB.getKey(), boManageOverviewArea);

        entryMap.keySet().parallelStream()
                .filter(x -> x.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey()))
                .map(x -> entryMap.get(x))
                .forEach(x -> {
                    synchronized (boManageOverviewArea) {
                        //汇总 - 全盘
                        boManageOverviewArea.setOverallSaleArea(MathUtils.nullDefaultZero(boManageOverviewArea.getOverallSaleArea()).add(MathUtils.nullDefaultZero(x.getOverallSaleArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 经营决策会
                        boManageOverviewArea.setManageDecisionSaleArea(MathUtils.nullDefaultZero(boManageOverviewArea.getManageDecisionSaleArea()).add(MathUtils.nullDefaultZero(x.getManageDecisionSaleArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 未开发
                        boManageOverviewArea.setUnDevelopArea(MathUtils.nullDefaultZero(boManageOverviewArea.getUnDevelopArea()).add(MathUtils.nullDefaultZero(x.getUnDevelopArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 未开工
                        boManageOverviewArea.setUnStartArea(MathUtils.nullDefaultZero(boManageOverviewArea.getUnStartArea()).add(MathUtils.nullDefaultZero(x.getUnStartArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已开工未达形象进度
                        boManageOverviewArea.setUnReachImgGoalArea(MathUtils.nullDefaultZero(boManageOverviewArea.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(x.getUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已达形象进度未取证
                        boManageOverviewArea.setUnGetCardArea(MathUtils.nullDefaultZero(boManageOverviewArea.getUnGetCardArea()).add(MathUtils.nullDefaultZero(x.getUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已取证未售
                        boManageOverviewArea.setUnSaleArea(MathUtils.nullDefaultZero(boManageOverviewArea.getUnSaleArea()).add(MathUtils.nullDefaultZero(x.getUnSaleArea())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已售
                        boManageOverviewArea.setSaledArea(MathUtils.nullDefaultZero(boManageOverviewArea.getSaledArea()).add(MathUtils.nullDefaultZero(x.getSaledArea())).setScale(2, RoundingMode.HALF_UP));
                    }
                });
    }

    private void makeLocalDisableAreaSubtract(Map<String, Map.Entry<String, BigDecimal>> disableSaleBuildingProductTypePriceMap, String buildingProductType, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>> entry) {
        Map.Entry<String, BigDecimal> saleEntry = disableSaleBuildingProductTypePriceMap.get(buildingProductType);
        if (saleEntry != null) {
            Map.Entry<String, BigDecimal> localValue = entry.getValue();
            localValue.setValue(localValue.getValue().subtract(saleEntry.getValue()));
        }
    }


    private void makeProjectAreaCalcLandPart(Map<String, BoManageOverviewArea> entryMap) {
        BoManageOverviewArea projectArea = entryMap.get(ManageOverviewPriceKeyTypeEnum.PROJECT.getKey());
        List<BoManageOverviewArea> landPartArea = entryMap.keySet().parallelStream().filter(x -> x.startsWith(ManageOverviewPriceKeyTypeEnum.LAND_PART.getKey())).map(x -> entryMap.get(x)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(landPartArea)) {
            //全盘
            landPartArea.stream().map(x -> MathUtils.nullDefaultZero(x.getOverallSaleArea())).reduce(BigDecimal::add)
                    .ifPresent(x -> {
                        projectArea.setOverallSaleArea(MathUtils.nullDefaultZero(projectArea.getOverallSaleArea()).add(x).setScale(2, RoundingMode.HALF_UP));
                    });
            //未开发面积
            landPartArea.stream().map(x -> MathUtils.nullDefaultZero(x.getUnDevelopArea())).reduce(BigDecimal::add)
                    .ifPresent(x -> {
                        projectArea.setUnDevelopArea(MathUtils.nullDefaultZero(projectArea.getUnDevelopArea()).add(x).setScale(2, RoundingMode.HALF_UP));
                    });
            //未开发
            projectArea.setUnDevelopArea(projectArea.getOverallSaleArea());
        }
    }

    private void makeManageDecisionProductArea(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> subMap) {
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), Stream.of(StageCodeEnum.STAGE_02.getKey()).collect(Collectors.toList()), endTime);
        if (priceLastPassedVersion != null) {
            List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(priceLastPassedVersion.getProjectQuotaExtendId());
            if (CollectionUtils.isNotEmpty(buildingProductTypeList)) {
                //Map<业态code,Entry<示例楼栋业态,总可售面积>>
                Map<String, Map.Entry<BoBuildingProductTypeMap, BigDecimal>> map = new ConcurrentHashMap<>();
                List<BoBuildingProductTypeQuotaMap> landPartProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(priceLastPassedVersion.getProjectQuotaExtendId());
                Set<String> areaQuotaSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
                buildingProductTypeList.parallelStream().forEach(bpt -> {
                    landPartProductTypeQuotaList.parallelStream().forEach(quota -> {
                        if (!bpt.getId().equals(quota.getBuildingProductTypeId())) {
                            return;
                        }
                        if (!areaQuotaSet.contains(quota.getQuotaCode())) {
                            return;
                        }
                        Map.Entry<BoBuildingProductTypeMap, BigDecimal> entry = map.get(bpt.getProductTypeCode());
                        if (entry == null) {
                            entry = new AbstractMap.SimpleEntry<>(bpt, BigDecimal.ZERO);
                            Map.Entry<BoBuildingProductTypeMap, BigDecimal> last = map.putIfAbsent(bpt.getProductTypeCode(), entry);
                            if (last != null) {
                                entry = last;
                            }
                        }
                        synchronized (entry) {
                            entry.setValue(entry.getValue().add(MathUtils.newBigDecimal(quota.getQuotaValue()).setScale(2, RoundingMode.HALF_UP)));
                        }
                    });
                });
                map.values().parallelStream().forEach(entry -> {
                    BoManageOverviewArea boManageOverviewArea = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + entry.getKey().getProductTypeCode());
                    if (boManageOverviewArea == null) {
                        boManageOverviewArea = new BoManageOverviewArea();
                        boManageOverviewArea.setId(UUIDUtils.create());
                        boManageOverviewArea.setCreateTime(LocalDateTime.now());
                        boManageOverviewArea.setOrgId(SecureUtil.md5(entry.getKey().getProductTypeCode()));
                        boManageOverviewArea.setOrgName(entry.getKey().getProductTypeName());
                        boManageOverviewArea.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                        boManageOverviewArea.setParentOrgId(ucOrg.getFdSid());
                        boManageOverviewArea.setParentOrgName(ucOrg.getFdName());
                        boManageOverviewArea.setManageOverviewVersionId(version.getId());
                        boManageOverviewArea.setVersionDate(version.getVersionDate());
                        boManageOverviewArea.setManageDecisionSaleArea(BigDecimal.ZERO);
                        BoManageOverviewArea last = subMap.putIfAbsent(OrgTypeCodeEnum.PRODUCT_TYPE.getCode() + entry.getKey().getProductTypeCode(), boManageOverviewArea);
                        if (last != null) {
                            boManageOverviewArea = last;
                        }
                    }
                    boManageOverviewArea.setManageDecisionSaleArea(entry.getValue());
                });
            }
        }
    }

    private void makeInvestDecisionProductArea(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> entryMap) {
        BoManageOverviewArea boManageOverviewArea = new BoManageOverviewArea();
        boManageOverviewArea.setId(UUIDUtils.create());
        boManageOverviewArea.setCreateTime(LocalDateTime.now());
        boManageOverviewArea.setOrgId(ucOrg.getFdSid());
        boManageOverviewArea.setOrgName(ucOrg.getFdName());
        boManageOverviewArea.setOrgType(ucOrg.getFdType());
        boManageOverviewArea.setParentOrgId(ucOrg.getFdPsid());
        boManageOverviewArea.setParentOrgName(ucOrg.getFdPname());
        boManageOverviewArea.setManageOverviewVersionId(version.getId());
        boManageOverviewArea.setVersionDate(version.getVersionDate());
        boManageOverviewArea.setInvestDecisionSaleArea(BigDecimal.ZERO);
        entryMap.put(ManageOverviewPriceKeyTypeEnum.PROJECT.getKey(), boManageOverviewArea);
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), Stream.of(StageCodeEnum.STAGE_01.getKey()).collect(Collectors.toList()), endTime);
        if (priceLastPassedVersion != null) {
            List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaListByPriceVersionId(priceLastPassedVersion.getId());
            Set<String> areaQuotaSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
            landPartProductTypeQuotaList.parallelStream().filter(x -> areaQuotaSet.contains(x.getQuotaCode()))
                    .map(x -> MathUtils.newBigDecimal(x.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                    .reduce(BigDecimal::add)
                    .ifPresent(x -> {
                        boManageOverviewArea.setInvestDecisionSaleArea(x.setScale(2, RoundingMode.HALF_UP));
                    });
        }
    }

    private void makeLandPartNotDevelopedArea(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> entryMap) {
        //查项目投决会阶段的地块业态以及价格信息
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), Stream.of(StageCodeEnum.STAGE_01.getKey()).collect(Collectors.toList()), endTime);
        if (priceLastPassedVersion != null) {
            List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeListByPriceVersionId(priceLastPassedVersion.getId());
            if (CollectionUtils.isNotEmpty(landPartProductTypeList)) {
                List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaListByPriceVersionId(priceLastPassedVersion.getId());
                if (CollectionUtils.isNotEmpty(landPartProductTypeQuotaList)) {
                    //地块业态总可售面积 - 投决会
                    //Map<地块ID,Map<业态CODE,Entry<样例地块业态对象,总可售面积>>>
                    Map<String, Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>>> landPartIdAndLandPartProductTypeMap = new HashMap<>();
                    //地上、地下可售面积指标、车位数
                    Set<String> areaQuotaSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
                    landPartProductTypeList.stream().forEach(x -> {
                        Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>> map = landPartIdAndLandPartProductTypeMap.get(x.getLandPartId());
                        if (map == null) {
                            map = new HashMap<>();
                            landPartIdAndLandPartProductTypeMap.put(x.getLandPartId(), map);
                        }
                        Map.Entry<BoLandPartProductTypeMap, BigDecimal> entry = map.get(x.getProductTypeCode());
                        if (entry == null) {
                            entry = new AbstractMap.SimpleEntry<>(x, BigDecimal.ZERO);
                            map.put(x.getProductTypeCode(), entry);
                        }
                        Map.Entry<BoLandPartProductTypeMap, BigDecimal> finalEntry = entry;
                        //可售面积计算
                        landPartProductTypeQuotaList.parallelStream()
                                .filter(y -> areaQuotaSet.contains(y.getQuotaCode()) && x.getId().equals(y.getLandPartProductTypeId()))
                                .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                .reduce(BigDecimal::add)
                                .ifPresent(y -> {
                                    BigDecimal prevVal = finalEntry.getValue();
                                    finalEntry.setValue(prevVal.add(y).setScale(2, RoundingMode.HALF_UP));
                                });
                    });

                    //地块业态总可售面积 - 分期
                    //Map<地块ID,Map<业态CODE,Entry<样例地块业态对象,总可售面积>>>
                    Map<String, Map<String, Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal>>> projectLandPartIdAndLandPartProductTypeMap = new HashMap<>();
                    List<BoProjectExtend> projectSubLastPassedVersionList = boProjectExtendService.getProjectSubLastPassedVersionListByProjectId(ucOrg.getFdSid());
                    if (CollectionUtils.isNotEmpty(projectSubLastPassedVersionList)) {
                        List<String> projectExtendIds = projectSubLastPassedVersionList.stream().map(x -> x.getId()).collect(Collectors.toList());
                        List<BoProjectLandPartMap> projectLandPartList = boProjectLandPartMapService.getProjectLandPartListByProjectExtendIds(projectExtendIds);
                        if (CollectionUtils.isNotEmpty(projectLandPartList)) {
                            //便于查询 分期地块映射ID与地块的映射关系
                            Map<String, String> projectLandPartIdAndLandPartIdMap = projectLandPartList.parallelStream().collect(Collectors.toMap(BoProjectLandPartMap::getId, BoProjectLandPartMap::getLandPartId, (o, n) -> n));
                            List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeList = boProjectLandPartProductTypeMapService.getProjectLandPartProductTypeListByProjectExtendIds(projectExtendIds);
                            if (CollectionUtils.isNotEmpty(projectLandPartProductTypeList)) {
                                List<BoProjectLandPartProductTypeQuotaMap> projectLandPartProductQuotaList = boProjectLandPartProductTypeQuotaMapService.getListByProjectLandPartProductTypeIds(projectLandPartProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toList()));
                                if (CollectionUtils.isNotEmpty(projectLandPartProductQuotaList)) {
                                    projectLandPartProductTypeList.stream().forEach(x -> {
                                        String landPartId = projectLandPartIdAndLandPartIdMap.get(x.getProjectLandPartId());
                                        if (StringUtils.isBlank(landPartId)) {
                                            return;
                                        }
                                        Map<String, Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal>> map = projectLandPartIdAndLandPartProductTypeMap.get(landPartId);
                                        if (map == null) {
                                            map = new HashMap<>();
                                            projectLandPartIdAndLandPartProductTypeMap.put(landPartId, map);
                                        }
                                        Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal> entry = map.get(x.getProductTypeCode());
                                        if (entry == null) {
                                            entry = new AbstractMap.SimpleEntry<>(x, BigDecimal.ZERO);
                                            map.put(x.getProductTypeCode(), entry);
                                        }
                                        Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal> finalEntry = entry;

                                        //可售面积计算
                                        projectLandPartProductQuotaList.parallelStream()
                                                .filter(y -> areaQuotaSet.contains(y.getQuotaCode()) && x.getId().equals(y.getProjectLandPartProductTypeId()))
                                                .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                                .reduce(BigDecimal::add)
                                                .ifPresent(y -> {
                                                    BigDecimal prevVal = finalEntry.getValue();
                                                    finalEntry.setValue(prevVal.add(y).setScale(2, RoundingMode.HALF_UP));
                                                });

                                    });
                                }
                            }
                        }
                    }

                    //比较投决会与分期中的地块业态总可售面积，如果后者小于前者，则表示未全部开发，则生成地块的统计信息
                    landPartIdAndLandPartProductTypeMap.forEach((landId, investAreaMap) -> {
                        Map<String, Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal>> projectSubAreaMap = projectLandPartIdAndLandPartProductTypeMap.get(landId);
                        if (projectSubAreaMap == null) {
                            //map2未空表示分期并未规划该地块，则该地块需要加入统计
                            makeLandIfProjectSubNotUse(version, ucOrg, entryMap, landId, investAreaMap);
                        } else {
                            //map2不为空表示分期已规划地块，需要减去分期规划的可售面积
                            makeLandPriceIfProjectSubUsed(version, ucOrg, entryMap, landId, investAreaMap, projectSubAreaMap);
                        }
                    });

                }
            }
        }

    }

    private void makeLandIfProjectSubNotUse(BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> entryMap, String landId, Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>> investAreaMap) {
        BoManageOverviewArea landArea = new BoManageOverviewArea();
        landArea.setId(UUIDUtils.create());
        landArea.setCreateTime(LocalDateTime.now());
        landArea.setOrgId(landId);
        landArea.setOrgName(investAreaMap.values().stream().findFirst().get().getKey().getLandPartName());
        landArea.setParentOrgId(ucOrg.getFdSid());
        landArea.setParentOrgName(ucOrg.getFdName());
        landArea.setOrgType(OrgTypeCodeEnum.LAND_PART.getCode());
        landArea.setManageOverviewVersionId(version.getId());
        landArea.setVersionDate(version.getVersionDate());
        landArea.setOverallSaleArea(BigDecimal.ZERO);
        landArea.setInvestDecisionSaleArea(BigDecimal.ZERO);
        entryMap.put(ManageOverviewPriceKeyTypeEnum.LAND_PART.getKey() + landId, landArea);
        //计算地块下未开发业态的货值
        investAreaMap.forEach((productTypeCode, entry) -> {
            BoManageOverviewArea landPtcArea = new BoManageOverviewArea();
            landPtcArea.setId(UUIDUtils.create());
            landPtcArea.setCreateTime(LocalDateTime.now());
            landPtcArea.setOrgId(SecureUtil.md5(landId + productTypeCode));
            landPtcArea.setOrgName(entry.getKey().getProductTypeName());
            landPtcArea.setParentOrgId(landId);
            landPtcArea.setParentOrgName(landArea.getOrgName());
            landPtcArea.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
            landPtcArea.setManageOverviewVersionId(version.getId());
            landPtcArea.setVersionDate(version.getVersionDate());
            //均价单位为元，结果需除以10000，转为货值单位 万元
            landPtcArea.setOverallSaleArea(entry.getValue());
            landPtcArea.setUnDevelopArea(entry.getValue());
            entryMap.put(landId + productTypeCode, landPtcArea);

            //汇总到地块
            landArea.setOverallSaleArea(MathUtils.nullDefaultZero(landArea.getOverallSaleArea()).add(landPtcArea.getOverallSaleArea().setScale(2, RoundingMode.HALF_UP)));
            landArea.setUnDevelopArea(landArea.getOverallSaleArea());
        });
    }

    private void makeLandPriceIfProjectSubUsed(BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewArea> entryMap, String landId, Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>> investAreaMap, Map<String, Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal>> projectSubAreaMap) {
        //表示有剩余未开发
        BoManageOverviewArea landArea = new BoManageOverviewArea();
        landArea.setId(UUIDUtils.create());
        landArea.setCreateTime(LocalDateTime.now());
        landArea.setOrgId(landId);
        landArea.setOrgName(investAreaMap.values().stream().findFirst().get().getKey().getLandPartName());
        landArea.setParentOrgId(ucOrg.getFdSid());
        landArea.setParentOrgName(ucOrg.getFdName());
        landArea.setOrgType(OrgTypeCodeEnum.LAND_PART.getCode());
        landArea.setManageOverviewVersionId(version.getId());
        landArea.setVersionDate(version.getVersionDate());
        landArea.setOverallSaleArea(BigDecimal.ZERO);
        landArea.setInvestDecisionSaleArea(BigDecimal.ZERO);
        entryMap.put(ManageOverviewPriceKeyTypeEnum.LAND_PART.getKey() + landId, landArea);
        investAreaMap.forEach((productTypeCode, entry1) -> {
            Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal> entry2 = projectSubAreaMap.get(productTypeCode);
            //若entry2为空则表示分期下该地块的次业态未规划面积，那么试做规划为0
            BigDecimal investArea = entry1.getValue();
            BigDecimal subArea = entry2 == null ? BigDecimal.ZERO : entry2.getValue();
            BigDecimal result = investArea.subtract(subArea).setScale(2, RoundingMode.HALF_UP);
            //投决会的大于分期的，则表示有剩余，需统计货值，反之则无，则不需统计货值
            if (result.doubleValue() > 0) {
                String refId = SecureUtil.md5(landId + productTypeCode);
                BoManageOverviewArea landPtcArea = new BoManageOverviewArea();
                landPtcArea.setId(UUIDUtils.create());
                landPtcArea.setCreateTime(LocalDateTime.now());
                landPtcArea.setOrgId(refId);
                landPtcArea.setOrgName(entry1.getKey().getProductTypeName());
                landPtcArea.setParentOrgId(landId);
                landPtcArea.setParentOrgName(landArea.getOrgName());
                landPtcArea.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                landPtcArea.setManageOverviewVersionId(version.getId());
                landPtcArea.setVersionDate(version.getVersionDate());
                landPtcArea.setOverallSaleArea(result);
                landPtcArea.setUnDevelopArea(result);
                entryMap.put(landId + productTypeCode, landPtcArea);

                //汇总到地块
                landArea.setOverallSaleArea(MathUtils.nullDefaultZero(landArea.getOverallSaleArea()).add(landPtcArea.getOverallSaleArea().setScale(2, RoundingMode.HALF_UP)));
                landArea.setUnDevelopArea(landArea.getOverallSaleArea());
            }
        });
    }

    @Override
    public List<BoManageOverviewArea> getListByVersionIdAndUcOrgList(String versionId, String parentOrgId) {
        QueryWrapper<BoManageOverviewArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manage_overview_version_id", versionId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(queryWrapper);
    }
}
