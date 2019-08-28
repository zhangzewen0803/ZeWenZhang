package com.tahoecn.bo.controller.webapi;

import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.ManageOverviewVersionTypeEnum;
import com.tahoecn.bo.common.enums.OrgTypeCodeEnum;
import com.tahoecn.bo.common.utils.*;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.entity.BoManageOverviewArea;
import com.tahoecn.bo.model.entity.BoManageOverviewPrice;
import com.tahoecn.bo.model.entity.BoManageOverviewVersion;
import com.tahoecn.bo.model.entity.UcOrg;
import com.tahoecn.bo.model.vo.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.collection.ConcurrentHashSet;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.core.util.NumberUtil;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 经营概览 API
 *
 * @author panglx
 */
@Api(tags = "经营概览API", value = "经营概览API")
@RestController
@RequestMapping(value = "/api/manageOverview")
public class ManageOverviewController extends TahoeBaseController {

    private static final Log logger = LogFactory.get();
    public static final BigDecimal K10_BIGDECIMAL = MathUtils.newBigDecimal("10000");

    @Autowired
    private BoManageOverviewPriceService boManageOverviewPriceService;

    @Autowired
    private BoSysPermissionService boSysPermissionService;

    @Autowired
    private BoManageOverviewVersionService boManageOverviewVersionService;

    @Autowired
    private BoManageOverviewAreaService boManageOverviewAreaService;

    @Autowired
    private UcOrgService ucOrgService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${tahoe.uc.land-plate.org.id}")
    private String landPlateId;

    @Value("${tahoe.uc.tour.org.id}")
    private String tourId;

    @Value("${tahoe.uc.direct-city.org.id}")
    private String directCityId;

    @ApiOperation(value = "查询货值概览数据", httpMethod = "POST", notes = "查询货值概览数据")
    @RequestMapping(value = "/getPriceList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "endTime", value = "截止时间，格式yyyy-MM-dd", required = true, paramType = "query"),
            @ApiImplicitParam(name = "parentOrgId", value = "父级组织ID,暂时不启用", required = false, paramType = "query"),
    })
    public JSONResult<List<ManageOverviewPriceVO>> getPriceList(@NotBlank(message = "endTime不能为空") String endTime, String parentOrgId) {
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return JsonResultBuilder.failed("endTime格式不正确");
        }
        CurrentUserVO currentUser = getCurrentUser();
        List<BoManageOverviewPrice> listByEndDimeAndUcOrgList;
        if (endDate.isEqual(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            //当天查缓存
            listByEndDimeAndUcOrgList = (List<BoManageOverviewPrice>) redisTemplate.opsForValue().get(RedisConstants.MANAGE_OVERVIEW_PRICE_TODAY);
            if (listByEndDimeAndUcOrgList == null) {
                listByEndDimeAndUcOrgList = boManageOverviewPriceService.makeData(endDate);
            }
        } else {
            //历史版本查数据库
            BoManageOverviewVersion priceVersion = boManageOverviewVersionService.getByEndDate(ManageOverviewVersionTypeEnum.PRODUCT_PRICE,endDate);
            if (priceVersion == null) {
                // 不存在的历史版本需要生成
                listByEndDimeAndUcOrgList = boManageOverviewPriceService.makeData(endDate);
            } else {
                listByEndDimeAndUcOrgList = boManageOverviewPriceService.getListByVersionIdAndUcOrgList(priceVersion.getId(), parentOrgId);
            }
        }
        Map<String, BoManageOverviewPrice> boManageOverviewPriceMap = listByEndDimeAndUcOrgList.parallelStream().collect(Collectors.toMap(BoManageOverviewPrice::getOrgId, x -> x, (o, n) -> n));

        //查组织机构
        List<UcOrg> orgList = ucOrgService.getChildrenByFdSid(landPlateId);
        orgList = orgList.parallelStream().filter(x -> StringUtils.isNotBlank(x.getFdSidTree()) ? !x.getFdSidTree().contains(tourId) : false).collect(Collectors.toList());
        List<UcOrg> filteredOrgList = this.recomPowerProjectData(currentUser.getUsername(), orgList);

        //将组织机构与货值绑定
        Set<String> projectAndSubSet = new ConcurrentHashSet<>();
        Map<String, List<ManageOverviewPriceVO>> projectPriceMap = new ConcurrentHashMap<>();
        List<ManageOverviewPriceVO> result = filteredOrgList.parallelStream().map(x -> {
            //制作projectAndSubSet为了后面过滤业态与地块
            if (OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode().equals(x.getFdType()) || OrgTypeCodeEnum.UC_LAND_PROJECT.getCode().equals(x.getFdType())) {
                projectAndSubSet.add(x.getFdSid());
            }

            //对象转换，没有汇总的组织，赋予默认对象
            ManageOverviewPriceVO manageOverviewPriceVO = new ManageOverviewPriceVO();
            if (x.getFdSid().equals(directCityId)){
                //直管城市 排最后
                manageOverviewPriceVO.setSortNo(Integer.MAX_VALUE);
            }else {
                manageOverviewPriceVO.setSortNo(x.getFdOrder());
            }
            BoManageOverviewPrice boManageOverviewPrice = boManageOverviewPriceMap.get(x.getFdSid());
            if (boManageOverviewPrice == null) {
                manageOverviewPriceVO.setOrgId(x.getFdSid());
                manageOverviewPriceVO.setOrgName(x.getFdName());
                manageOverviewPriceVO.setOrgType(x.getFdType());
                manageOverviewPriceVO.setParentOrgId(x.getFdPsid());
            } else {
                try {
                    PropertyUtils.copyProperties(manageOverviewPriceVO, boManageOverviewPrice);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            if (OrgTypeCodeEnum.UC_LAND_PROJECT.getCode().equals(x.getFdType())) {
                List<ManageOverviewPriceVO> list = projectPriceMap.get(x.getFdPsid());
                if (list == null) {
                    list = Collections.synchronizedList(new ArrayList<>());
                    List<ManageOverviewPriceVO> tmp = projectPriceMap.putIfAbsent(x.getFdPsid(), list);
                    if (tmp != null) {
                        list = tmp;
                    }
                }
                list.add(manageOverviewPriceVO);
            }
            return manageOverviewPriceVO;
        }).collect(Collectors.toList());

        //组织机构汇总货值
        orgSumPrice(projectPriceMap, result);

        //加入业态/地块
        Set<String> priceMap = listByEndDimeAndUcOrgList.parallelStream().map(x -> x.getOrgId()).collect(Collectors.toSet());
        listByEndDimeAndUcOrgList.stream().filter(x->OrgTypeCodeEnum.LAND_PART.getCode().equals(x.getOrgType()) && projectAndSubSet.contains(x.getParentOrgId())).forEach(x->projectAndSubSet.add(x.getOrgId()));
        List<ManageOverviewPriceVO> productTypeResult = listByEndDimeAndUcOrgList.parallelStream()
                .filter(x -> ((OrgTypeCodeEnum.PRODUCT_TYPE.getCode().equals(x.getOrgType()) || OrgTypeCodeEnum.LAND_PART.getCode().equals(x.getOrgType()))) && (projectAndSubSet.contains(x.getParentOrgId())))
                .filter(x-> MathUtils.existNotZero(
                        //全盘
                        x.getOverallProductPrice(),
                        //经营决策会
                        x.getManageDecisionProductPrice(),
                        //投决会
                        x.getInvestDecisionProductPrice()
                        ))
                .map(x -> {
                    ManageOverviewPriceVO manageOverviewPriceVO = VOUtils.convert2ManageOverviewPriceVO(x);
                    //地块要排在分期后面
                    manageOverviewPriceVO.setSortNo(Integer.MAX_VALUE);
                    return manageOverviewPriceVO;
                })
                .collect(Collectors.toList());
        result.addAll(productTypeResult);

        //货值-单位万元 转为 单位亿元，面积单位平米 ，改为万平米
        result.parallelStream().forEach(x -> {
            //全盘
            x.setOverallProductPrice(NumberUtil.div(x.getOverallProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //投资决策会
            x.setInvestDecisionProductPrice(NumberUtil.div(x.getInvestDecisionProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //经营决策会
            x.setManageDecisionProductPrice(NumberUtil.div(x.getManageDecisionProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //未开发
            x.setUnDevelopProductPrice(NumberUtil.div(x.getUnDevelopProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //未开工
            x.setUnStartProductPrice(NumberUtil.div(x.getUnStartProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已开工未达形象进度
            x.setUnReachImgGoalProductPrice(NumberUtil.div(x.getUnReachImgGoalProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已达形象进度未取证
            x.setUnGetCardProductPrice(NumberUtil.div(x.getUnGetCardProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已取证未售
            x.setUnSaleProductPrice(NumberUtil.div(x.getUnSaleProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已售
            x.setSaledProductPrice(NumberUtil.div(x.getSaledProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));

            //货龄
            x.setAgeFirstProductPrice(NumberUtil.div(x.getAgeFirstProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setAgeSencondProductPrice(NumberUtil.div(x.getAgeSencondProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setAgeThirdProductPrice(NumberUtil.div(x.getAgeThirdProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setAgeFourthProductPrice(NumberUtil.div(x.getAgeFourthProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setAgeFifthProductPrice(NumberUtil.div(x.getAgeFifthProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));


            /**
             * 面积
             */
            x.setUnDevelopArea(NumberUtil.div(x.getUnDevelopArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnStartArea(NumberUtil.div(x.getUnStartArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnReachImgGoalArea(NumberUtil.div(x.getUnReachImgGoalArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnGetCardArea(NumberUtil.div(x.getUnGetCardArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnSaleArea(NumberUtil.div(x.getUnSaleArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setSaledArea(NumberUtil.div(x.getSaledArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));

            /**
             * 认购口径
             */
            //未开工
            x.setOfferUnStartProductPrice(NumberUtil.div(x.getOfferUnStartProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已开工未达形象进度
            x.setOfferUnReachImgGoalProductPrice(NumberUtil.div(x.getOfferUnReachImgGoalProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已达形象进度未取证
            x.setOfferUnGetCardProductPrice(NumberUtil.div(x.getOfferUnGetCardProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已取证未售
            x.setOfferUnSaleProductPrice(NumberUtil.div(x.getOfferUnSaleProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //已售
            x.setOfferSaledProductPrice(NumberUtil.div(x.getOfferSaledProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));

            //货龄
            x.setOfferAgeFirstProductPrice(NumberUtil.div(x.getOfferAgeFirstProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferAgeSencondProductPrice(NumberUtil.div(x.getOfferAgeSencondProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferAgeThirdProductPrice(NumberUtil.div(x.getOfferAgeThirdProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferAgeFourthProductPrice(NumberUtil.div(x.getOfferAgeFourthProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferAgeFifthProductPrice(NumberUtil.div(x.getOfferAgeFifthProductPrice(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));


            /**
             * 面积
             */
            x.setOfferUnStartArea(NumberUtil.div(x.getOfferUnStartArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferUnReachImgGoalArea(NumberUtil.div(x.getOfferUnReachImgGoalArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferUnGetCardArea(NumberUtil.div(x.getOfferUnGetCardArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferUnSaleArea(NumberUtil.div(x.getOfferUnSaleArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setOfferSaledArea(NumberUtil.div(x.getOfferSaledArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));

        });

        //计算未售
        result.parallelStream().forEach(x->{
            BigDecimal res = NumberUtil.add(x.getUnDevelopProductPrice(), x.getUnStartProductPrice(), x.getUnReachImgGoalProductPrice(), x.getUnGetCardProductPrice(), x.getUnSaleProductPrice());
            x.setRealUnSaleProductPrice(res.setScale(2,RoundingMode.HALF_UP));
        });

        //排序
        result = result.parallelStream().sorted((a, b) -> MathUtils.compare(a.getSortNo(), b.getSortNo())).collect(Collectors.toList());
        return JsonResultBuilder.successWithData(result);
    }

    private void orgSumPrice(Map<String, List<ManageOverviewPriceVO>> projectPriceMap, List<ManageOverviewPriceVO> result) {
        //汇总虚拟地产项目级
        Map<String, List<ManageOverviewPriceVO>> virtualPriceMap = new ConcurrentHashMap<>();
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.UC_LAND_VIRTUAL_DIR.getCode().equals(x.getOrgType())) {
                List<ManageOverviewPriceVO> priceList = projectPriceMap.get(x.getOrgId());
                childrenSumPrice(virtualPriceMap, x, priceList);
            }
        });

        //汇总城市
        Map<String, List<ManageOverviewPriceVO>> cityPriceMap = new ConcurrentHashMap<>();
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.CITY.getCode().equals(x.getOrgType())) {
                List<ManageOverviewPriceVO> priceList = virtualPriceMap.get(x.getOrgId());
                childrenSumPrice(cityPriceMap, x, priceList);
            }
        });

        //汇总区域
        Map<String, List<ManageOverviewPriceVO>> areaPriceMap = new ConcurrentHashMap<>();
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.AREA.getCode().equals(x.getOrgType())) {
                List<ManageOverviewPriceVO> priceList = cityPriceMap.get(x.getOrgId());
                childrenSumPrice(areaPriceMap, x, priceList);
            }
        });

        //汇总板块
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.PLATE.getCode().equals(x.getOrgType())) {
                List<ManageOverviewPriceVO> priceList = areaPriceMap.get(x.getOrgId());
                if (priceList != null) {
                    commonSumPrice(x, priceList);
                }
            }
        });
    }

    private void childrenSumPrice(Map<String, List<ManageOverviewPriceVO>> areaPriceMap, ManageOverviewPriceVO x, List<ManageOverviewPriceVO> priceList) {
        if (priceList != null) {
            commonSumPrice(x, priceList);
            List<ManageOverviewPriceVO> list = areaPriceMap.get(x.getParentOrgId());
            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>());
                List<ManageOverviewPriceVO> tmp = areaPriceMap.putIfAbsent(x.getParentOrgId(), list);
                if (tmp != null) {
                    list = tmp;
                }
            }
            list.add(x);
        }
    }

    private void commonSumPrice(ManageOverviewPriceVO x, List<ManageOverviewPriceVO> priceList) {
        try {
            priceList.parallelStream().forEach(y->{
                synchronized (x) {
                    //汇总 - 全盘货值
                    x.setOverallProductPrice(MathUtils.nullDefaultZero(x.getOverallProductPrice()).add(MathUtils.nullDefaultZero(y.getOverallProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 经营决策会货值
                    x.setManageDecisionProductPrice(MathUtils.nullDefaultZero(x.getManageDecisionProductPrice()).add(MathUtils.nullDefaultZero(y.getManageDecisionProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 投决会
                    x.setInvestDecisionProductPrice(MathUtils.nullDefaultZero(x.getInvestDecisionProductPrice()).add(MathUtils.nullDefaultZero(y.getInvestDecisionProductPrice())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 未开工货值
                    x.setUnStartProductPrice(MathUtils.nullDefaultZero(x.getUnStartProductPrice()).add(MathUtils.nullDefaultZero(y.getUnStartProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 未开工 面积
                    x.setUnStartArea(MathUtils.nullDefaultZero(x.getUnStartArea()).add(MathUtils.nullDefaultZero(y.getUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已开工未达形象进度货值
                    x.setUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(x.getUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(y.getUnReachImgGoalProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已开工未达形象进度面积
                    x.setUnReachImgGoalArea(MathUtils.nullDefaultZero(x.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(y.getUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已达形象进度未取证货值
                    x.setUnGetCardProductPrice(MathUtils.nullDefaultZero(x.getUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(y.getUnGetCardProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已达形象进度未取证面面积
                    x.setUnGetCardArea(MathUtils.nullDefaultZero(x.getUnGetCardArea()).add(MathUtils.nullDefaultZero(y.getUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已取证未售 （存货）货值
                    x.setUnSaleProductPrice(MathUtils.nullDefaultZero(x.getUnSaleProductPrice()).add(MathUtils.nullDefaultZero(y.getUnSaleProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已取证未售 （存货）面积
                    x.setUnSaleArea(MathUtils.nullDefaultZero(x.getUnSaleArea()).add(MathUtils.nullDefaultZero(y.getUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已售货值
                    x.setSaledProductPrice(MathUtils.nullDefaultZero(x.getSaledProductPrice()).add(MathUtils.nullDefaultZero(y.getSaledProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已售面积
                    x.setSaledArea(MathUtils.nullDefaultZero(x.getSaledArea()).add(MathUtils.nullDefaultZero(y.getSaledArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 未开发货值
                    x.setUnDevelopProductPrice(MathUtils.nullDefaultZero(x.getUnDevelopProductPrice()).add(MathUtils.nullDefaultZero(y.getUnDevelopProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 -  未开发面积
                    x.setUnDevelopArea(MathUtils.nullDefaultZero(x.getUnDevelopArea()).add(MathUtils.nullDefaultZero(y.getUnDevelopArea())).setScale(2, RoundingMode.HALF_UP));


                    //货龄
                    x.setAgeFirstProductPrice(MathUtils.nullDefaultZero(x.getAgeFirstProductPrice()).add(MathUtils.nullDefaultZero(y.getAgeFirstProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setAgeSencondProductPrice(MathUtils.nullDefaultZero(x.getAgeSencondProductPrice()).add(MathUtils.nullDefaultZero(y.getAgeSencondProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setAgeThirdProductPrice(MathUtils.nullDefaultZero(x.getAgeThirdProductPrice()).add(MathUtils.nullDefaultZero(y.getAgeThirdProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setAgeFourthProductPrice(MathUtils.nullDefaultZero(x.getAgeFourthProductPrice()).add(MathUtils.nullDefaultZero(y.getAgeFourthProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setAgeFifthProductPrice(MathUtils.nullDefaultZero(x.getAgeFifthProductPrice()).add(MathUtils.nullDefaultZero(y.getAgeFifthProductPrice())).setScale(2, RoundingMode.HALF_UP));


                    /**
                     * 认购口径
                     */
                    //汇总 - 未开工货值
                    x.setOfferUnStartProductPrice(MathUtils.nullDefaultZero(x.getOfferUnStartProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferUnStartProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 未开工 面积
                    x.setOfferUnStartArea(MathUtils.nullDefaultZero(x.getOfferUnStartArea()).add(MathUtils.nullDefaultZero(y.getOfferUnStartArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已开工未达形象进度货值
                    x.setOfferUnReachImgGoalProductPrice(MathUtils.nullDefaultZero(x.getOfferUnReachImgGoalProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferUnReachImgGoalProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已开工未达形象进度面积
                    x.setOfferUnReachImgGoalArea(MathUtils.nullDefaultZero(x.getOfferUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(y.getOfferUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已达形象进度未取证货值
                    x.setOfferUnGetCardProductPrice(MathUtils.nullDefaultZero(x.getOfferUnGetCardProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferUnGetCardProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已达形象进度未取证面面积
                    x.setOfferUnGetCardArea(MathUtils.nullDefaultZero(x.getOfferUnGetCardArea()).add(MathUtils.nullDefaultZero(y.getOfferUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已取证未售 （存货）货值
                    x.setOfferUnSaleProductPrice(MathUtils.nullDefaultZero(x.getOfferUnSaleProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferUnSaleProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已取证未售 （存货）面积
                    x.setOfferUnSaleArea(MathUtils.nullDefaultZero(x.getOfferUnSaleArea()).add(MathUtils.nullDefaultZero(y.getOfferUnSaleArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 已售货值
                    x.setOfferSaledProductPrice(MathUtils.nullDefaultZero(x.getOfferSaledProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferSaledProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已售面积
                    x.setOfferSaledArea(MathUtils.nullDefaultZero(x.getOfferSaledArea()).add(MathUtils.nullDefaultZero(y.getOfferSaledArea())).setScale(2, RoundingMode.HALF_UP));


                    //货龄
                    x.setOfferAgeFirstProductPrice(MathUtils.nullDefaultZero(x.getOfferAgeFirstProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferAgeFirstProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setOfferAgeSencondProductPrice(MathUtils.nullDefaultZero(x.getOfferAgeSencondProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferAgeSencondProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setOfferAgeThirdProductPrice(MathUtils.nullDefaultZero(x.getOfferAgeThirdProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferAgeThirdProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setOfferAgeFourthProductPrice(MathUtils.nullDefaultZero(x.getOfferAgeFourthProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferAgeFourthProductPrice())).setScale(2, RoundingMode.HALF_UP));
                    x.setOfferAgeFifthProductPrice(MathUtils.nullDefaultZero(x.getOfferAgeFifthProductPrice()).add(MathUtils.nullDefaultZero(y.getOfferAgeFifthProductPrice())).setScale(2, RoundingMode.HALF_UP));

                }
            });

            //计算均价
            if (MathUtils.isNoneZero(x.getSaledArea(), x.getSaledProductPrice())) {
                //已售均价
                x.setSaledAvgPrice(x.getSaledProductPrice().multiply(K10_BIGDECIMAL).divide(x.getSaledArea(), 2, RoundingMode.HALF_UP));
            }
            //未售
            BigDecimal totalProductPrice = NumberUtil.add(x.getUnStartProductPrice(), x.getUnReachImgGoalProductPrice(), x.getUnGetCardProductPrice(), x.getUnSaleProductPrice());
            BigDecimal totalArea = NumberUtil.add(x.getUnStartArea(), x.getUnReachImgGoalArea(), x.getUnGetCardArea(), x.getUnSaleArea());
            if (MathUtils.isNoneZero(totalProductPrice, totalArea)) {
                //货值单位万元要转为元
                x.setUnSaleAvgPrice(totalProductPrice.multiply(K10_BIGDECIMAL).divide(totalArea, 2, RoundingMode.HALF_UP));
            }
            if (MathUtils.isNoneZero(x.getUnDevelopProductPrice(),x.getUnDevelopArea())){
                //未开发均价
                x.setUnDevelopAvgPrice(x.getUnDevelopProductPrice().multiply(K10_BIGDECIMAL).divide(x.getUnDevelopArea(), 2, RoundingMode.HALF_UP));
            }

            //计算均价 - 认购口径
            if (MathUtils.isNoneZero(x.getOfferSaledArea(), x.getOfferSaledProductPrice())) {
                //已售均价
                x.setOfferSaledAvgPrice(x.getOfferSaledProductPrice().multiply(K10_BIGDECIMAL).divide(x.getOfferSaledArea(), 2, RoundingMode.HALF_UP));
            }
            //未售
            BigDecimal offerTotalProductPrice = NumberUtil.add(x.getOfferUnStartProductPrice(), x.getOfferUnReachImgGoalProductPrice(), x.getOfferUnGetCardProductPrice(), x.getOfferUnSaleProductPrice());
            BigDecimal offerTotalArea = NumberUtil.add(x.getOfferUnStartArea(), x.getOfferUnReachImgGoalArea(), x.getOfferUnGetCardArea(), x.getOfferUnSaleArea());
            if (MathUtils.isNoneZero(offerTotalProductPrice, offerTotalArea)) {
                //货值单位万元要转为元
                x.setOfferUnSaleAvgPrice(offerTotalProductPrice.multiply(K10_BIGDECIMAL).divide(offerTotalArea, 2, RoundingMode.HALF_UP));
            }
        } catch (Exception e) {
            logger.error(e,e.getMessage() + "ManageOverviewQueryVO: {},priceList: {}", x, priceList);
        }
    }

    @ApiOperation(value = "查询面积概览数据", httpMethod = "POST", notes = "查询面积概览数据")
    @RequestMapping(value = "/getAreaList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "endTime", value = "截止时间，格式yyyy-MM-dd", required = true, paramType = "query"),
            @ApiImplicitParam(name = "parentOrgId", value = "父级组织ID,暂时不启用", required = false, paramType = "query"),
    })
    public JSONResult<List<ManageOverviewAreaVO>> getAreaList(@NotBlank(message = "endTime不能为空") String endTime, String parentOrgId) {
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return JsonResultBuilder.failed("endTime格式不正确");
        }
        CurrentUserVO currentUser = getCurrentUser();
        List<BoManageOverviewArea> areaList;
        if (endDate.isEqual(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            //当天查缓存
            areaList = (List<BoManageOverviewArea>) redisTemplate.opsForValue().get(RedisConstants.MANAGE_OVERVIEW_AREA_TODAY);
            if (areaList == null) {
                areaList = boManageOverviewAreaService.makeData(endDate);
            }
        } else {
            //历史版本查数据库
            BoManageOverviewVersion priceVersion = boManageOverviewVersionService.getByEndDate(ManageOverviewVersionTypeEnum.AREA,endDate);
            if (priceVersion == null) {
                // 不存在的历史版本需要生成
                areaList = boManageOverviewAreaService.makeData(endDate);
            } else {
                areaList = boManageOverviewAreaService.getListByVersionIdAndUcOrgList(priceVersion.getId(), parentOrgId);
            }
        }
        Map<String, BoManageOverviewArea> boManageOverviewAreaMap = areaList.parallelStream().collect(Collectors.toMap(BoManageOverviewArea::getOrgId, x -> x, (o, n) -> n));

        //查组织机构
        List<UcOrg> orgList = ucOrgService.getChildrenByFdSid(landPlateId);
        orgList = orgList.parallelStream().filter(x -> StringUtils.isNotBlank(x.getFdSidTree()) ? !x.getFdSidTree().contains(tourId) : false).collect(Collectors.toList());
        List<UcOrg> filteredOrgList = this.recomPowerProjectData(currentUser.getUsername(), orgList);

        //将组织机构与面积绑定
        Set<String> projectAndSubSet = new ConcurrentHashSet<>();
        Map<String, List<ManageOverviewAreaVO>> projectAreaMap = new ConcurrentHashMap<>();
        List<ManageOverviewAreaVO> result = filteredOrgList.parallelStream().map(x -> {
            //制作projectAndSubSet为了后面过滤业态与地块
            if (OrgTypeCodeEnum.UC_LAND_PROJECT_SUB.getCode().equals(x.getFdType()) || OrgTypeCodeEnum.UC_LAND_PROJECT.getCode().equals(x.getFdType())) {
                projectAndSubSet.add(x.getFdSid());
            }

            //对象转换，没有汇总的组织，赋予默认对象
            ManageOverviewAreaVO manageOverviewAreaVO = new ManageOverviewAreaVO();
            if (x.getFdSid().equals(directCityId)){
                //直管城市 排最后
                manageOverviewAreaVO.setSortNo(Integer.MAX_VALUE);
            }else {
                manageOverviewAreaVO.setSortNo(x.getFdOrder());
            }
            BoManageOverviewArea boManageOverviewArea = boManageOverviewAreaMap.get(x.getFdSid());
            if (boManageOverviewArea == null) {
                manageOverviewAreaVO.setOrgId(x.getFdSid());
                manageOverviewAreaVO.setOrgName(x.getFdName());
                manageOverviewAreaVO.setOrgType(x.getFdType());
                manageOverviewAreaVO.setParentOrgId(x.getFdPsid());
            } else {
                try {
                    PropertyUtils.copyProperties(manageOverviewAreaVO, boManageOverviewArea);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            if (OrgTypeCodeEnum.UC_LAND_PROJECT.getCode().equals(x.getFdType())) {
                List<ManageOverviewAreaVO> list = projectAreaMap.get(x.getFdPsid());
                if (list == null) {
                    list = Collections.synchronizedList(new ArrayList<>());
                    List<ManageOverviewAreaVO> tmp = projectAreaMap.putIfAbsent(x.getFdPsid(), list);
                    if (tmp != null) {
                        list = tmp;
                    }
                }
                list.add(manageOverviewAreaVO);
            }
            return manageOverviewAreaVO;
        }).collect(Collectors.toList());

        //组织机构汇总面积
        orgSumArea(projectAreaMap, result);

        //加入业态/地块
        areaList.stream().filter(x->OrgTypeCodeEnum.LAND_PART.getCode().equals(x.getOrgType()) && projectAndSubSet.contains(x.getParentOrgId())).forEach(x->projectAndSubSet.add(x.getOrgId()));
        List<ManageOverviewAreaVO> productTypeResult = areaList.parallelStream()
                .filter(x -> ((OrgTypeCodeEnum.PRODUCT_TYPE.getCode().equals(x.getOrgType()) || OrgTypeCodeEnum.LAND_PART.getCode().equals(x.getOrgType()))) && (projectAndSubSet.contains(x.getParentOrgId())))
                .filter(x-> MathUtils.existNotZero(
                        //全盘
                        x.getOverallSaleArea(),
                        //经营决策会
                        x.getManageDecisionSaleArea(),
                        //投决会
                        x.getInvestDecisionSaleArea()
                ))
                .map(x -> {
                    ManageOverviewAreaVO manageOverviewAreaVO = VOUtils.convert2ManageOverviewAreaVO(x);
                    //地块要排在分期后面
                    manageOverviewAreaVO.setSortNo(Integer.MAX_VALUE);
                    return manageOverviewAreaVO;
                })
                .collect(Collectors.toList());
        result.addAll(productTypeResult);

        //面积-面积单位平米 ，改为万平米
        result.parallelStream().forEach(x -> {
            //全盘
            x.setOverallSaleArea(NumberUtil.div(x.getOverallSaleArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //投资决策会
            x.setInvestDecisionSaleArea(NumberUtil.div(x.getInvestDecisionSaleArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            //经营决策会
            x.setManageDecisionSaleArea(NumberUtil.div(x.getManageDecisionSaleArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));

            /**
             * 面积
             */
            x.setUnDevelopArea(NumberUtil.div(x.getUnDevelopArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnStartArea(NumberUtil.div(x.getUnStartArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnReachImgGoalArea(NumberUtil.div(x.getUnReachImgGoalArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnGetCardArea(NumberUtil.div(x.getUnGetCardArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnSaleArea(NumberUtil.div(x.getUnSaleArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setSaledArea(NumberUtil.div(x.getSaledArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalActualAreaAbove(NumberUtil.div(x.getTotalActualAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalActualAreaUnder(NumberUtil.div(x.getTotalActualAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalAreaAbove(NumberUtil.div(x.getTotalAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalAreaUnder(NumberUtil.div(x.getTotalAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalSaleAreaAbove(NumberUtil.div(x.getTotalSaleAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalSaleAreaUnder(NumberUtil.div(x.getTotalSaleAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalRentAreaAbove(NumberUtil.div(x.getTotalRentAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalRentAreaUnder(NumberUtil.div(x.getTotalRentAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalGiveAreaAbove(NumberUtil.div(x.getTotalGiveAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalGiveAreaUnder(NumberUtil.div(x.getTotalGiveAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalUnRentAreaAbove(NumberUtil.div(x.getTotalUnRentAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalUnRentAreaUnder(NumberUtil.div(x.getTotalUnRentAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalUsedArea(NumberUtil.div(x.getTotalUsedArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setBuildingUsedArea(NumberUtil.div(x.getBuildingUsedArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setBuildingCoverdArea(NumberUtil.div(x.getBuildingCoverdArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setBasalArea(NumberUtil.div(x.getBasalArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setCapacityAreaAbove(NumberUtil.div(x.getCapacityAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setCapacityAreaUnder(NumberUtil.div(x.getCapacityAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setCivilDefenceArea(NumberUtil.div(x.getCivilDefenceArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setUnCivilDefenceArea(NumberUtil.div(x.getUnCivilDefenceArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setElesCivilDefenceArea(NumberUtil.div(x.getElesCivilDefenceArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setElesGreenArea(NumberUtil.div(x.getElesGreenArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalExpropriationAreaAbove(NumberUtil.div(x.getTotalExpropriationAreaAbove(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setTotalExpropriationAreaUnder(NumberUtil.div(x.getTotalExpropriationAreaUnder(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setBatchDecorationArea(NumberUtil.div(x.getBatchDecorationArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setBaseApartmentArea(NumberUtil.div(x.getBaseApartmentArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setPublicDecorationArea(NumberUtil.div(x.getPublicDecorationArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setDecorationForActualArea(NumberUtil.div(x.getDecorationForActualArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setSubstituteHardArea(NumberUtil.div(x.getSubstituteHardArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setIntraHardArea(NumberUtil.div(x.getIntraHardArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setSubstituteSoftArea(NumberUtil.div(x.getSubstituteSoftArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));
            x.setIntraSoftArea(NumberUtil.div(x.getIntraSoftArea(), K10_BIGDECIMAL, 2, RoundingMode.HALF_UP));

        });
        //排序
        result = result.parallelStream().sorted((a, b) -> MathUtils.compare(a.getSortNo(), b.getSortNo())).collect(Collectors.toList());
        return JsonResultBuilder.successWithData(result);
    }

    private void commonSumArea(ManageOverviewAreaVO x, List<ManageOverviewAreaVO> areaList) {
        try {
            areaList.parallelStream().forEach(y->{
                synchronized (x) {
                    //汇总 - 全盘货值
                    x.setOverallSaleArea(MathUtils.nullDefaultZero(x.getOverallSaleArea()).add(MathUtils.nullDefaultZero(y.getOverallSaleArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 经营决策会货值
                    x.setManageDecisionSaleArea(MathUtils.nullDefaultZero(x.getManageDecisionSaleArea()).add(MathUtils.nullDefaultZero(y.getManageDecisionSaleArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 投决会
                    x.setInvestDecisionSaleArea(MathUtils.nullDefaultZero(x.getInvestDecisionSaleArea()).add(MathUtils.nullDefaultZero(y.getInvestDecisionSaleArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 未开工 面积
                    x.setUnStartArea(MathUtils.nullDefaultZero(x.getUnStartArea()).add(MathUtils.nullDefaultZero(y.getUnStartArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已开工未达形象进度面积
                    x.setUnReachImgGoalArea(MathUtils.nullDefaultZero(x.getUnReachImgGoalArea()).add(MathUtils.nullDefaultZero(y.getUnReachImgGoalArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已达形象进度未取证面面积
                    x.setUnGetCardArea(MathUtils.nullDefaultZero(x.getUnGetCardArea()).add(MathUtils.nullDefaultZero(y.getUnGetCardArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已取证未售 （存货）面积
                    x.setUnSaleArea(MathUtils.nullDefaultZero(x.getUnSaleArea()).add(MathUtils.nullDefaultZero(y.getUnSaleArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 - 已售面积
                    x.setSaledArea(MathUtils.nullDefaultZero(x.getSaledArea()).add(MathUtils.nullDefaultZero(y.getSaledArea())).setScale(2, RoundingMode.HALF_UP));
                    //汇总 -  未开发面积
                    x.setUnDevelopArea(MathUtils.nullDefaultZero(x.getUnDevelopArea()).add(MathUtils.nullDefaultZero(y.getUnDevelopArea())).setScale(2, RoundingMode.HALF_UP));


                    //汇总 - 地上总实际建筑面积
                    x.setTotalActualAreaAbove(MathUtils.nullDefaultZero(x.getTotalActualAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalActualAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总实际建筑面积
                    x.setTotalActualAreaUnder(MathUtils.nullDefaultZero(x.getTotalActualAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalActualAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上总建筑面积
                    x.setTotalAreaAbove(MathUtils.nullDefaultZero(x.getTotalAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总建筑面积
                    x.setTotalAreaUnder(MathUtils.nullDefaultZero(x.getTotalAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上总可售建筑面积
                    x.setTotalSaleAreaAbove(MathUtils.nullDefaultZero(x.getTotalSaleAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalSaleAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总可售建筑面积
                    x.setTotalSaleAreaUnder(MathUtils.nullDefaultZero(x.getTotalSaleAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalSaleAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上总可租建筑面积
                    x.setTotalRentAreaAbove(MathUtils.nullDefaultZero(x.getTotalRentAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalRentAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总可租建筑面积
                    x.setTotalRentAreaUnder(MathUtils.nullDefaultZero(x.getTotalRentAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalRentAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上总赠送建筑面积
                    x.setTotalGiveAreaAbove(MathUtils.nullDefaultZero(x.getTotalGiveAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalGiveAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总赠送建筑面积
                    x.setTotalGiveAreaUnder(MathUtils.nullDefaultZero(x.getTotalGiveAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalGiveAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上总非可租售建筑面积
                    x.setTotalUnRentAreaAbove(MathUtils.nullDefaultZero(x.getTotalUnRentAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalUnRentAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总非可租售建筑面积
                    x.setTotalUnRentAreaUnder(MathUtils.nullDefaultZero(x.getTotalUnRentAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalUnRentAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 总用地面积
                    x.setTotalUsedArea(MathUtils.nullDefaultZero(x.getTotalUsedArea()).add(MathUtils.nullDefaultZero(y.getTotalUsedArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 建设用地面积
                    x.setBuildingUsedArea(MathUtils.nullDefaultZero(x.getBuildingUsedArea()).add(MathUtils.nullDefaultZero(y.getBuildingUsedArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 建设占地面积
                    x.setBuildingCoverdArea(MathUtils.nullDefaultZero(x.getBuildingCoverdArea()).add(MathUtils.nullDefaultZero(y.getBuildingCoverdArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 基底面积
                    x.setBasalArea(MathUtils.nullDefaultZero(x.getBasalArea()).add(MathUtils.nullDefaultZero(y.getBasalArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上计容建筑面积
                    x.setCapacityAreaAbove(MathUtils.nullDefaultZero(x.getCapacityAreaAbove()).add(MathUtils.nullDefaultZero(y.getCapacityAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下计容建筑面积
                    x.setCapacityAreaUnder(MathUtils.nullDefaultZero(x.getCapacityAreaUnder()).add(MathUtils.nullDefaultZero(y.getCapacityAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 人防面积
                    x.setCivilDefenceArea(MathUtils.nullDefaultZero(x.getCivilDefenceArea()).add(MathUtils.nullDefaultZero(y.getCivilDefenceArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 非人防面积
                    x.setUnCivilDefenceArea(MathUtils.nullDefaultZero(x.getUnCivilDefenceArea()).add(MathUtils.nullDefaultZero(y.getUnCivilDefenceArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 易地人防面积
                    x.setElesCivilDefenceArea(MathUtils.nullDefaultZero(x.getElesCivilDefenceArea()).add(MathUtils.nullDefaultZero(y.getElesCivilDefenceArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 易地绿地面积
                    x.setElesGreenArea(MathUtils.nullDefaultZero(x.getElesGreenArea()).add(MathUtils.nullDefaultZero(y.getElesGreenArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地上总代征用地面积
                    x.setTotalExpropriationAreaAbove(MathUtils.nullDefaultZero(x.getTotalExpropriationAreaAbove()).add(MathUtils.nullDefaultZero(y.getTotalExpropriationAreaAbove())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 地下总代征用地面积
                    x.setTotalExpropriationAreaUnder(MathUtils.nullDefaultZero(x.getTotalExpropriationAreaUnder()).add(MathUtils.nullDefaultZero(y.getTotalExpropriationAreaUnder())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 批量精装修套内面积
                    x.setBatchDecorationArea(MathUtils.nullDefaultZero(x.getBatchDecorationArea()).add(MathUtils.nullDefaultZero(y.getBatchDecorationArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 典型户型精装修套内面积
                    x.setBaseApartmentArea(MathUtils.nullDefaultZero(x.getBaseApartmentArea()).add(MathUtils.nullDefaultZero(y.getBaseApartmentArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 公区精装修面积
                    x.setPublicDecorationArea(MathUtils.nullDefaultZero(x.getPublicDecorationArea()).add(MathUtils.nullDefaultZero(y.getPublicDecorationArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 精装修对应实际建筑面积
                    x.setDecorationForActualArea(MathUtils.nullDefaultZero(x.getDecorationForActualArea()).add(MathUtils.nullDefaultZero(y.getDecorationForActualArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 代征硬景面积
                    x.setSubstituteHardArea(MathUtils.nullDefaultZero(x.getSubstituteHardArea()).add(MathUtils.nullDefaultZero(y.getSubstituteHardArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 区内硬景面积
                    x.setIntraHardArea(MathUtils.nullDefaultZero(x.getIntraHardArea()).add(MathUtils.nullDefaultZero(y.getIntraHardArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 代征软景面积
                    x.setSubstituteSoftArea(MathUtils.nullDefaultZero(x.getSubstituteSoftArea()).add(MathUtils.nullDefaultZero(y.getSubstituteSoftArea())).setScale(2, RoundingMode.HALF_UP));

                    //汇总 - 区内软景面积
                    x.setIntraSoftArea(MathUtils.nullDefaultZero(x.getIntraSoftArea()).add(MathUtils.nullDefaultZero(y.getIntraSoftArea())).setScale(2, RoundingMode.HALF_UP));



                }
            });
        } catch (Exception e) {
            logger.error(e,e.getMessage() + "ManageOverviewQueryVO: {},priceList: {}", x, areaList);
        }
    }

    private void childrenSumArea(Map<String, List<ManageOverviewAreaVO>> areaMap, ManageOverviewAreaVO x, List<ManageOverviewAreaVO> areaList) {
        if (areaList != null) {
            commonSumArea(x, areaList);
            List<ManageOverviewAreaVO> list = areaMap.get(x.getParentOrgId());
            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>());
                List<ManageOverviewAreaVO> tmp = areaMap.putIfAbsent(x.getParentOrgId(), list);
                if (tmp != null) {
                    list = tmp;
                }
            }
            list.add(x);
        }
    }

    private void orgSumArea(Map<String, List<ManageOverviewAreaVO>> projectAreaMap, List<ManageOverviewAreaVO> result) {
        //汇总虚拟地产项目级
        Map<String, List<ManageOverviewAreaVO>> virtualAreaMap = new ConcurrentHashMap<>();
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.UC_LAND_VIRTUAL_DIR.getCode().equals(x.getOrgType())) {
                List<ManageOverviewAreaVO> priceList = projectAreaMap.get(x.getOrgId());
                childrenSumArea(virtualAreaMap, x, priceList);
            }
        });

        //汇总城市
        Map<String, List<ManageOverviewAreaVO>> cityPriceMap = new ConcurrentHashMap<>();
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.CITY.getCode().equals(x.getOrgType())) {
                List<ManageOverviewAreaVO> priceList = virtualAreaMap.get(x.getOrgId());
                childrenSumArea(cityPriceMap, x, priceList);
            }
        });

        //汇总区域
        Map<String, List<ManageOverviewAreaVO>> areaPriceMap = new ConcurrentHashMap<>();
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.AREA.getCode().equals(x.getOrgType())) {
                List<ManageOverviewAreaVO> priceList = cityPriceMap.get(x.getOrgId());
                childrenSumArea(areaPriceMap, x, priceList);
            }
        });

        //汇总板块
        result.parallelStream().forEach(x -> {
            if (OrgTypeCodeEnum.PLATE.getCode().equals(x.getOrgType())) {
                List<ManageOverviewAreaVO> priceList = areaPriceMap.get(x.getOrgId());
                if (priceList != null) {
                    commonSumArea(x, priceList);
                }
            }
        });
    }


    public List<UcOrg> recomPowerProjectData(String userName, List<UcOrg> orgList) {
        List<DataPrivVO> userDataPrivList = boSysPermissionService.getUserDataPrivList(userName);
        List<UcOrg> orgInfoList = new ArrayList<>();
        List<String> sidList = new ArrayList<String>();
        String dataRangeName = "";
        for (DataPrivVO dataPrivVO : userDataPrivList) {
            if (dataPrivVO.getFdCode().equals("bo_mnu_1")) {
                List<DataPrivDetailVO> dataPrivList = dataPrivVO.getFdDataPrivList();
                if (DataUtils.isNotEmpty(dataPrivList)) {
                    for (DataPrivDetailVO dataPrivDetailVO : dataPrivList) {
                        if (dataPrivDetailVO.getFdDataRangeName().equals("全集团")) {
                            dataRangeName = dataPrivDetailVO.getFdDataRangeName();
                        } else {
                            sidList.add(dataPrivDetailVO.getFdOrgSid());
                        }
                    }
                }
            }
        }

        if (dataRangeName.equals("全集团")) {
            for (UcOrg ucOrg : orgList) {
                orgInfoList.add(ucOrg);
            }
        } else if (StrUtils.isEmpty(dataRangeName) && DataUtils.isNotEmpty(sidList)) {
            for (String sidStr : sidList) {
                String[] split = new String[1];
                for (UcOrg ucOrg : orgList) {
                    if (ucOrg.getFdSidTree().indexOf(sidStr) >= 0) {
                        orgInfoList.add(ucOrg);
                    }
                }
                if (split != null) {
                    for (UcOrg ucOrg : orgList) {
                        for (int i = 1; i < split.length - 1; i++) {
                            if (ucOrg.getFdSid().equals(split[i])) {
                                orgInfoList.add(ucOrg);
                            }
                        }
                    }
                }
            }
        }
        return orgInfoList;
    }
}
