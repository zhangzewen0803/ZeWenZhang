package com.tahoecn.bo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.MathUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.BoManageOverviewPriceMapper;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.collection.ConcurrentHashSet;
import com.tahoecn.core.util.NumberUtil;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 经营概览货值统计表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-07-02
 */
@Service
public class BoManageOverviewPriceServiceImpl extends ServiceImpl<BoManageOverviewPriceMapper, BoManageOverviewPrice> implements BoManageOverviewPriceService {

    private static final BigDecimal K10_BIGDECIMAL = MathUtils.newBigDecimal("10000");

    @Autowired
    private BoProjectPriceExtendService boProjectPriceExtendService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapService boProjectLandPartProductTypeQuotaMapService;

    @Autowired
    private BoProjectPriceQuotaMapService boProjectPriceQuotaMapService;

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
    private SaleRoomService saleRoomService;

    @Autowired
    private SaleRoomStreamService saleRoomStreamService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${tahoe.uc.land-plate.org.id}")
    private String landPlateId;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BoManageOverviewPrice> makeData(LocalDate localDate) {
        //版本时间/截止时间，统计到此时间为止的货值
        LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

        //创建一个经营概览版本 - 货值版
        BoManageOverviewVersion version = new BoManageOverviewVersion();
        version.setId(UUIDUtils.create());
        version.setCreateTime(LocalDateTime.now());
        version.setVersionDate(localDate);
        version.setVersionType(ManageOverviewVersionTypeEnum.PRODUCT_PRICE.getKey());

        //Map<项目ID/分期ID,Map<项目KEY/业态KEY/分期KEY/地块KEY,货值>>
        Map<String, Map<String, BoManageOverviewPrice>> boManageOverviewPriceMap = new ConcurrentHashMap<>();

        //查询地产板块下所有项目和分期
        List<UcOrg> ucOrgList = ucOrgService.getChildrenProjectAndSubByFdSid(landPlateId);
        if (CollectionUtils.isNotEmpty(ucOrgList)) {
            //遍历计算货值
            ucOrgList.parallelStream().forEach(ucOrg -> {
                //Map<PROJECT_OBJ(项目)/业态CODE(分期下业态)/PROJECT_SUB_OBJ(分期)/地块ID+业态CODE(地块业态)/LAND_PART_OBJ+地块ID（地块）,货值>
                Map<String, BoManageOverviewPrice> entryMap = new ConcurrentHashMap<>();
                boManageOverviewPriceMap.put(ucOrg.getFdSid(), entryMap);

                //项目
                if (OrgTypeCodeEnum.UC_LAND_PROJECT.getCode().equals(ucOrg.getFdType())) {
                    //项目投决会货值
                    makeInvestDecisionProductPrice(endTime, version, ucOrg, entryMap);

                    //项目下未开发地块货值
                    makeLandPartNotDevelopedPrice(endTime, version, ucOrg, entryMap);

                    //未开发地块汇总至项目（包括全盘动态货值与未开发货值字段）
                    makeProjectPriceCalcLandPart(entryMap);
                    return;
                }

                //分期
                if (OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode().equals(ucOrg.getFdType())) {
                    //业态-全盘动态货值、未开工、已开工未达形象进度、已达形象进度未取证、已售、已取证未售
                    makeOverallProductPrice(endTime, version, ucOrg, entryMap);

                    //业态-经营决策会货值
                    makeManageDecisionProductPrice(endTime, version, ucOrg, entryMap);

                    //分期-业态货值汇总（取业态数据的和） - 放最后
                    makeProjectSubPrice(version, ucOrg, entryMap);
                    return;
                }

            });

            //项目-将分期货值汇总至项目 - 放最后
            makeProjectPriceCalcProjectSub(boManageOverviewPriceMap, ucOrgList);
        }


        List<BoManageOverviewPrice> boManageOverviewPriceList = boManageOverviewPriceMap.values()
                .parallelStream()
                .flatMap(x -> x.values().parallelStream())
                .collect(Collectors.toList());
        if (localDate.isBefore(LocalDate.now())) {
            //保存入库 - 非当天
            saveManageOverviewVersionAndPrice(version, boManageOverviewPriceList);
            //清空临时数据
            QueryWrapper<BoManageOverviewVersion>  queryWrapper = new QueryWrapper();
            queryWrapper.eq("creater_name","system-temp")
                    .eq("version_type",ManageOverviewVersionTypeEnum.PRODUCT_PRICE.getKey())
                    .eq("version_date",version.getVersionDate());
            List<BoManageOverviewVersion> list = boManageOverviewVersionService.list(queryWrapper);
            if(CollectionUtils.isNotEmpty(list)){
                List<String> ids = list.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
                boManageOverviewVersionService.removeByIds(ids);
                QueryWrapper<BoManageOverviewPrice> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.in("manage_overview_version_id",ids);
                remove(deleteWrapper);
            }
        } else {
            //当天存入redis
            redisTemplate.opsForValue().set(RedisConstants.MANAGE_OVERVIEW_PRICE_TODAY, boManageOverviewPriceList);
            //也要入库给其他系统使用，后续要在昨日任务中将此类信息清空
            version.setCreaterName("system-temp");
            saveManageOverviewVersionAndPrice(version,boManageOverviewPriceList);
        }
        return boManageOverviewPriceList;
    }


    private void makeBuildingAreaClac(List<Map.Entry<BoBuilding, List<BoBuildingProductTypeMap>>> unReachGoalBuildingEntryList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList, Map<String, Map.Entry<BoBuildingProductTypeMap, BigDecimal>> productTypeCanSaleAreaOrCarParkingMap) {
        Set<String> areaQuotaCodeSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
        Set<String> carQuotaCodeSet = Stream.of(QuotaCodeEnum.SIMPLE_CAR_PARKING_PLATE_COUNT.getKey()).collect(Collectors.toSet());
        unReachGoalBuildingEntryList.parallelStream().map(x -> x.getValue()).flatMap(x -> x.parallelStream()).forEach(x -> {
            Map.Entry<BoBuildingProductTypeMap, BigDecimal> entry = productTypeCanSaleAreaOrCarParkingMap.get(x.getProductTypeCode());
            if (entry == null) {
                entry = new AbstractMap.SimpleEntry<>(x, BigDecimal.ZERO);
                Map.Entry<BoBuildingProductTypeMap, BigDecimal> last = productTypeCanSaleAreaOrCarParkingMap.putIfAbsent(x.getProductTypeCode(), entry);
                if (last != null) {
                    entry = last;
                }
            }
            Map.Entry<BoBuildingProductTypeMap, BigDecimal> finalEntry = entry;
            if (ProductTypeEnum.isCarParkingPlate(x.getProductTypeCode())) {
                //车位计算
                buildingProductTypeQuotaList.parallelStream()
                        .filter(y -> y.getBuildingProductTypeId().equals(x.getId()))
                        .filter(y -> carQuotaCodeSet.contains(y.getQuotaCode()))
                        .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal::add)
                        .ifPresent(y -> {
                            synchronized (x.getProductTypeCode()) {
                                finalEntry.setValue(finalEntry.getValue().add(y).setScale(2, RoundingMode.HALF_UP));
                            }
                        });
            } else {
                //可售面积
                buildingProductTypeQuotaList.parallelStream()
                        .filter(y -> y.getBuildingProductTypeId().equals(x.getId()))
                        .filter(y -> areaQuotaCodeSet.contains(y.getQuotaCode()))
                        .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                        .reduce(BigDecimal::add)
                        .ifPresent(y -> {
                            synchronized (x.getProductTypeCode()) {
                                finalEntry.setValue(finalEntry.getValue().add(y).setScale(2, RoundingMode.HALF_UP));
                            }
                        });
            }

        });
    }

    private void makeProjectPriceCalcProjectSub(Map<String, Map<String, BoManageOverviewPrice>> boManageOverviewPriceMap, List<UcOrg> ucOrgList) {
        ucOrgList.parallelStream().forEach(ucOrg -> {
            //分期
            if (OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode().equals(ucOrg.getFdType())) {
                Map<String, BoManageOverviewPrice> parentMap = boManageOverviewPriceMap.get(ucOrg.getFdPsid());
                if (parentMap != null && !parentMap.isEmpty()) {
                    BoManageOverviewPrice parentPrice = parentMap.get(ManageOverviewPriceKeyTypeEnum.PROJECT.getKey());
                    if (parentPrice != null) {
                        Map<String, BoManageOverviewPrice> entryMap = boManageOverviewPriceMap.get(ucOrg.getFdSid());
                        entryMap.keySet().parallelStream().filter(x -> x.startsWith(ManageOverviewPriceKeyTypeEnum.PROJECT_SUB.getKey())).map(x -> entryMap.get(x)).forEach(x -> {
                            synchronized (parentPrice) {

                                //汇总 - 全盘货值
                                parentPrice.setOverallProductPrice(MathUtils.nullDefaultZero(parentPrice.getOverallProductPrice()).add(MathUtils.nullDefaultZero(x.getOverallProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 经营决策会货值
                                parentPrice.setManageDecisionProductPrice(MathUtils.nullDefaultZero(parentPrice.getManageDecisionProductPrice()).add(MathUtils.nullDefaultZero(x.getManageDecisionProductPrice())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 未开工货值
                                parentPrice.setUnStartProductPrice(MathUtils.nullDefaultZero(parentPrice.getUnStartProductPrice()).add(MathUtils.nullDefaultZero(x.getUnStartProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 未开工 面积
                                parentPrice.setUnStartArea(MathUtils.nullDefaultZero(parentPrice.getUnStartArea()).add(MathUtils.nullDefaultZero(x.getUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已开工未达形象进度货值
                                parentPrice.setUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(parentPrice.getUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(x.getUnReachImgGoalProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已开工未达形象进度面积
                                parentPrice.setUnReachImgGoalArea(MathUtils.nullDefaultZero(parentPrice.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(x.getUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已达形象进度未取证货值
                                parentPrice.setUnGetCardProductPrice(MathUtils.nullDefaultZero(parentPrice.getUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(x.getUnGetCardProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已达形象进度未取证面面积
                                parentPrice.setUnGetCardArea(MathUtils.nullDefaultZero(parentPrice.getUnGetCardArea()).add(MathUtils.nullDefaultZero(x.getUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已取证未售 （存货）货值
                                parentPrice.setUnSaleProductPrice(MathUtils.nullDefaultZero(parentPrice.getUnSaleProductPrice()).add(MathUtils.nullDefaultZero(x.getUnSaleProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已取证未售 （存货）面积
                                parentPrice.setUnSaleArea(MathUtils.nullDefaultZero(parentPrice.getUnSaleArea()).add(MathUtils.nullDefaultZero(x.getUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已售货值
                                parentPrice.setSaledProductPrice(MathUtils.nullDefaultZero(parentPrice.getSaledProductPrice()).add(MathUtils.nullDefaultZero(x.getSaledProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已售面积
                                parentPrice.setSaledArea(MathUtils.nullDefaultZero(parentPrice.getSaledArea()).add(MathUtils.nullDefaultZero(x.getSaledArea())).setScale(2, RoundingMode.HALF_UP));

                                //货龄
                                parentPrice.setAgeFirstProductPrice(MathUtils.nullDefaultZero(parentPrice.getAgeFirstProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeFirstProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setAgeSencondProductPrice(MathUtils.nullDefaultZero(parentPrice.getAgeSencondProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeSencondProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setAgeThirdProductPrice(MathUtils.nullDefaultZero(parentPrice.getAgeThirdProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeThirdProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setAgeFourthProductPrice(MathUtils.nullDefaultZero(parentPrice.getAgeFourthProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeFourthProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setAgeFifthProductPrice(MathUtils.nullDefaultZero(parentPrice.getAgeFifthProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeFifthProductPrice())).setScale(2, RoundingMode.HALF_UP));

                                /**
                                 * 认购口径
                                 */
                                //汇总 - 未开工货值
                                parentPrice.setOfferUnStartProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferUnStartProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnStartProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 未开工 面积
                                parentPrice.setOfferUnStartArea(MathUtils.nullDefaultZero(parentPrice.getOfferUnStartArea()).add(MathUtils.nullDefaultZero(x.getOfferUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已开工未达形象进度货值
                                parentPrice.setOfferUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnReachImgGoalProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已开工未达形象进度面积
                                parentPrice.setOfferUnReachImgGoalArea(MathUtils.nullDefaultZero(parentPrice.getOfferUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(x.getOfferUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已达形象进度未取证货值
                                parentPrice.setOfferUnGetCardProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnGetCardProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已达形象进度未取证面面积
                                parentPrice.setOfferUnGetCardArea(MathUtils.nullDefaultZero(parentPrice.getOfferUnGetCardArea()).add(MathUtils.nullDefaultZero(x.getOfferUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已取证未售 （存货）货值
                                parentPrice.setOfferUnSaleProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferUnSaleProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnSaleProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已取证未售 （存货）面积
                                parentPrice.setOfferUnSaleArea(MathUtils.nullDefaultZero(parentPrice.getOfferUnSaleArea()).add(MathUtils.nullDefaultZero(x.getOfferUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                                //汇总 - 已售货值
                                parentPrice.setOfferSaledProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferSaledProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferSaledProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                //汇总 - 已售面积
                                parentPrice.setOfferSaledArea(MathUtils.nullDefaultZero(parentPrice.getOfferSaledArea()).add(MathUtils.nullDefaultZero(x.getOfferSaledArea())).setScale(2, RoundingMode.HALF_UP));

                                //货龄
                                parentPrice.setOfferAgeFirstProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferAgeFirstProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeFirstProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setOfferAgeSencondProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferAgeSencondProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeSencondProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setOfferAgeThirdProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferAgeThirdProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeThirdProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setOfferAgeFourthProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferAgeFourthProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeFourthProductPrice())).setScale(2, RoundingMode.HALF_UP));
                                parentPrice.setOfferAgeFifthProductPrice(MathUtils.nullDefaultZero(parentPrice.getOfferAgeFifthProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeFifthProductPrice())).setScale(2, RoundingMode.HALF_UP));

                            }
                        });
                        //计算项目下均价
                        if (MathUtils.isNoneZero(parentPrice.getSaledArea(), parentPrice.getSaledProductPrice())) {
                            //已售均价
                            parentPrice.setSaledAvgPrice(parentPrice.getSaledProductPrice().multiply(K10_BIGDECIMAL).divide(parentPrice.getSaledArea(), 2, RoundingMode.HALF_UP));
                        }
                        //未售
                        BigDecimal totalProductPrice = NumberUtil.add(parentPrice.getUnStartProductPrice(), parentPrice.getUnReachImgGoalProductPrice(), parentPrice.getUnGetCardProductPrice(), parentPrice.getUnSaleProductPrice());
                        BigDecimal totalArea = NumberUtil.add(parentPrice.getUnStartArea(), parentPrice.getUnReachImgGoalArea(), parentPrice.getUnGetCardArea(), parentPrice.getUnSaleArea());
                        if (MathUtils.isNoneZero(totalProductPrice, totalArea)) {
                            //货值单位万元要转为元
                            parentPrice.setUnSaleAvgPrice(totalProductPrice.multiply(K10_BIGDECIMAL).divide(totalArea, 2, RoundingMode.HALF_UP));
                        }

                        //计算项目下均价 - 认购口径
                        if (MathUtils.isNoneZero(parentPrice.getOfferSaledArea(), parentPrice.getOfferSaledProductPrice())) {
                            //已售均价
                            parentPrice.setOfferSaledAvgPrice(parentPrice.getOfferSaledProductPrice().multiply(K10_BIGDECIMAL).divide(parentPrice.getOfferSaledArea(), 2, RoundingMode.HALF_UP));
                        }
                        //未售
                        BigDecimal offerTotalProductPrice = NumberUtil.add(parentPrice.getOfferUnStartProductPrice(), parentPrice.getOfferUnReachImgGoalProductPrice(), parentPrice.getOfferUnGetCardProductPrice(), parentPrice.getOfferUnSaleProductPrice());
                        BigDecimal offerTotalArea = NumberUtil.add(parentPrice.getOfferUnStartArea(), parentPrice.getOfferUnReachImgGoalArea(), parentPrice.getOfferUnGetCardArea(), parentPrice.getOfferUnSaleArea());
                        if (MathUtils.isNoneZero(offerTotalProductPrice, offerTotalArea)) {
                            //货值单位万元要转为元
                            parentPrice.setOfferUnSaleAvgPrice(offerTotalProductPrice.multiply(K10_BIGDECIMAL).divide(offerTotalArea, 2, RoundingMode.HALF_UP));
                        }

                    }
                }
                return;
            }
        });
    }

    private void makeProjectPriceCalcLandPart(Map<String, BoManageOverviewPrice> entryMap) {
        BoManageOverviewPrice projectPrice = entryMap.get(ManageOverviewPriceKeyTypeEnum.PROJECT.getKey());
        List<BoManageOverviewPrice> landPartPrice = entryMap.keySet().parallelStream().filter(x -> x.startsWith(ManageOverviewPriceKeyTypeEnum.LAND_PART.getKey())).map(x -> entryMap.get(x)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(landPartPrice)) {
            //全盘
            landPartPrice.stream().map(x -> MathUtils.nullDefaultZero(x.getOverallProductPrice())).reduce(BigDecimal::add)
                    .ifPresent(x -> {
                        projectPrice.setOverallProductPrice(MathUtils.nullDefaultZero(projectPrice.getOverallProductPrice()).add(x).setScale(2, RoundingMode.HALF_UP));
                    });
            //未开发面积
            landPartPrice.stream().map(x -> MathUtils.nullDefaultZero(x.getUnDevelopArea())).reduce(BigDecimal::add)
                    .ifPresent(x -> {
                        projectPrice.setUnDevelopArea(MathUtils.nullDefaultZero(projectPrice.getUnDevelopArea()).add(x).setScale(2, RoundingMode.HALF_UP));
                    });
            //未开发
            projectPrice.setUnDevelopProductPrice(projectPrice.getOverallProductPrice());

            //均价
            if (MathUtils.isNoneZero(projectPrice.getUnDevelopProductPrice(), projectPrice.getUnDevelopArea())) {
                projectPrice.setUnDevelopAvgPrice(projectPrice.getUnDevelopProductPrice().multiply(K10_BIGDECIMAL).divide(projectPrice.getUnDevelopArea(), 2, RoundingMode.HALF_UP));
            }
        }
    }

    private void makeProjectSubPrice(BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> entryMap) {
        BoManageOverviewPrice boManageOverviewPrice = new BoManageOverviewPrice();
        boManageOverviewPrice.setId(UUIDUtils.create());
        boManageOverviewPrice.setCreateTime(LocalDateTime.now());
        boManageOverviewPrice.setOrgId(ucOrg.getFdSid());
        boManageOverviewPrice.setOrgName(ucOrg.getFdName());
        boManageOverviewPrice.setParentOrgId(ucOrg.getFdPsid());
        boManageOverviewPrice.setParentOrgName(ucOrg.getFdPname());
        boManageOverviewPrice.setOrgType(ucOrg.getFdType());
        boManageOverviewPrice.setManageOverviewVersionId(version.getId());
        boManageOverviewPrice.setVersionDate(version.getVersionDate());
        entryMap.put(ManageOverviewPriceKeyTypeEnum.PROJECT_SUB.getKey(), boManageOverviewPrice);

        entryMap.keySet().parallelStream()
                .filter(x -> x.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey()))
                .map(x -> entryMap.get(x))
                .forEach(x -> {
                    synchronized (boManageOverviewPrice) {
                        //汇总 - 全盘货值
                        boManageOverviewPrice.setOverallProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOverallProductPrice()).add(MathUtils.nullDefaultZero(x.getOverallProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 经营决策会货值
                        boManageOverviewPrice.setManageDecisionProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getManageDecisionProductPrice()).add(MathUtils.nullDefaultZero(x.getManageDecisionProductPrice())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 未开工货值
                        boManageOverviewPrice.setUnStartProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartProductPrice()).add(MathUtils.nullDefaultZero(x.getUnStartProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 未开工 面积
                        boManageOverviewPrice.setUnStartArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartArea()).add(MathUtils.nullDefaultZero(x.getUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已开工未达形象进度货值
                        boManageOverviewPrice.setUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(x.getUnReachImgGoalProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已开工未达形象进度面积
                        boManageOverviewPrice.setUnReachImgGoalArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(x.getUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已达形象进度未取证货值
                        boManageOverviewPrice.setUnGetCardProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(x.getUnGetCardProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已达形象进度未取证面面积
                        boManageOverviewPrice.setUnGetCardArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardArea()).add(MathUtils.nullDefaultZero(x.getUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已取证未售 （存货）货值
                        boManageOverviewPrice.setUnSaleProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnSaleProductPrice()).add(MathUtils.nullDefaultZero(x.getUnSaleProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已取证未售 （存货）面积
                        boManageOverviewPrice.setUnSaleArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnSaleArea()).add(MathUtils.nullDefaultZero(x.getUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已售货值
                        boManageOverviewPrice.setSaledProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledProductPrice()).add(MathUtils.nullDefaultZero(x.getSaledProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已售面积
                        boManageOverviewPrice.setSaledArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledArea()).add(MathUtils.nullDefaultZero(x.getSaledArea())).setScale(2, RoundingMode.HALF_UP));

                        //货龄
                        boManageOverviewPrice.setAgeFirstProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getAgeFirstProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeFirstProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setAgeSencondProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getAgeSencondProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeSencondProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setAgeThirdProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getAgeThirdProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeThirdProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setAgeFourthProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getAgeFourthProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeFourthProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setAgeFifthProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getAgeFifthProductPrice()).add(MathUtils.nullDefaultZero(x.getAgeFifthProductPrice())).setScale(2, RoundingMode.HALF_UP));

                        /**
                         * 认购口径汇总
                         */
                        //汇总 - 未开工货值
                        boManageOverviewPrice.setOfferUnStartProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnStartProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 未开工 面积
                        boManageOverviewPrice.setOfferUnStartArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartArea()).add(MathUtils.nullDefaultZero(x.getOfferUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已开工未达形象进度货值
                        boManageOverviewPrice.setOfferUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnReachImgGoalProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已开工未达形象进度面积
                        boManageOverviewPrice.setOfferUnReachImgGoalArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(x.getOfferUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已达形象进度未取证货值
                        boManageOverviewPrice.setOfferUnGetCardProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnGetCardProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已达形象进度未取证面面积
                        boManageOverviewPrice.setOfferUnGetCardArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardArea()).add(MathUtils.nullDefaultZero(x.getOfferUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已取证未售 （存货）货值
                        boManageOverviewPrice.setOfferUnSaleProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnSaleProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferUnSaleProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已取证未售 （存货）面积
                        boManageOverviewPrice.setOfferUnSaleArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnSaleArea()).add(MathUtils.nullDefaultZero(x.getOfferUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                        //汇总 - 已售货值
                        boManageOverviewPrice.setOfferSaledProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferSaledProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        //汇总 - 已售面积
                        boManageOverviewPrice.setOfferSaledArea(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledArea()).add(MathUtils.nullDefaultZero(x.getOfferSaledArea())).setScale(2, RoundingMode.HALF_UP));

                        //货龄
                        boManageOverviewPrice.setOfferAgeFirstProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferAgeFirstProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeFirstProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setOfferAgeSencondProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferAgeSencondProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeSencondProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setOfferAgeThirdProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferAgeThirdProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeThirdProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setOfferAgeFourthProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferAgeFourthProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeFourthProductPrice())).setScale(2, RoundingMode.HALF_UP));
                        boManageOverviewPrice.setOfferAgeFifthProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferAgeFifthProductPrice()).add(MathUtils.nullDefaultZero(x.getOfferAgeFifthProductPrice())).setScale(2, RoundingMode.HALF_UP));

                    }
                });

        //计算分期下均价
        if (MathUtils.isNoneZero(boManageOverviewPrice.getSaledArea(), boManageOverviewPrice.getSaledProductPrice())) {
            //已售均价
            boManageOverviewPrice.setSaledAvgPrice(boManageOverviewPrice.getSaledProductPrice().multiply(K10_BIGDECIMAL).divide(boManageOverviewPrice.getSaledArea(), 2, RoundingMode.HALF_UP));
        }
        //未售
        BigDecimal totalProductPrice = NumberUtil.add(boManageOverviewPrice.getUnStartProductPrice(), boManageOverviewPrice.getUnReachImgGoalProductPrice(), boManageOverviewPrice.getUnGetCardProductPrice(), boManageOverviewPrice.getUnSaleProductPrice());
        BigDecimal totalArea = NumberUtil.add(boManageOverviewPrice.getUnStartArea(), boManageOverviewPrice.getUnReachImgGoalArea(), boManageOverviewPrice.getUnGetCardArea(), boManageOverviewPrice.getUnSaleArea());
        if (MathUtils.isNoneZero(totalProductPrice, totalArea)) {
            //货值单位万元要转为元
            boManageOverviewPrice.setUnSaleAvgPrice(totalProductPrice.multiply(K10_BIGDECIMAL).divide(totalArea, 2, RoundingMode.HALF_UP));
        }

        //计算分期下均价 - 认购口径
        if (MathUtils.isNoneZero(boManageOverviewPrice.getOfferSaledArea(), boManageOverviewPrice.getOfferSaledProductPrice())) {
            //已售均价
            boManageOverviewPrice.setOfferSaledAvgPrice(boManageOverviewPrice.getOfferSaledProductPrice().multiply(K10_BIGDECIMAL).divide(boManageOverviewPrice.getOfferSaledArea(), 2, RoundingMode.HALF_UP));
        }
        //未售
        BigDecimal offerTotalProductPrice = NumberUtil.add(boManageOverviewPrice.getOfferUnStartProductPrice(), boManageOverviewPrice.getOfferUnReachImgGoalProductPrice(), boManageOverviewPrice.getOfferUnGetCardProductPrice(), boManageOverviewPrice.getOfferUnSaleProductPrice());
        BigDecimal offerTotalArea = NumberUtil.add(boManageOverviewPrice.getOfferUnStartArea(), boManageOverviewPrice.getOfferUnReachImgGoalArea(), boManageOverviewPrice.getOfferUnGetCardArea(), boManageOverviewPrice.getOfferUnSaleArea());
        if (MathUtils.isNoneZero(offerTotalProductPrice, offerTotalArea)) {
            //货值单位万元要转为元
            boManageOverviewPrice.setOfferUnSaleAvgPrice(offerTotalProductPrice.multiply(K10_BIGDECIMAL).divide(offerTotalArea, 2, RoundingMode.HALF_UP));
        }

    }

    private void makeLandPartNotDevelopedPrice(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> entryMap) {
        //查项目投决会阶段的地块业态以及价格信息
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), Stream.of(StageCodeEnum.STAGE_01.getKey()).collect(Collectors.toList()), endTime);
        if (priceLastPassedVersion != null) {
            List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(priceLastPassedVersion.getId());
            if (projectPriceQuotaList != null) {
                //Map<RefId,Map<指标CODE,指标对象>>
                Map<String, Map<String, BoProjectPriceQuotaMap>> refIdAndPriceMap = new HashMap<>();
                projectPriceQuotaList.stream().forEach(x -> {
                    Map<String, BoProjectPriceQuotaMap> priceMap = refIdAndPriceMap.get(x.getRefId());
                    if (priceMap == null) {
                        priceMap = new HashMap<>();
                        refIdAndPriceMap.put(x.getRefId(), priceMap);
                    }
                    priceMap.put(x.getQuotaCode(), x);
                });
                List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeListByPriceVersionId(priceLastPassedVersion.getId());
                if (CollectionUtils.isNotEmpty(landPartProductTypeList)) {
                    List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaListByPriceVersionId(priceLastPassedVersion.getId());
                    if (CollectionUtils.isNotEmpty(landPartProductTypeQuotaList)) {
                        //地块业态总可售面积 - 投决会
                        //Map<地块ID,Map<业态CODE,Entry<样例地块业态对象,总可售面积/总可售车位数>>>
                        Map<String, Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>>> landPartIdAndLandPartProductTypeMap = new HashMap<>();
                        //地上、地下可售面积指标、车位数
                        Set<String> areaQuotaSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
                        Set<String> carPlateQuotaSet = Stream.of(QuotaCodeEnum.SIMPLE_CAR_PARKING_PLATE_COUNT.getKey()).collect(Collectors.toSet());
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
                            if (ProductTypeEnum.isCarParkingPlate(x.getProductTypeCode())) {
                                //车位计算
                                landPartProductTypeQuotaList.parallelStream()
                                        .filter(y -> carPlateQuotaSet.contains(y.getQuotaCode()) && x.getId().equals(y.getLandPartProductTypeId()))
                                        .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                        .reduce(BigDecimal::add)
                                        .ifPresent(y -> {
                                            BigDecimal prevVal = finalEntry.getValue();
                                            finalEntry.setValue(prevVal.add(y).setScale(2, RoundingMode.HALF_UP));
                                        });
                            } else {
                                //可售面积计算
                                landPartProductTypeQuotaList.parallelStream()
                                        .filter(y -> areaQuotaSet.contains(y.getQuotaCode()) && x.getId().equals(y.getLandPartProductTypeId()))
                                        .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                        .reduce(BigDecimal::add)
                                        .ifPresent(y -> {
                                            BigDecimal prevVal = finalEntry.getValue();
                                            finalEntry.setValue(prevVal.add(y).setScale(2, RoundingMode.HALF_UP));
                                        });
                            }
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
                                            if (ProductTypeEnum.isCarParkingPlate(x.getProductTypeCode())) {
                                                //车位计算
                                                projectLandPartProductQuotaList.parallelStream()
                                                        .filter(y -> carPlateQuotaSet.contains(y.getQuotaCode()) && x.getId().equals(y.getProjectLandPartProductTypeId()))
                                                        .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                                        .reduce(BigDecimal::add)
                                                        .ifPresent(y -> {
                                                            BigDecimal prevVal = finalEntry.getValue();
                                                            finalEntry.setValue(prevVal.add(y).setScale(2, RoundingMode.HALF_UP));
                                                        });
                                            } else {
                                                //可售面积计算
                                                projectLandPartProductQuotaList.parallelStream()
                                                        .filter(y -> areaQuotaSet.contains(y.getQuotaCode()) && x.getId().equals(y.getProjectLandPartProductTypeId()))
                                                        .map(y -> MathUtils.newBigDecimal(y.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                                        .reduce(BigDecimal::add)
                                                        .ifPresent(y -> {
                                                            BigDecimal prevVal = finalEntry.getValue();
                                                            finalEntry.setValue(prevVal.add(y).setScale(2, RoundingMode.HALF_UP));
                                                        });
                                            }

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
                                makeLandPriceIfProjectSubNotUse(version, ucOrg, entryMap, refIdAndPriceMap, landId, investAreaMap);
                            } else {
                                //map2不为空表示分期已规划地块，需要减去分期规划的可售面积，若有剩余的则计算货值
                                makeLandPriceIfProjectSubUsed(version, ucOrg, entryMap, refIdAndPriceMap, landId, investAreaMap, projectSubAreaMap);
                            }
                        });

                    }
                }
            }

        }
    }

    private void makeLandPriceIfProjectSubUsed(BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> entryMap, Map<String, Map<String, BoProjectPriceQuotaMap>> refIdAndPriceMap, String landId, Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>> investAreaMap, Map<String, Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal>> projectSubAreaMap) {
        //表示有剩余未开发
        BoManageOverviewPrice landPrice = new BoManageOverviewPrice();
        landPrice.setId(UUIDUtils.create());
        landPrice.setCreateTime(LocalDateTime.now());
        landPrice.setOrgId(landId);
        landPrice.setOrgName(investAreaMap.values().stream().findFirst().get().getKey().getLandPartName());
        landPrice.setParentOrgId(ucOrg.getFdSid());
        landPrice.setParentOrgName(ucOrg.getFdName());
        landPrice.setOrgType(OrgTypeCodeEnum.LAND_PART.getCode());
        landPrice.setManageOverviewVersionId(version.getId());
        landPrice.setVersionDate(version.getVersionDate());
        landPrice.setOverallProductPrice(BigDecimal.ZERO);
        landPrice.setInvestDecisionProductPrice(BigDecimal.ZERO);
        entryMap.put(ManageOverviewPriceKeyTypeEnum.LAND_PART.getKey() + landId, landPrice);
        investAreaMap.forEach((productTypeCode, entry1) -> {
            Map.Entry<BoProjectLandPartProductTypeMap, BigDecimal> entry2 = projectSubAreaMap.get(productTypeCode);
            //若entry2为空则表示分期下该地块的次业态未规划面积，那么试做规划为0
            BigDecimal investArea = entry1.getValue();
            BigDecimal subArea = entry2 == null ? BigDecimal.ZERO : entry2.getValue();
            BigDecimal result = investArea.subtract(subArea).setScale(2, RoundingMode.HALF_UP);
            //投决会的大于分期的，则表示有剩余，需统计货值，反之则无，则不需统计货值
            if (result.doubleValue() > 0) {
                String refId = SecureUtil.md5(landId + productTypeCode);
                Map<String, BoProjectPriceQuotaMap> priceMap = refIdAndPriceMap.get(refId);
                if (priceMap != null) {
                    BoManageOverviewPrice landPtcPrice = new BoManageOverviewPrice();
                    landPtcPrice.setId(UUIDUtils.create());
                    landPtcPrice.setCreateTime(LocalDateTime.now());
                    landPtcPrice.setOrgId(refId);
                    landPtcPrice.setOrgName(entry1.getKey().getProductTypeName());
                    landPtcPrice.setParentOrgId(landId);
                    landPtcPrice.setParentOrgName(landPrice.getOrgName());
                    landPtcPrice.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                    landPtcPrice.setManageOverviewVersionId(version.getId());
                    landPtcPrice.setVersionDate(version.getVersionDate());
                    BoProjectPriceQuotaMap avg = priceMap.get(QuotaCodeEnum.AVERAGE_PRICE.getKey());
                    if (avg != null) {
                        //均价单位为元，结果需除以10000，转为货值单位 万元
                        BigDecimal overall = MathUtils.newBigDecimal(avg.getQuotaValue()).setScale(2, RoundingMode.HALF_UP).multiply(result)
                                .divide(K10_BIGDECIMAL)
                                .setScale(2, RoundingMode.HALF_UP);
                        landPtcPrice.setOverallProductPrice(overall);
                        landPtcPrice.setUnDevelopProductPrice(overall);
                        landPtcPrice.setUnDevelopArea(result);
                        if (MathUtils.isNoneZero(landPtcPrice.getUnDevelopArea(), landPtcPrice.getUnDevelopProductPrice())) {
                            landPtcPrice.setUnDevelopAvgPrice(landPtcPrice.getUnDevelopProductPrice().multiply(K10_BIGDECIMAL).divide(landPtcPrice.getUnDevelopArea(), 2, RoundingMode.HALF_UP));
                        }
                    } else {
                        landPtcPrice.setUnDevelopArea(BigDecimal.ZERO);
                        landPtcPrice.setOverallProductPrice(BigDecimal.ZERO);
                    }
                    entryMap.put(landId + productTypeCode, landPtcPrice);

                    //汇总到地块
                    landPrice.setOverallProductPrice(MathUtils.nullDefaultZero(landPrice.getOverallProductPrice()).add(landPtcPrice.getOverallProductPrice().setScale(2, RoundingMode.HALF_UP)));
                    landPrice.setUnDevelopProductPrice(landPrice.getOverallProductPrice());
                    landPrice.setUnDevelopArea(MathUtils.nullDefaultZero(landPrice.getUnDevelopArea()).add(landPtcPrice.getUnDevelopArea().setScale(2, RoundingMode.HALF_UP)));
                }
            }
        });

        //均价
        if (MathUtils.isNoneZero(landPrice.getUnDevelopArea(), landPrice.getUnDevelopProductPrice())) {
            landPrice.setUnDevelopAvgPrice(landPrice.getUnDevelopProductPrice().multiply(K10_BIGDECIMAL).divide(landPrice.getUnDevelopArea(), 2, RoundingMode.HALF_UP));
        }
    }

    private void makeLandPriceIfProjectSubNotUse(BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> entryMap, Map<String, Map<String, BoProjectPriceQuotaMap>> refIdAndPriceMap, String landId, Map<String, Map.Entry<BoLandPartProductTypeMap, BigDecimal>> investAreaMap) {
        BoManageOverviewPrice landPrice = new BoManageOverviewPrice();
        landPrice.setId(UUIDUtils.create());
        landPrice.setCreateTime(LocalDateTime.now());
        landPrice.setOrgId(landId);
        landPrice.setOrgName(investAreaMap.values().stream().findFirst().get().getKey().getLandPartName());
        landPrice.setParentOrgId(ucOrg.getFdSid());
        landPrice.setParentOrgName(ucOrg.getFdName());
        landPrice.setOrgType(OrgTypeCodeEnum.LAND_PART.getCode());
        landPrice.setManageOverviewVersionId(version.getId());
        landPrice.setVersionDate(version.getVersionDate());
        landPrice.setOverallProductPrice(BigDecimal.ZERO);
        landPrice.setInvestDecisionProductPrice(BigDecimal.ZERO);
        entryMap.put(ManageOverviewPriceKeyTypeEnum.LAND_PART.getKey() + landId, landPrice);
        //计算地块下未开发业态的货值
        investAreaMap.forEach((productTypeCode, entry) -> {
            String refId = SecureUtil.md5(landId + productTypeCode);
            Map<String, BoProjectPriceQuotaMap> priceMap = refIdAndPriceMap.get(refId);
            if (priceMap != null) {
                BoManageOverviewPrice landPtcPrice = new BoManageOverviewPrice();
                landPtcPrice.setId(UUIDUtils.create());
                landPtcPrice.setCreateTime(LocalDateTime.now());
                landPtcPrice.setOrgId(refId);
                landPtcPrice.setOrgName(entry.getKey().getProductTypeName());
                landPtcPrice.setParentOrgId(landId);
                landPtcPrice.setParentOrgName(landPrice.getOrgName());
                landPtcPrice.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                landPtcPrice.setManageOverviewVersionId(version.getId());
                landPtcPrice.setVersionDate(version.getVersionDate());
                BoProjectPriceQuotaMap avg = priceMap.get(QuotaCodeEnum.AVERAGE_PRICE.getKey());
                if (avg != null) {
                    //均价单位为元，结果需除以10000，转为货值单位 万元
                    BigDecimal overall = MathUtils.newBigDecimal(avg.getQuotaValue()).setScale(2, RoundingMode.HALF_UP).multiply(entry.getValue())
                            .divide(K10_BIGDECIMAL)
                            .setScale(2, RoundingMode.HALF_UP);
                    landPtcPrice.setOverallProductPrice(overall);
                    landPtcPrice.setUnDevelopProductPrice(overall);
                    landPtcPrice.setUnDevelopArea(entry.getValue());
                    if (MathUtils.isNoneZero(landPtcPrice.getUnDevelopArea(), landPtcPrice.getUnDevelopProductPrice())) {
                        landPtcPrice.setUnDevelopAvgPrice(landPtcPrice.getUnDevelopProductPrice().multiply(K10_BIGDECIMAL).divide(landPtcPrice.getUnDevelopArea(), 2, RoundingMode.HALF_UP));
                    }
                } else {
                    landPtcPrice.setUnDevelopArea(BigDecimal.ZERO);
                    landPtcPrice.setUnDevelopProductPrice(BigDecimal.ZERO);
                    landPtcPrice.setOverallProductPrice(BigDecimal.ZERO);
                }
                entryMap.put(landId + productTypeCode, landPtcPrice);

                //汇总到地块
                landPrice.setOverallProductPrice(MathUtils.nullDefaultZero(landPrice.getOverallProductPrice()).add(landPtcPrice.getOverallProductPrice().setScale(2, RoundingMode.HALF_UP)));
                landPrice.setUnDevelopProductPrice(landPrice.getOverallProductPrice());
                landPrice.setUnDevelopArea(MathUtils.nullDefaultZero(landPrice.getUnDevelopArea()).add(landPtcPrice.getUnDevelopArea().setScale(2, RoundingMode.HALF_UP)));

            }
        });

        //均价
        if (MathUtils.isNoneZero(landPrice.getUnDevelopArea(), landPrice.getUnDevelopProductPrice())) {
            landPrice.setUnDevelopAvgPrice(landPrice.getUnDevelopProductPrice().multiply(K10_BIGDECIMAL).divide(landPrice.getUnDevelopArea(), 2, RoundingMode.HALF_UP));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveManageOverviewVersionAndPrice(BoManageOverviewVersion boManageOverviewVersion, Collection<BoManageOverviewPrice> boManageOverviewPrices) {

        boManageOverviewVersionService.save(boManageOverviewVersion);

        if (CollectionUtils.isNotEmpty(boManageOverviewPrices)) {
            saveBatch(boManageOverviewPrices);
        }
    }

    @Override
    public List<BoManageOverviewPrice> getListByVersionIdAndUcOrgList(LocalDate endDate, String parentOrgId) {
        QueryWrapper<BoManageOverviewPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("version_date", endDate)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(queryWrapper);
    }

    @Override
    public List<BoManageOverviewPrice> getListByVersionIdAndUcOrgList(String versionId, String parentOrgId) {
        QueryWrapper<BoManageOverviewPrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manage_overview_version_id", versionId)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        return list(queryWrapper);
    }


    private void makeInvestDecisionProductPrice(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> entryMap) {
        BoManageOverviewPrice boManageOverviewPrice = new BoManageOverviewPrice();
        boManageOverviewPrice.setId(UUIDUtils.create());
        boManageOverviewPrice.setCreateTime(LocalDateTime.now());
        boManageOverviewPrice.setOrgId(ucOrg.getFdSid());
        boManageOverviewPrice.setOrgName(ucOrg.getFdName());
        boManageOverviewPrice.setOrgType(ucOrg.getFdType());
        boManageOverviewPrice.setParentOrgId(ucOrg.getFdPsid());
        boManageOverviewPrice.setParentOrgName(ucOrg.getFdPname());
        boManageOverviewPrice.setManageOverviewVersionId(version.getId());
        boManageOverviewPrice.setVersionDate(version.getVersionDate());
        boManageOverviewPrice.setInvestDecisionProductPrice(BigDecimal.ZERO);
        entryMap.put(ManageOverviewPriceKeyTypeEnum.PROJECT.getKey(), boManageOverviewPrice);
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), Stream.of(StageCodeEnum.STAGE_01.getKey()).collect(Collectors.toList()), endTime);
        if (priceLastPassedVersion != null) {
            List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(priceLastPassedVersion.getId());
            if (CollectionUtils.isNotEmpty(projectPriceQuotaList)) {
                projectPriceQuotaList.parallelStream().filter(x -> QuotaCodeEnum.PRODUCT_PRICE.getKey().equals(x.getQuotaCode()))
                        .map(x -> MathUtils.newBigDecimal(x.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                        .reduce((a, b) -> a.add(b)).ifPresent(x -> {
                    boManageOverviewPrice.setInvestDecisionProductPrice(x.setScale(2, RoundingMode.HALF_UP));
                });
            }
        }
    }

    private void makeManageDecisionProductPrice(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> subMap) {
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), Stream.of(StageCodeEnum.STAGE_02.getKey()).collect(Collectors.toList()), endTime);
        if (priceLastPassedVersion != null) {
            List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(priceLastPassedVersion.getId());
            if (CollectionUtils.isNotEmpty(projectPriceQuotaList)) {
                //Map<业态CODE>
                Map<String, Map.Entry<BoBuildingProductTypeMap, BigDecimal>> productCodeBigDecimal = new HashMap<>();
                List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(priceLastPassedVersion.getProjectQuotaExtendId());
                buildingProductTypeList.stream().forEach(bpt -> {
                    projectPriceQuotaList.stream().forEach(ppq -> {
                        if (!productCodeBigDecimal.containsKey(bpt.getProductTypeCode())) {
                            if (SecureUtil.md5(bpt.getProductTypeCode()).equals(ppq.getRefId())) {
                                if (QuotaCodeEnum.PRODUCT_PRICE.getKey().equals(ppq.getQuotaCode())) {
                                    Map.Entry<BoBuildingProductTypeMap, BigDecimal> map = new AbstractMap.SimpleEntry<>(bpt, MathUtils.newBigDecimal(ppq.getQuotaValue()).setScale(2, RoundingMode.HALF_UP));
                                    productCodeBigDecimal.put(bpt.getProductTypeCode(), map);
                                }
                            }
                        }
                    });
                });
                productCodeBigDecimal.values().parallelStream().forEach(entry -> {
                    BoBuildingProductTypeMap key = entry.getKey();
                    BigDecimal value = entry.getValue();
                    BoManageOverviewPrice boManageOverviewPrice;
                    String mapKey = ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + key.getProductTypeCode();
                    if ((boManageOverviewPrice = subMap.get(mapKey)) == null) {
                        BoManageOverviewPrice tmp = new BoManageOverviewPrice();
                        if ((boManageOverviewPrice = subMap.putIfAbsent(mapKey, tmp)) == null) {
                            boManageOverviewPrice = tmp;
                            boManageOverviewPrice.setId(UUIDUtils.create());
                            boManageOverviewPrice.setCreateTime(LocalDateTime.now());
                            boManageOverviewPrice.setOrgId(SecureUtil.md5(priceLastPassedVersion.getId() + key.getProductTypeCode()));
                            boManageOverviewPrice.setOrgName(key.getProductTypeName());
                            boManageOverviewPrice.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                            boManageOverviewPrice.setProductTypeCode(key.getProductTypeCode());
                            boManageOverviewPrice.setParentOrgId(ucOrg.getFdSid());
                            boManageOverviewPrice.setParentOrgName(ucOrg.getFdName());
                            boManageOverviewPrice.setManageOverviewVersionId(version.getId());
                            boManageOverviewPrice.setVersionDate(version.getVersionDate());
                        }
                    }
                    //经营决策会货值
                    synchronized (boManageOverviewPrice) {
                        BigDecimal manageDecisionProductPrice = boManageOverviewPrice.getManageDecisionProductPrice();
                        if (manageDecisionProductPrice == null) {
                            manageDecisionProductPrice = BigDecimal.ZERO;
                        }
                        manageDecisionProductPrice = manageDecisionProductPrice.add(value).setScale(2, RoundingMode.HALF_UP);
                        boManageOverviewPrice.setManageDecisionProductPrice(manageDecisionProductPrice);
                    }

                });

            }
        }
    }

    /**
     * 分期下全盘动态货值计算
     *
     * @param endTime
     * @param version
     * @param ucOrg
     * @param subMap
     */
    private void makeOverallProductPrice(LocalDateTime endTime, BoManageOverviewVersion version, UcOrg ucOrg, Map<String, BoManageOverviewPrice> subMap) {
        //业态级的数据计算
        List<String> stageCodes = Stream.of(StageCodeEnum.STAGE_02.getKey(), StageCodeEnum.STAGE_03.getKey(), StageCodeEnum.STAGE_04.getKey()).collect(Collectors.toList());
        BoProjectPriceExtend priceLastPassedVersion = boProjectPriceExtendService.getLastPassedVersion(ucOrg.getFdSid(), stageCodes, endTime);
        if (priceLastPassedVersion == null) {
            //不存在价格版本的不统计
            return;
        }
        List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(priceLastPassedVersion.getId());
        if (CollectionUtils.isEmpty(projectPriceQuotaList)) {
            //有价格版本，但无货值数据，不统计
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
        Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> localPriceMap = makeLocalPrice(projectPriceQuotaList, buildingProductTypeList, buildingList, buildingProductTypeQuotaList);

        //查营销房间，包含定价的与未定价的
        List<SaleRoomStream> saleRoomStreamList = saleRoomStreamService.getLastSaleRoomStreamListByBuildingOriginIds(localPriceMap.keySet(), endTime);
        //营销未完全定价的楼栋业态以及，定价部分的货值
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //营销系统 - Map<楼栋origin_id,Map<业态code,Entry<key无意义,Entry<key无意义,value为数值>[0：总可售面积/车位数，1：货值]>>>
            Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>>> salePriceMap = makeSalePrice(saleRoomStreamList,localPriceMap);
            mergeLocalSale(localPriceMap, salePriceMap);
        }


        //生成全盘动态货值对象
        localPriceMap.values().parallelStream().forEach(productTypeCodePriceMap -> {
            productTypeCodePriceMap.keySet().parallelStream().forEach(productTypeCode -> {
                Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> priceEntry = productTypeCodePriceMap.get(productTypeCode);
                String key = ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + productTypeCode;
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                if (boManageOverviewPrice == null) {
                    boManageOverviewPrice = new BoManageOverviewPrice();
                    BoManageOverviewPrice last = subMap.putIfAbsent(key, boManageOverviewPrice);
                    if (last == null) {
                        boManageOverviewPrice.setId(UUIDUtils.create());
                        boManageOverviewPrice.setCreateTime(LocalDateTime.now());
                        boManageOverviewPrice.setOrgId(SecureUtil.md5(priceLastPassedVersion.getId() + productTypeCode));
                        boManageOverviewPrice.setOrgName(priceEntry.getKey().getProductTypeName());
                        boManageOverviewPrice.setOrgType(OrgTypeCodeEnum.PRODUCT_TYPE.getCode());
                        boManageOverviewPrice.setProductTypeCode(productTypeCode);
                        boManageOverviewPrice.setParentOrgId(ucOrg.getFdSid());
                        boManageOverviewPrice.setParentOrgName(ucOrg.getFdName());
                        boManageOverviewPrice.setManageOverviewVersionId(version.getId());
                        boManageOverviewPrice.setVersionDate(version.getVersionDate());
                    } else {
                        boManageOverviewPrice = last;
                    }
                }
                //相同业态货值汇总到一块
                synchronized (boManageOverviewPrice) {
                    Map.Entry<String, BigDecimal>[] innerEntry = priceEntry.getValue();
                    boManageOverviewPrice.setOverallProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOverallProductPrice()).add(innerEntry[1].getValue()).setScale(2, RoundingMode.HALF_UP));
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

        //计算签约口径货值
        makeOverallSignData(endTime, subMap, localPriceMap, saleRoomStreamList, allSpeedBuildingIdSet, reachGoalBuildingIdSet, startedUnReachGoalBuildingIdSet, unStartedBuildingIdSet, overall);

        //计算认购口径货值
        makeOverallOfferData(endTime, subMap, localPriceMap, saleRoomStreamList, allSpeedBuildingIdSet, reachGoalBuildingIdSet, startedUnReachGoalBuildingIdSet, unStartedBuildingIdSet, overall);



    }

    private void makeOverallSignData(LocalDateTime endTime, Map<String, BoManageOverviewPrice> subMap, Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> localPriceMap, List<SaleRoomStream> saleRoomStreamList, Set<String> allSpeedBuildingIdSet, Set<String> reachGoalBuildingIdSet, Set<String> startedUnReachGoalBuildingIdSet, Set<String> unStartedBuildingIdSet, Set<String> overall) {

        //分已售、已拿证未售、已达形象进度未取证、已开工未达形象进度、未开工
        Set<String> saledBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> getCardUnSaleBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> reachGoalUnGetCardBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> staredUnReachGoalBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> unStartedBuildingProductTypeSet = new ConcurrentHashSet<>();

        //已售 - 签约口径
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //包含销售系统全部定价部分与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> SaleRoomStreamStatusEnum.SIGNING.getKey().equals(saleRoomStream.getStatus()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        saledBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                //已售
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setSaledArea(area);
                                boManageOverviewPrice.setSaledProductPrice(productPrice);

                                if (saleRoomStream.getRealTakeCardTime() == null){
                                    //已售未取证
                                    BigDecimal saledUnGetCardArea = MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledUnGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                    BigDecimal saledUnGetCardPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                    boManageOverviewPrice.setSaledUnGetCardArea(saledUnGetCardArea);
                                    boManageOverviewPrice.setSaledUnGetCardProductPrice(saledUnGetCardPrice);
                                }else {
                                    //已售已取证
                                    BigDecimal saledGetCardArea = MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                    BigDecimal saledGetCardPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledGetCardProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                    boManageOverviewPrice.setSaledGetCardArea(saledGetCardArea);
                                    boManageOverviewPrice.setSaledGetCardProductPrice(saledGetCardPrice);
                                }
                            }


                        }
                    });
        }

        //已拿证未售
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //销售系统完全定价部分与未完全定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !SaleRoomStreamStatusEnum.SIGNING.getKey().equals(saleRoomStream.getStatus()))
                    .filter(saleRoomStream -> saleRoomStream.getRealTakeCardTime() != null && saleRoomStream.getRealTakeCardTime().isBefore(endTime))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        getCardUnSaleBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnSaleArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnSaleProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setUnSaleArea(area);
                                boManageOverviewPrice.setUnSaleProductPrice(productPrice);
                            }
                        }
                    });

            //货龄Map<业态CODE,Entry<key无意义,value总货值>[0:3月内,1:3-6月,2:6-12月,3:12-24月,4:24月以上]>
            Map<String, Map.Entry<String, BigDecimal>[]> productPriceMap = new ConcurrentHashMap<>();
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        Duration duration = Duration.between(saleRoomStream.getRealTakeCardTime(), endTime);
                        long days = duration.toDays();
                        BigDecimal price = MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal());
                        Map.Entry<String, BigDecimal>[] priceEntry = productPriceMap.get(saleRoomStream.getMainDataProductTypeCode());
                        if (priceEntry == null) {
                            priceEntry = new AbstractMap.SimpleEntry[5];
                            //<3
                            priceEntry[0] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>=3 <6
                            priceEntry[1] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>=6 <12
                            priceEntry[2] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>=12 <24
                            priceEntry[3] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>24
                            priceEntry[4] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            Map.Entry<String, BigDecimal>[] last = productPriceMap.putIfAbsent(saleRoomStream.getMainDataProductTypeCode(), priceEntry);
                            if (last != null) {
                                priceEntry = last;
                            }
                        }

                        //3月内
                        if (days < 3 * 30) {
                            synchronized (priceEntry[0]) {
                                priceEntry[0].setValue(priceEntry[0].getValue().add(price));
                            }
                        } else if (days >= 3 * 30 && days < 6 * 30) {
                            synchronized (priceEntry[1]) {
                                priceEntry[1].setValue(priceEntry[1].getValue().add(price));
                            }
                        } else if (days >= 6 * 30 && days < 12 * 30) {
                            synchronized (priceEntry[2]) {
                                priceEntry[2].setValue(priceEntry[2].getValue().add(price));
                            }
                        } else if (days >= 12 * 30 && days < 24 * 30) {
                            synchronized (priceEntry[3]) {
                                priceEntry[3].setValue(priceEntry[3].getValue().add(price));
                            }
                        } else {
                            synchronized (priceEntry[4]) {
                                priceEntry[4].setValue(priceEntry[4].getValue().add(price));
                            }
                        }
                    });


            //元变万元
            productPriceMap.values().parallelStream().forEach(x -> {
                x[0].setValue(x[0].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[1].setValue(x[1].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[2].setValue(x[2].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[3].setValue(x[3].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[4].setValue(x[4].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            });

            //货龄-同步到业态信息中
            productPriceMap.keySet().parallelStream().forEach(productTypeCode -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + productTypeCode);
                if (boManageOverviewPrice != null) {
                    Map.Entry<String, BigDecimal>[] entries = productPriceMap.get(productTypeCode);
                    boManageOverviewPrice.setAgeFirstProductPrice(entries[0].getValue());
                    boManageOverviewPrice.setAgeSencondProductPrice(entries[1].getValue());
                    boManageOverviewPrice.setAgeThirdProductPrice(entries[2].getValue());
                    boManageOverviewPrice.setAgeFourthProductPrice(entries[3].getValue());
                    boManageOverviewPrice.setAgeFifthProductPrice(entries[4].getValue());
                }
            });
        }

        //已售未售单位转换
        subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
            BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
            synchronized (boManageOverviewPrice) {
                boManageOverviewPrice.setSaledProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getSaledProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                boManageOverviewPrice.setUnSaleProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnSaleProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            }
        });


        //已达形象进度未拿证
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //销售系统全部定价部分与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> saleRoomStream.getRealTakeCardTime() == null || saleRoomStream.getRealTakeCardTime().isAfter(endTime))
                    .filter(saleRoomStream -> reachGoalBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        reachGoalUnGetCardBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        //与本系统均价交互，获得货值
                        mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setUnGetCardArea(area);
                                boManageOverviewPrice.setUnGetCardProductPrice(productPrice);
                            }
                        }
                    });

            //元转万元
            subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                synchronized (boManageOverviewPrice) {
                    boManageOverviewPrice.setUnGetCardProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
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
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardArea()).add(entry.getValue()[0].getValue());
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnGetCardProductPrice()).add(entry.getValue()[1].getValue());
                                boManageOverviewPrice.setUnGetCardArea(area);
                                boManageOverviewPrice.setUnGetCardProductPrice(productPrice);
                            }
                        }
                    }
                });

        //已开工未达形象进度
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)){
            //包含全部定价与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !reachGoalUnGetCardBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> startedUnReachGoalBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        staredUnReachGoalBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        //与本系统均价交互，获得货值
                        mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setUnReachImgGoalArea(area);
                                boManageOverviewPrice.setUnReachImgGoalProductPrice(productPrice);
                            }
                        }
                    });

            //元转万元
            subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                synchronized (boManageOverviewPrice) {
                    boManageOverviewPrice.setUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
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
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalArea()).add(entry.getValue()[0].getValue());
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnReachImgGoalProductPrice()).add(entry.getValue()[1].getValue());
                                boManageOverviewPrice.setUnReachImgGoalArea(area);
                                boManageOverviewPrice.setUnReachImgGoalProductPrice(productPrice);
                            }
                        }
                    }
                });

        //未开工
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)){
            //包含了全部定价与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !reachGoalUnGetCardBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !staredUnReachGoalBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> unStartedBuildingIdSet.contains(saleRoomStream.getMainDataBldId()) || !allSpeedBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        unStartedBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        //与本系统均价交互，获得货值
                        mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setUnStartArea(area);
                                boManageOverviewPrice.setUnStartProductPrice(productPrice);
                            }
                        }
                    });

            //元转万元
            subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                synchronized (boManageOverviewPrice) {
                    boManageOverviewPrice.setUnStartProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
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
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartArea()).add(entry.getValue()[0].getValue());
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getUnStartProductPrice()).add(entry.getValue()[1].getValue());
                                boManageOverviewPrice.setUnStartArea(area);
                                boManageOverviewPrice.setUnStartProductPrice(productPrice);
                            }
                        }
                    }
                });


        //7均价
        subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey()))
                .forEach(key -> {
                    BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                    //已售
                    if (MathUtils.isNoneZero(boManageOverviewPrice.getSaledProductPrice(), boManageOverviewPrice.getSaledArea())) {
                        //货值单位万元要转为元
                        boManageOverviewPrice.setSaledAvgPrice(boManageOverviewPrice.getSaledProductPrice().multiply(K10_BIGDECIMAL).divide(boManageOverviewPrice.getSaledArea(), 2, RoundingMode.HALF_UP));
                    }
                    //未售
                    BigDecimal totalProductPrice = NumberUtil.add(boManageOverviewPrice.getUnStartProductPrice(), boManageOverviewPrice.getUnReachImgGoalProductPrice(), boManageOverviewPrice.getUnGetCardProductPrice(), boManageOverviewPrice.getUnSaleProductPrice());
                    BigDecimal totalArea = NumberUtil.add(boManageOverviewPrice.getUnStartArea(), boManageOverviewPrice.getUnReachImgGoalArea(), boManageOverviewPrice.getUnGetCardArea(), boManageOverviewPrice.getUnSaleArea());
                    if (MathUtils.isNoneZero(totalProductPrice, totalArea)) {
                        //货值单位万元要转为元
                        boManageOverviewPrice.setUnSaleAvgPrice(totalProductPrice.multiply(K10_BIGDECIMAL).divide(totalArea, 2, RoundingMode.HALF_UP));
                    }
                });
    }
    private void makeOverallOfferData(LocalDateTime endTime, Map<String, BoManageOverviewPrice> subMap, Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> localPriceMap, List<SaleRoomStream> saleRoomStreamList, Set<String> allSpeedBuildingIdSet, Set<String> reachGoalBuildingIdSet, Set<String> startedUnReachGoalBuildingIdSet, Set<String> unStartedBuildingIdSet, Set<String> overall) {
        //分已售、已拿证未售、已达形象进度未取证、已开工未达形象进度、未开工
        Set<String> saledBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> getCardUnSaleBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> reachGoalUnGetCardBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> staredUnReachGoalBuildingProductTypeSet = new ConcurrentHashSet<>();
        Set<String> unStartedBuildingProductTypeSet = new ConcurrentHashSet<>();

        //已售-认购口径
        Set<String> saledStatus = Stream.of(SaleRoomStreamStatusEnum.SIGNING.getKey(), SaleRoomStreamStatusEnum.PRE_BUY.getKey()).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //包含销售系统全部定价部分与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> saledStatus.contains(saleRoomStream.getStatus()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        saledBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                //已售
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setOfferSaledArea(area);
                                boManageOverviewPrice.setOfferSaledProductPrice(productPrice);

                                if (saleRoomStream.getRealTakeCardTime() == null){
                                    //已售未取证
                                    BigDecimal saledUnGetCardArea = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledUnGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                    BigDecimal saledUnGetCardPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                    boManageOverviewPrice.setOfferSaledUnGetCardArea(saledUnGetCardArea);
                                    boManageOverviewPrice.setOfferSaledUnGetCardProductPrice(saledUnGetCardPrice);
                                }else {
                                    //已售已取证
                                    BigDecimal saledGetCardArea = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                    BigDecimal saledGetCardPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledGetCardProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                    boManageOverviewPrice.setOfferSaledGetCardArea(saledGetCardArea);
                                    boManageOverviewPrice.setOfferSaledGetCardProductPrice(saledGetCardPrice);
                                }


                            }
                        }
                    });
        }

        //已拿证未售
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //销售系统完全定价部分与未完全定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledStatus.contains(saleRoomStream.getStatus()))
                    .filter(saleRoomStream -> saleRoomStream.getRealTakeCardTime() != null && saleRoomStream.getRealTakeCardTime().isBefore(endTime))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        getCardUnSaleBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnSaleArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnSaleProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setOfferUnSaleArea(area);
                                boManageOverviewPrice.setOfferUnSaleProductPrice(productPrice);
                            }
                        }
                    });

            //货龄Map<业态CODE,Entry<key无意义,value总货值>[0:3月内,1:3-6月,2:6-12月,3:12-24月,4:24月以上]>
            Map<String, Map.Entry<String, BigDecimal>[]> productPriceMap = new ConcurrentHashMap<>();
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        Duration duration = Duration.between(saleRoomStream.getRealTakeCardTime(), endTime);
                        long days = duration.toDays();
                        BigDecimal price = MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal());
                        Map.Entry<String, BigDecimal>[] priceEntry = productPriceMap.get(saleRoomStream.getMainDataProductTypeCode());
                        if (priceEntry == null) {
                            priceEntry = new AbstractMap.SimpleEntry[5];
                            //<3
                            priceEntry[0] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>=3 <6
                            priceEntry[1] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>=6 <12
                            priceEntry[2] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>=12 <24
                            priceEntry[3] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            //>24
                            priceEntry[4] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            Map.Entry<String, BigDecimal>[] last = productPriceMap.putIfAbsent(saleRoomStream.getMainDataProductTypeCode(), priceEntry);
                            if (last != null) {
                                priceEntry = last;
                            }
                        }

                        //3月内
                        if (days < 3 * 30) {
                            synchronized (priceEntry[0]) {
                                priceEntry[0].setValue(priceEntry[0].getValue().add(price));
                            }
                        } else if (days >= 3 * 30 && days < 6 * 30) {
                            synchronized (priceEntry[1]) {
                                priceEntry[1].setValue(priceEntry[1].getValue().add(price));
                            }
                        } else if (days >= 6 * 30 && days < 12 * 30) {
                            synchronized (priceEntry[2]) {
                                priceEntry[2].setValue(priceEntry[2].getValue().add(price));
                            }
                        } else if (days >= 12 * 30 && days < 24 * 30) {
                            synchronized (priceEntry[3]) {
                                priceEntry[3].setValue(priceEntry[3].getValue().add(price));
                            }
                        } else {
                            synchronized (priceEntry[4]) {
                                priceEntry[4].setValue(priceEntry[4].getValue().add(price));
                            }
                        }
                    });


            //元变万元
            productPriceMap.values().parallelStream().forEach(x -> {
                x[0].setValue(x[0].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[1].setValue(x[1].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[2].setValue(x[2].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[3].setValue(x[3].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                x[4].setValue(x[4].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            });

            //货龄-同步到业态信息中
            productPriceMap.keySet().parallelStream().forEach(productTypeCode -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + productTypeCode);
                if (boManageOverviewPrice != null) {
                    Map.Entry<String, BigDecimal>[] entries = productPriceMap.get(productTypeCode);
                    boManageOverviewPrice.setOfferAgeFirstProductPrice(entries[0].getValue());
                    boManageOverviewPrice.setOfferAgeSencondProductPrice(entries[1].getValue());
                    boManageOverviewPrice.setOfferAgeThirdProductPrice(entries[2].getValue());
                    boManageOverviewPrice.setOfferAgeFourthProductPrice(entries[3].getValue());
                    boManageOverviewPrice.setOfferAgeFifthProductPrice(entries[4].getValue());
                }
            });
        }

        //已售未售单位转换
        subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
            BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
            synchronized (boManageOverviewPrice) {
                boManageOverviewPrice.setOfferSaledProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferSaledProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
                boManageOverviewPrice.setOfferUnSaleProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnSaleProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            }
        });


        //已达形象进度未拿证
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            //销售系统全部定价部分与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> saleRoomStream.getRealTakeCardTime() == null || saleRoomStream.getRealTakeCardTime().isAfter(endTime))
                    .filter(saleRoomStream -> reachGoalBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        reachGoalUnGetCardBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        //与本系统均价交互，获得货值
                        mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setOfferUnGetCardArea(area);
                                boManageOverviewPrice.setOfferUnGetCardProductPrice(productPrice);
                            }
                        }
                    });

            //元转万元
            subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                synchronized (boManageOverviewPrice) {
                    boManageOverviewPrice.setOfferUnGetCardProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
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
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardArea()).add(entry.getValue()[0].getValue());
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnGetCardProductPrice()).add(entry.getValue()[1].getValue());
                                boManageOverviewPrice.setOfferUnGetCardArea(area);
                                boManageOverviewPrice.setOfferUnGetCardProductPrice(productPrice);
                            }
                        }
                    }
                });

        //已开工未达形象进度
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)){
            //包含全部定价与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !reachGoalUnGetCardBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> startedUnReachGoalBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        staredUnReachGoalBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        //与本系统均价交互，获得货值
                        mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setOfferUnReachImgGoalArea(area);
                                boManageOverviewPrice.setOfferUnReachImgGoalProductPrice(productPrice);
                            }
                        }
                    });

            //元转万元
            subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                synchronized (boManageOverviewPrice) {
                    boManageOverviewPrice.setOfferUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
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
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalArea()).add(entry.getValue()[0].getValue());
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnReachImgGoalProductPrice()).add(entry.getValue()[1].getValue());
                                boManageOverviewPrice.setOfferUnReachImgGoalArea(area);
                                boManageOverviewPrice.setOfferUnReachImgGoalProductPrice(productPrice);
                            }
                        }
                    }
                });

        //未开工
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)){
            //包含了全部定价与未全部定价部分
            saleRoomStreamList.parallelStream()
                    .filter(saleRoomStream -> !saledBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !getCardUnSaleBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !reachGoalUnGetCardBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> !staredUnReachGoalBuildingProductTypeSet.contains(makeBuildingProductRoomKey(saleRoomStream)))
                    .filter(saleRoomStream -> unStartedBuildingIdSet.contains(saleRoomStream.getMainDataBldId()) || !allSpeedBuildingIdSet.contains(saleRoomStream.getMainDataBldId()))
                    .filter(saleRoomStream -> overall.contains(makeBuildingProductTypeKey(saleRoomStream)))
                    .forEach(saleRoomStream -> {
                        unStartedBuildingProductTypeSet.add(makeBuildingProductRoomKey(saleRoomStream));
                        //与本系统均价交互，获得货值
                        mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                        BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + saleRoomStream.getMainDataProductTypeCode());
                        if (boManageOverviewPrice != null) {
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartArea()).add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea()));
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartProductPrice()).add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal()));
                                boManageOverviewPrice.setOfferUnStartArea(area);
                                boManageOverviewPrice.setOfferUnStartProductPrice(productPrice);
                            }
                        }
                    });

            //元转万元
            subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey())).forEach(key -> {
                BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                synchronized (boManageOverviewPrice) {
                    boManageOverviewPrice.setOfferUnStartProductPrice(MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartProductPrice()).divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
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
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> entryMap = localPriceMap.get(split[0]);
                    if (entryMap != null) {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry = entryMap.get(split[1]);
                        if (entry != null) {
                            BoManageOverviewPrice boManageOverviewPrice = subMap.get(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey() + split[1]);
                            synchronized (boManageOverviewPrice) {
                                BigDecimal area = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartArea()).add(entry.getValue()[0].getValue());
                                BigDecimal productPrice = MathUtils.nullDefaultZero(boManageOverviewPrice.getOfferUnStartProductPrice()).add(entry.getValue()[1].getValue());
                                boManageOverviewPrice.setOfferUnStartArea(area);
                                boManageOverviewPrice.setOfferUnStartProductPrice(productPrice);
                            }
                        }
                    }
                });


        //7均价
        subMap.keySet().parallelStream().filter(key -> key.startsWith(ManageOverviewPriceKeyTypeEnum.PRODUCT_TYPE.getKey()))
                .forEach(key -> {
                    BoManageOverviewPrice boManageOverviewPrice = subMap.get(key);
                    //已售
                    if (MathUtils.isNoneZero(boManageOverviewPrice.getOfferSaledProductPrice(), boManageOverviewPrice.getOfferSaledArea())) {
                        //货值单位万元要转为元
                        boManageOverviewPrice.setOfferSaledAvgPrice(boManageOverviewPrice.getOfferSaledProductPrice().multiply(K10_BIGDECIMAL).divide(boManageOverviewPrice.getOfferSaledArea(), 2, RoundingMode.HALF_UP));
                    }
                    //未售
                    BigDecimal totalProductPrice = NumberUtil.add(boManageOverviewPrice.getOfferUnStartProductPrice(), boManageOverviewPrice.getOfferUnReachImgGoalProductPrice(), boManageOverviewPrice.getOfferUnGetCardProductPrice(), boManageOverviewPrice.getOfferUnSaleProductPrice());
                    BigDecimal totalArea = NumberUtil.add(boManageOverviewPrice.getOfferUnStartArea(), boManageOverviewPrice.getOfferUnReachImgGoalArea(), boManageOverviewPrice.getOfferUnGetCardArea(), boManageOverviewPrice.getOfferUnSaleArea());
                    if (MathUtils.isNoneZero(totalProductPrice, totalArea)) {
                        //货值单位万元要转为元
                        boManageOverviewPrice.setOfferUnSaleAvgPrice(totalProductPrice.multiply(K10_BIGDECIMAL).divide(totalArea, 2, RoundingMode.HALF_UP));
                    }
                });
    }

    private void mergeLocalSaleRoomPrice(Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> localPriceMap, SaleRoomStream saleRoomStream) {
        if (saleRoomStream.getRoomTotal() != null && saleRoomStream.getRoomTotal().doubleValue() > 0){
            return;
        }
        if (SaleRoomStreamStatusEnum.SIGNING.getKey().equals(saleRoomStream.getStatus())){
            return;
        }
        if (!SaleRoomStreamStatusEnum.SIGNING.getKey().equals(saleRoomStream.getStatus()) && saleRoomStream.getRealTakeCardTime() != null){
            return;
        }
            Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> productTypePriceMap = localPriceMap.get(saleRoomStream.getMainDataBldId());
            if (productTypePriceMap != null){
                Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> localPriceEntry = productTypePriceMap.get(saleRoomStream.getMainDataProductTypeCode());
                if (localPriceEntry != null){
                    Map.Entry<String, BigDecimal>[] entry = localPriceEntry.getValue();
                    if (ProductTypeEnum.isCarParkingPlate(saleRoomStream.getMainDataProductTypeCode())){
                        //车位
                        saleRoomStream.setRoomTotal(entry[2].getValue());
                    }else {
                        //非车位
                        saleRoomStream.setRoomTotal(entry[2].getValue().multiply(saleRoomStream.getBldArea()).setScale(2, RoundingMode.HALF_UP));
                    }
                }
            }
    }

    private void makeLocalDisablePriceSubstract(Map<String, Map.Entry<String, BigDecimal[]>> disableSaleBuildingProductTypePriceMap, String buildingProductType, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> entry) {
        Map.Entry<String, BigDecimal[]> saleEntry = disableSaleBuildingProductTypePriceMap.get(buildingProductType);
        if (saleEntry != null){
            Map.Entry<String, BigDecimal>[] localValue = entry.getValue();
            BigDecimal[] saleValue = saleEntry.getValue();
            localValue[0].setValue(localValue[0].getValue().subtract(saleValue[0]));
            localValue[1].setValue(localValue[1].getValue().subtract(saleValue[1]));
        }
    }

    private String makeBuildingProductTypeKey(SaleRoomStream saleRoomStream) {
        return saleRoomStream.getMainDataBldId()+","+saleRoomStream.getMainDataProductTypeCode();
    }

    private String makeBuildingProductRoomKey(SaleRoomStream saleRoomStream) {
        return makeBuildingProductTypeKey(saleRoomStream) + "," + saleRoomStream.getRoomGuid();
    }

    private void mergeLocalSale(Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> localPriceMap, Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>>> salePriceMap) {
        //合并为动态货值（营销系统的覆盖本系统的）
        if (!salePriceMap.isEmpty()) {
            localPriceMap.keySet().parallelStream().forEach(buildingOriginId -> {
                Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>> saleProductTypeCodePriceMap = salePriceMap.get(buildingOriginId);
                if (saleProductTypeCodePriceMap != null) {
                    Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> localProductTypeCodePriceMap = localPriceMap.get(buildingOriginId);
                    localProductTypeCodePriceMap.keySet().parallelStream().forEach(productTypeCode -> {
                        Map.Entry<String, Map.Entry<String, BigDecimal>[]> salePriceEntry = saleProductTypeCodePriceMap.get(productTypeCode);
                        if (salePriceEntry != null) {
                            Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> localPriceEntry = localProductTypeCodePriceMap.get(productTypeCode);
                            //销售均价被本系统均价替换
                            Map.Entry<String, BigDecimal>[] localEntry = localPriceEntry.getValue();
                            Map.Entry<String, BigDecimal>[] saleEntry = salePriceEntry.getValue();
                            saleEntry[2].setValue(localEntry[2].getValue());
                            localPriceEntry.setValue(salePriceEntry.getValue());
                        }
                    });
                }
            });
        }
    }

    private Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>>> makeSalePrice(List<SaleRoomStream> saleRoomStreamList, Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> localPriceMap) {
        //营销系统 - Map<楼栋origin_id,Map<业态code,Entry<key无意义,Entry<key无意义,value为数值>[0：总面积，1：货值]，2均价>>>
        Map<String, Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>>> salePrice = new ConcurrentHashMap<>();
        if (CollectionUtils.isNotEmpty(saleRoomStreamList)) {
            saleRoomStreamList.parallelStream().forEach(saleRoomStream -> {
                Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>> productTypeCodePriceMap = salePrice.get(saleRoomStream.getMainDataBldId());
                if (productTypeCodePriceMap == null) {
                    productTypeCodePriceMap = new ConcurrentHashMap<>();
                    Map<String, Map.Entry<String, Map.Entry<String, BigDecimal>[]>> last = salePrice.putIfAbsent(saleRoomStream.getMainDataBldId(), productTypeCodePriceMap);
                    if (last != null) {
                        productTypeCodePriceMap = last;
                    }
                }
                Map.Entry<String, Map.Entry<String, BigDecimal>[]> priceEntry = productTypeCodePriceMap.get(saleRoomStream.getMainDataProductTypeCode());
                if (priceEntry == null) {
                    priceEntry = new AbstractMap.SimpleEntry<>("", new AbstractMap.SimpleEntry[3]);
                    Map.Entry<String, BigDecimal>[] innerEntry = priceEntry.getValue();
                    innerEntry[0] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                    innerEntry[1] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                    innerEntry[2] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                    Map.Entry<String, Map.Entry<String, BigDecimal>[]> last = productTypeCodePriceMap.putIfAbsent(saleRoomStream.getMainDataProductTypeCode(), priceEntry);
                    if (last != null) {
                        priceEntry = last;
                    }
                }
                Map.Entry<String, BigDecimal>[] innerEntry = priceEntry.getValue();
                synchronized (innerEntry[0]) {
                    //面积
                    innerEntry[0].setValue(innerEntry[0].getValue().add(MathUtils.nullDefaultZero(saleRoomStream.getBldArea())).setScale(2, RoundingMode.HALF_UP));
                }
                synchronized (innerEntry[1]) {
                    mergeLocalSaleRoomPrice(localPriceMap, saleRoomStream);
                    //货值
                    innerEntry[1].setValue(innerEntry[1].getValue().add(MathUtils.nullDefaultZero(saleRoomStream.getRoomTotal())).setScale(2, RoundingMode.HALF_UP));
                }
            });
            //元转万元
            salePrice.values().parallelStream().flatMap(x -> x.values().parallelStream()).forEach(x -> {
                Map.Entry<String, BigDecimal>[] entrys = x.getValue();
                entrys[1].setValue(entrys[1].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            });
        }
        return salePrice;
    }

    /**
     * 本系统计算货值
     *
     * @param projectPriceQuotaList
     * @param buildingProductTypeList
     * @param buildingList
     * @param buildingProductTypeQuotaList
     * @return
     */
    private Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> makeLocalPrice(List<BoProjectPriceQuotaMap> projectPriceQuotaList, List<BoBuildingProductTypeMap> buildingProductTypeList, List<BoBuilding> buildingList, List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList) {
        //本系统 - Map<楼栋origin_id,Map<业态code,Entry<示例楼栋业态对象,Entry<key无意义,value为数值>[0：总可售面积，1：货值,2：均价]>>>
        Map<String, Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>>> buildingOriginIdProductTypeCodePriceMap = new ConcurrentHashMap<>();
        Set<String> canSaleAreaQuota = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey()).collect(Collectors.toSet());
        Set<String> canSaleCarParkingPlateQuota = Stream.of(QuotaCodeEnum.SIMPLE_CAR_PARKING_PLATE_COUNT.getKey()).collect(Collectors.toSet());
        buildingList.parallelStream().forEach(building -> {
            Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> productTypeCodePriceMap = buildingOriginIdProductTypeCodePriceMap.get(building.getOriginId());
            if (productTypeCodePriceMap == null) {
                productTypeCodePriceMap = new ConcurrentHashMap<>();
                Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> last = buildingOriginIdProductTypeCodePriceMap.putIfAbsent(building.getOriginId(), productTypeCodePriceMap);
                if (last != null) {
                    productTypeCodePriceMap = last;
                }
            }
            Map<String, Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]>> finalProductTypeCodePriceMap = productTypeCodePriceMap;
            buildingProductTypeList.parallelStream()
                    .filter(buildingProductType -> buildingProductType.getBuildingId().equals(building.getId()))
                    .forEach(buildingProductType -> {
                        Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> priceEntry = finalProductTypeCodePriceMap.get(buildingProductType.getProductTypeCode());
                        if (priceEntry == null) {
                            priceEntry = new AbstractMap.SimpleEntry<>(buildingProductType, new AbstractMap.SimpleEntry[3]);
                            Map.Entry<String, BigDecimal>[] innerEntry = priceEntry.getValue();
                            innerEntry[0] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            innerEntry[1] = new AbstractMap.SimpleEntry<>("", BigDecimal.ZERO);
                            innerEntry[2] = new AbstractMap.SimpleEntry<>("", null);
                            Map.Entry<BoBuildingProductTypeMap, Map.Entry<String, BigDecimal>[]> last = finalProductTypeCodePriceMap.putIfAbsent(buildingProductType.getProductTypeCode(), priceEntry);
                            if (last != null) {
                                priceEntry = last;
                            }
                        }
                        Map.Entry<String, BigDecimal>[] innerEntry = priceEntry.getValue();
                        buildingProductTypeQuotaList.parallelStream()
                                .filter(boBuildingProductTypeQuotaMap -> boBuildingProductTypeQuotaMap.getBuildingProductTypeId().equals(buildingProductType.getId()))
                                .filter(boBuildingProductTypeQuotaMap -> ProductTypeEnum.isCarParkingPlate(buildingProductType.getProductTypeCode()) ? canSaleCarParkingPlateQuota.contains(boBuildingProductTypeQuotaMap.getQuotaCode()) : canSaleAreaQuota.contains(boBuildingProductTypeQuotaMap.getQuotaCode()))
                                .map(boBuildingProductTypeQuotaMap -> MathUtils.newBigDecimal(boBuildingProductTypeQuotaMap.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                .reduce(BigDecimal::add)
                                .ifPresent(entity -> {
                                    synchronized (innerEntry[0]) {
                                        //车位要计算总可售面积
                                        if (ProductTypeEnum.isCarParkingPlate(buildingProductType.getProductTypeCode())) {
                                            buildingProductTypeQuotaList.parallelStream()
                                                    .filter(boBuildingProductTypeQuotaMap -> boBuildingProductTypeQuotaMap.getBuildingProductTypeId().equals(buildingProductType.getId()))
                                                    .filter(boBuildingProductTypeQuotaMap -> canSaleAreaQuota.contains(boBuildingProductTypeQuotaMap.getQuotaCode()))
                                                    .map(boBuildingProductTypeQuotaMap -> MathUtils.newBigDecimal(boBuildingProductTypeQuotaMap.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                                    .reduce(BigDecimal::add)
                                                    .ifPresent(carEntity -> {
                                                        innerEntry[0].setValue(innerEntry[0].getValue().add(carEntity).setScale(2, RoundingMode.HALF_UP));
                                                    });
                                        } else {
                                            innerEntry[0].setValue(innerEntry[0].getValue().add(entity).setScale(2, RoundingMode.HALF_UP));
                                        }
                                    }
                                    projectPriceQuotaList.parallelStream()
                                            .filter(quota -> quota.getRefId().equals(SecureUtil.md5(buildingProductType.getProductTypeCode())))
                                            .filter(quota -> QuotaCodeEnum.AVERAGE_PRICE.getKey().equals(quota.getQuotaCode()))
                                            .map(quota -> MathUtils.newBigDecimal(quota.getQuotaValue()).setScale(2, RoundingMode.HALF_UP))
                                            .findFirst()
                                            .ifPresent(avg -> {
                                                if (innerEntry[2].getValue() == null){
                                                    synchronized (innerEntry[2]){
                                                        if (innerEntry[2].getValue() == null){
                                                            innerEntry[2].setValue(avg);
                                                        }
                                                    }
                                                }

                                                synchronized (innerEntry[1]) {
                                                    BigDecimal price = entity.multiply(avg);
                                                    innerEntry[1].setValue(innerEntry[1].getValue().add(price));
                                                }
                                            });
                                });
                    });
        });
        buildingOriginIdProductTypeCodePriceMap.values().parallelStream().flatMap(x -> x.values().parallelStream()).forEach(x -> {
            Map.Entry<String, BigDecimal>[] entrys = x.getValue();
            entrys[1].setValue(entrys[1].getValue().divide(K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
        });
        return buildingOriginIdProductTypeCodePriceMap;
    }
}
