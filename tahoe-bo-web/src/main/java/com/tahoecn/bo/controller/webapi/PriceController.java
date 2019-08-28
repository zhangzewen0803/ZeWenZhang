package com.tahoecn.bo.controller.webapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.*;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.model.vo.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.security.SecureUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 价格管理API
 *
 * @author panglx
 */
@Api(tags = "价格管理API", value = "价格管理API")
@RestController
@RequestMapping(value = "/api/price")
public class PriceController extends TahoeBaseController {

    private static final Log logger = LogFactory.get();

    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoProjectPriceExtendService boProjectPriceExtendService;

    @Autowired
    private BoProjectQuotaExtendService boProjectQuotaExtendService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoProjectPriceQuotaMapService boProjectPriceQuotaMapService;

    @Autowired
    private MdmProjectInfoService mdmProjectInfoService;

    @Autowired
    private BoApproveRecordService boApproveRecordService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UcUserService userService;

    @ApiOperation(value = "进入审批前校验", httpMethod = "POST", notes = "进入审批前校验")
    @RequestMapping(value = "/validateBeforApproval", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, paramType = "query"),
    })
    public JSONResult updateVersion(@NotBlank(message = "versionId不能为空") String versionId) {
        BoProjectPriceExtend projectPriceExtend = boProjectPriceExtendService.getById(versionId);
        if (projectPriceExtend == null){
            return JsonResultBuilder.failed("版本不存在");
        }
        //无processId，且状态为【审批通过】，不允许进入审批
        if (StringUtils.isBlank(projectPriceExtend.getApproveId()) && VersionStatusEnum.PASSED.getKey() == projectPriceExtend.getVersionStatus()){
            return JsonResultBuilder.create(CodeEnum.APPROVAL_EXTENDS);
        }
        //有processId，且申请人不是当前用户，不允许进入审批
        if (StringUtils.isNotBlank(projectPriceExtend.getApproveId())){
            QueryWrapper<BoApproveRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("approve_id",projectPriceExtend.getApproveId())
                    .eq("is_delete",IsDeleteEnum.NO.getKey())
                    .eq("is_disable",IsDisableEnum.NO.getKey())
                    .orderByDesc("create_time").last("limit 1");
            BoApproveRecord approveRecord = boApproveRecordService.getOne(queryWrapper);
            if (approveRecord == null){
                return JsonResultBuilder.failed("审批记录不存在");
            }

            CurrentUserVO currentUser = getCurrentUser();
            if (!currentUser.getId().equals(approveRecord.getCreaterId())){
                QueryWrapper<UcUser> queryUserWrapper = new QueryWrapper<UcUser>();
                queryUserWrapper.eq("fd_sid", approveRecord.getCreaterId());
                UcUser user = userService.getOne(queryUserWrapper);

                return JsonResultBuilder.create(CodeEnum.APPROVAL_PROCESS_USE_EXTENDS.getKey(), CodeEnum.APPROVAL_PROCESS_USE_EXTENDS.getValue().replace("**", user==null?"":user.getFdName()));
            }
        }
        return JsonResultBuilder.success();
    }


    @ApiOperation(value = "*设置版本状态，临时使用", httpMethod = "POST", notes = "*设置版本状态，临时使用")
    @RequestMapping(value = "/updateVersion", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "versionStatus", value = "状态，可为空，默认审批通过。0-编制中，2-审批通过", required = false, paramType = "query"),
    })
    public JSONResult updateVersion(@NotBlank(message = "versionId不能为空") String versionId, Integer versionStatus) {
        BoProjectPriceExtend t = new BoProjectPriceExtend();
        t.setId(versionId);
        t.setVersionStatus(versionStatus == null ? VersionStatusEnum.PASSED.getKey() : versionStatus);
        CurrentUserVO currentUser = getCurrentUser();
        t.setUpdaterId(currentUser.getId());
        t.setUpdaterName(currentUser.getName());
        t.setUpdateTime(LocalDateTime.now());
        boProjectPriceExtendService.updateById(t);
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "查询所有价格版本信息", httpMethod = "POST", notes = "查询所有价格版本信息")
    @RequestMapping(value = "/getPriceVersionList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "投决会阶段传项目ID，其他阶段传分期ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stageCode", value = "阶段CODE", required = true, paramType = "query")
    })
    public JSONResult<List<VersionVO>> getAreaVersionList(@NotBlank(message = "projectId不能为空") String projectId, @NotBlank(message = "stageCode不能为空") String stageCode) {
        QueryWrapper<BoProjectPriceExtend> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("create_time");
        List<BoProjectPriceExtend> list = boProjectPriceExtendService.list(wrapper);
        List<VersionVO> respList = new ArrayList<>();
        if (!list.isEmpty()) {
            list.forEach(x -> {
                VersionVO versionVO = new VersionVO();
                versionVO.setVersionId(x.getId());
                versionVO.setVersionName(x.getVersion());
                versionVO.setVersionStatus(x.getVersionStatus());
                versionVO.setVersionStatusDesc(VersionStatusEnum.getValueByKey(x.getVersionStatus()));
                versionVO.setVersionProcessId(StringUtils.isBlank(x.getApproveId())?"":x.getApproveId());
                versionVO.setVersionApprovaledDate((VersionStatusEnum.PASSED.getKey() == x.getVersionStatus() ? (x.getApproveEndTime() != null ? " " + x.getApproveEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE) : "") : ""));
                respList.add(versionVO);
            });
        }
        return JsonResultBuilder.successWithData(respList);
    }

    @ApiOperation(value = "查询价格阶段列表与阶段状态", httpMethod = "POST", notes = "查询价格阶段列表与阶段状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "投决会阶段传项目ID，其他阶段传分期ID", required = true, paramType = "query"),
    })
    @RequestMapping(value = "/getStageStatusList", method = {RequestMethod.POST})
    public JSONResult<List<StageStatusVO>> getStageStatusList(@NotBlank(message = "projectId不能为空") String projectId) {
        //制作阶段VO
        List<StageStatusVO> stageStatusList = Arrays.stream(StageCodeEnum.values()).map(x -> {
            StageStatusVO stageStatusVO = new StageStatusVO();
            stageStatusVO.setStageCode(x.getKey());
            stageStatusVO.setStageName(x.getValue());
            stageStatusVO.setSortNo(x.getOrder());
            //默认未编制
            stageStatusVO.setStageStatus(VersionStatusEnum.NONE.getKey());
            stageStatusVO.setStageStatusDesc(VersionStatusEnum.NONE.getValue());
            return stageStatusVO;
        }).sorted(Comparator.comparing(x -> x.getSortNo())).collect(Collectors.toList());

        //填充阶段状态数据
        QueryWrapper<BoProjectPriceExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("version_status", "max(create_time) create_time", "stage_code")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .eq("project_id", projectId)
                .groupBy("stage_code")
                .orderByAsc("stage_code");
        List<BoProjectPriceExtend> list = boProjectPriceExtendService.list(queryWrapper);
        if (!list.isEmpty()) {
            stageStatusList.stream().forEach(x -> {
                list.stream().forEach(y -> {
                    if (x.getStageCode().equals(y.getStageCode())) {
                        x.setStageStatus(y.getVersionStatus());
                        x.setStageStatusDesc(VersionStatusEnum.getValueByKey(y.getVersionStatus()));
                    }
                });
            });
        }
        return JsonResultBuilder.successWithData(stageStatusList);
    }

    @ApiOperation(value = "创建新版本价格", notes = "创建新版本价格，结果data返回新的版本ID")
    @RequestMapping(value = "/createPriceVersion", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目或分期ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stageCode", value = "阶段CODE", required = true, paramType = "query"),
    })
    public JSONResult<String> createPriceVersion(@NotBlank(message = "projectId不能为空") String projectId, @NotBlank(message = "stageCode不能为空") String stageCode) {
        StageCodeEnum stageCodeEnum = StageCodeEnum.getByKey(stageCode);
        if (stageCodeEnum == null) {
            return JsonResultBuilder.failed("stageCode不存在，请确认是否输入正确");
        }
        String projectName = StageCodeEnum.STAGE_01 == stageCodeEnum ? "项目" : "分期";
        MdmProjectInfo projectInfo = mdmProjectInfoService.getById(projectId);
        if (projectInfo == null) {
            return JsonResultBuilder.failed(projectName + "不存在");
        }
        if (LevelTypeEnum.PROJECT.getKey().equals(projectInfo.getLevelType()) && stageCodeEnum.getOrder() != 1) {
            return JsonResultBuilder.failed("项目不存在该阶段：" + stageCode);
        }
        if (LevelTypeEnum.PROJECT_SUB.getKey().equals(projectInfo.getLevelType()) && stageCodeEnum.getOrder() == 1) {
            return JsonResultBuilder.failed("分期不存在该阶段：" + stageCode);
        }
        //判断当前项目/分期，当前阶段下面积版本是否审批通过
        BoProjectQuotaExtend lastProjectQuotaVersion = boProjectQuotaExtendService.getLastVersion(projectId, stageCode);
        if (lastProjectQuotaVersion == null || VersionStatusEnum.PASSED.getKey() != lastProjectQuotaVersion.getVersionStatus()) {
            return JsonResultBuilder.failed("创建失败，当前阶段下的面积版本尚未审批通过。");
        }
        String key = RedisConstants.PRICE_CREATE_VERSION_LOCK + projectId;
        //禁止重复创建
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }
        try {
            //判断当前项目/分期，当前阶段，是否存在编制中、审批中、审批驳回的数据
            if (boProjectPriceExtendService.hasCanEditData(projectId, stageCode)) {
                return JsonResultBuilder.failed("创建失败，当前阶段下存在编制中、审批中或已驳回的数据，禁止创建新版本。");
            }
            BoProjectPriceExtend newVersion = boProjectPriceExtendService.createVersion(projectId, lastProjectQuotaVersion.getId(), stageCode, getCurrentUser());
            return JsonResultBuilder.successWithData(newVersion.getId());
        } finally {
            redisTemplate.delete(key);
        }
    }

    @ApiOperation(value = "查询地块汇总面积以及价格信息", notes = "查询地块汇总面积以及价格信息")
    @RequestMapping(value = "/getLandPartProductTypeQuotaPrice", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "价格版本ID", required = true, paramType = "query"),
    })
    public JSONResult<GetLandPartProductTypeQuotaPriceVO> getLandPartProductTypeQuotaPrice(@NotBlank(message = "versionId不能为空") String versionId) {
        GetLandPartProductTypeQuotaPriceVO getLandPartProductTypeQuotaPriceVO = new GetLandPartProductTypeQuotaPriceVO();
        //head
        List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey(), QuotaGroupCodeEnum.PROJECT_PRICE.getKey());
        Map<String, BoQuotaGroupMap> queryQuotaMap = new HashMap<>();
        quotaGroupMapList.stream().forEach(x -> queryQuotaMap.put(x.getCode(), x));
        List<QuotaHeadVO> headVOList = VOUtils.makeQuotaHeadVOList(quotaGroupMapList);
        getLandPartProductTypeQuotaPriceVO.setHead(headVOList);

        //汇总地块业态面积信息 以及 价格信息
        List<BoLandPartProductTypeMap> landPartProductTypeList = boLandPartProductTypeMapService.getLandPartProductTypeListByPriceVersionId(versionId);
        if (!landPartProductTypeList.isEmpty()) {
            List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaListByPriceVersionId(versionId);
            List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(versionId);

            //地块业态进行分组
            Map<String, LandPartProductTypePriceVO> queryLandPartProductTypeMap = new HashMap<>();
            Set<String> queryQuotaSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.TOTAL_CAN_SALE_AREA.getKey(), QuotaCodeEnum.SIMPLE_CAR_PARKING_PLATE_COUNT.getKey()).collect(Collectors.toSet());
            landPartProductTypeList.stream().forEach(x -> {
                String key = x.getLandPartId() + x.getProductTypeCode();
                if (!queryLandPartProductTypeMap.containsKey(key)) {
                    //info
                    LandPartProductTypePriceVO landPartProductTypePriceVO = new LandPartProductTypePriceVO();
                    try {
                        PropertyUtils.copyProperties(landPartProductTypePriceVO, x);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    landPartProductTypePriceVO.setId(SecureUtil.md5(key));
                    queryLandPartProductTypeMap.put(key, landPartProductTypePriceVO);

                    //quota list
                    List<QuotaBodyVO> quotaList = landPartProductTypeQuotaList.stream().filter(y -> x.getId().equals(y.getLandPartProductTypeId()) && queryQuotaSet.contains(y.getQuotaCode())).map(z -> {
                        QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                        quotaBodyVO.setQuotaCode(z.getQuotaCode());
                        quotaBodyVO.setQuotaValue(z.getQuotaValue());
                        BusinessObjectUtils.quotaValueSetDefaultForObject(queryQuotaMap.get(z.getQuotaCode()).getValueType(), quotaBodyVO);
                        return quotaBodyVO;
                    }).collect(Collectors.toList());
                    landPartProductTypePriceVO.setQuotaList(quotaList);

                    //price quota list
                    List<QuotaBodyVO> priceQuotaList = projectPriceQuotaList.stream().filter(y -> SecureUtil.md5(key).equals(y.getRefId())).map(z -> makeQuotaBody(z)).collect(Collectors.toList());
                    landPartProductTypePriceVO.setPriceQuotaList(priceQuotaList);
                    return;
                }

                //quota value add+
                List<QuotaBodyVO> priceQuotaList = queryLandPartProductTypeMap.get(key).getQuotaList();
                landPartProductTypeQuotaList.stream().forEach(y -> {
                    if (x.getId().equals(y.getLandPartProductTypeId()) && queryQuotaSet.contains(y.getQuotaCode())) {
                        priceQuotaList.stream().forEach(z -> {
                            if (y.getQuotaCode().equals(z.getQuotaCode())) {
                                z.setQuotaValue(MathUtils.bigDecimalAdd(z.getQuotaValue(), y.getQuotaValue()).toString());
                            }
                        });
                    }
                });
            });

            //总可售汇总
            List<LandPartProductTypePriceVO> landPartProductTypePriceList = new ArrayList<>();
            queryLandPartProductTypeMap.forEach((k, v) -> {
                landPartProductTypePriceList.add(v);
                List<QuotaBodyVO> quotaList = v.getQuotaList();
                quotaList.stream().forEach(x -> {
                    if (QuotaCodeEnum.TOTAL_CAN_SALE_AREA.getKey().equals(x.getQuotaCode())) {
                        x.setQuotaValue("0");
                        quotaList.stream().forEach(y -> {
                            if (QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey().equals(y.getQuotaCode()) || QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey().equals(y.getQuotaCode())) {
                                x.setQuotaValue(MathUtils.bigDecimalAdd(x.getQuotaValue(), y.getQuotaValue()).toString());
                            }
                        });
                    }
                });
            });


            //若某些业态无价格信息，则新增（产生这种情况的原因是，在初始录入数据阶段，已经生成价格版本后仍然对面积进行了更改）
            List<BoProjectPriceQuotaMap> boProjectPriceQuotaMapList = new ArrayList<>();
            CurrentUserVO currentUser = getCurrentUser();
            List<BoQuotaGroupMap> priceGroupCodeMap = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_PRICE.getKey());
            landPartProductTypePriceList.stream().filter(x -> CollectionUtils.isEmpty(x.getPriceQuotaList()))
                    .forEach(x -> {
                        priceGroupCodeMap.stream().forEach(y -> {
                            BoProjectPriceQuotaMap boProjectPriceQuotaMap = new BoProjectPriceQuotaMap();
                            boProjectPriceQuotaMap.setQuotaGroupMapId(y.getId());
                            boProjectPriceQuotaMap.setQuotaCode(y.getCode());
                            boProjectPriceQuotaMap.setProjectPriceExtendId(versionId);
                            boProjectPriceQuotaMap.setCreaterId(currentUser.getId());
                            boProjectPriceQuotaMap.setId(UUIDUtils.create());
                            boProjectPriceQuotaMap.setCreaterName(currentUser.getName());
                            boProjectPriceQuotaMap.setCreateTime(LocalDateTime.now());
                            boProjectPriceQuotaMap.setQuotaId(y.getQuotaId());
                            BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boProjectPriceQuotaMap);
                            boProjectPriceQuotaMap.setRefId(SecureUtil.md5(x.getLandPartId() + x.getProductTypeCode()));
                            boProjectPriceQuotaMapList.add(boProjectPriceQuotaMap);
                            if (x.getPriceQuotaList() == null) {
                                x.setPriceQuotaList(new ArrayList<>());
                            }
                            QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                            quotaBodyVO.setQuotaValue(boProjectPriceQuotaMap.getQuotaValue());
                            quotaBodyVO.setQuotaCode(boProjectPriceQuotaMap.getQuotaCode());
                            x.getPriceQuotaList().add(quotaBodyVO);
                        });
                    });
            if (!boProjectPriceQuotaMapList.isEmpty()) {
                boProjectPriceQuotaMapService.saveBatch(boProjectPriceQuotaMapList, 100);
            }

            List<LandPartProductTypePriceVO> finalList = landPartProductTypePriceList.stream().filter(x -> {
                List<QuotaBodyVO> quotaList = x.getQuotaList();
                for (QuotaBodyVO quotaBodyVO : quotaList) {
                    if (QuotaCodeEnum.TOTAL_CAN_SALE_AREA.getKey().equals(quotaBodyVO.getQuotaCode())) {
                        return new BigDecimal(quotaBodyVO.getQuotaValue()).doubleValue() > 0;
                    }
                }
                return false;
            }).collect(Collectors.toList());

            getLandPartProductTypeQuotaPriceVO.setLandPartProductTypePriceList(finalList);
        } else {
            getLandPartProductTypeQuotaPriceVO.setLandPartProductTypePriceList(new ArrayList<>(0));
        }
        return JsonResultBuilder.successWithData(getLandPartProductTypeQuotaPriceVO);
    }

    @ApiOperation(value = "查询楼栋汇总面积以及价格信息", notes = "查询楼栋汇总面积以及价格信息")
    @RequestMapping(value = "/getBuildingProductTypeQuotaPrice", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "价格版本ID", required = true, paramType = "query"),
    })
    public JSONResult<GetBuildingProductTypeQuotaPriceVO> getBuildingProductTypeQuotaPrice(@NotBlank(message = "versionId不能为空") String versionId) {
        GetBuildingProductTypeQuotaPriceVO getBuildingProductTypeQuotaPriceVO = new GetBuildingProductTypeQuotaPriceVO();

        //head
        List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.BUILDING_PRODUCT_TYPE.getKey(), QuotaGroupCodeEnum.PROJECT_PRICE.getKey());
        Map<String, BoQuotaGroupMap> queryQuotaMap = new HashMap<>();
        quotaGroupMapList.stream().forEach(x -> queryQuotaMap.put(x.getCode(), x));
        List<QuotaHeadVO> headVOList = VOUtils.makeQuotaHeadVOList(quotaGroupMapList);
        getBuildingProductTypeQuotaPriceVO.setHead(headVOList);


        //汇总楼栋业态面积信息 以及 价格信息
        List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeListByPriceVersionId(versionId);
        List<String> landPartProductTypeIdList = buildingProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toList());
        List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(landPartProductTypeIdList);
        List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(versionId);

        //地块业态进行分组
        Map<String, BuildingProductTypePriceVO> queryProductTypeMap = new HashMap<>();
        Set<String> queryQuotaSet = Stream.of(QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey(), QuotaCodeEnum.TOTAL_CAN_SALE_AREA.getKey(),QuotaCodeEnum.SIMPLE_CAR_PARKING_PLATE_COUNT.getKey()).collect(Collectors.toSet());
        buildingProductTypeList.stream().forEach(x -> {
            if (!queryProductTypeMap.containsKey(x.getProductTypeCode())) {
                //info
                BuildingProductTypePriceVO buildingProductTypePriceVO = new BuildingProductTypePriceVO();
                try {
                    PropertyUtils.copyProperties(buildingProductTypePriceVO, x);
                } catch (Exception e) {
                    logger.error(e);
                }
                buildingProductTypePriceVO.setId(SecureUtil.md5(x.getProductTypeCode()));
                queryProductTypeMap.put(x.getProductTypeCode(), buildingProductTypePriceVO);

                //quota list
                List<QuotaBodyVO> quotaList = buildingProductTypeQuotaList.stream().filter(y -> x.getId().equals(y.getBuildingProductTypeId()) && queryQuotaSet.contains(y.getQuotaCode())).map(z -> {
                    QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                    quotaBodyVO.setQuotaCode(z.getQuotaCode());
                    quotaBodyVO.setQuotaValue(z.getQuotaValue());
                    BusinessObjectUtils.quotaValueSetDefaultForObject(queryQuotaMap.get(z.getQuotaCode()).getValueType(), quotaBodyVO);
                    return quotaBodyVO;
                }).collect(Collectors.toList());
                buildingProductTypePriceVO.setQuotaList(quotaList);

                //price quota list
                List<QuotaBodyVO> priceQuotaList = projectPriceQuotaList.stream().filter(y -> SecureUtil.md5(x.getProductTypeCode()).equals(y.getRefId())).map(z -> makeQuotaBody(z)).collect(Collectors.toList());
                buildingProductTypePriceVO.setPriceQuotaList(priceQuotaList);
                return;
            }

            List<QuotaBodyVO> quotaList = queryProductTypeMap.get(x.getProductTypeCode()).getQuotaList();
            //quota value add+
            buildingProductTypeQuotaList.stream().forEach(y -> {
                if (x.getId().equals(y.getBuildingProductTypeId()) && queryQuotaSet.contains(y.getQuotaCode())) {
                    quotaList.stream().forEach(z -> {
                        if (y.getQuotaCode().equals(z.getQuotaCode())) {
                            z.setQuotaValue(MathUtils.bigDecimalAdd(z.getQuotaValue(), y.getQuotaValue()).toString());
                        }
                    });
                }
            });

        });

        //总可售汇总
        List<BuildingProductTypePriceVO> buildingProductTypePriveList = new ArrayList<>();
        queryProductTypeMap.forEach((k, v) -> {
            buildingProductTypePriveList.add(v);
            List<QuotaBodyVO> quotaList = v.getQuotaList();
            quotaList.stream().forEach(x -> {
                if (QuotaCodeEnum.TOTAL_CAN_SALE_AREA.getKey().equals(x.getQuotaCode())) {
                    x.setQuotaValue("0");
                    quotaList.stream().forEach(y -> {
                        if (QuotaCodeEnum.ABOVE_GROUND_CAN_SALE_AREA.getKey().equals(y.getQuotaCode()) || QuotaCodeEnum.UNDER_GROUND_CAN_SALE_AREA.getKey().equals(y.getQuotaCode())) {
                            x.setQuotaValue(MathUtils.bigDecimalAdd(x.getQuotaValue(), y.getQuotaValue()).toString());
                        }
                    });
                }
            });
        });

        //若某些业态无价格信息，则新增（产生这种情况的原因是，在初始录入数据阶段，已经生成价格版本后仍然对面积进行了更改）
        List<BoProjectPriceQuotaMap> boProjectPriceQuotaMapList = new ArrayList<>();
        CurrentUserVO currentUser = getCurrentUser();
        List<BoQuotaGroupMap> priceGroupCodeMap = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.PROJECT_PRICE.getKey());
        buildingProductTypePriveList.stream().filter(x -> CollectionUtils.isEmpty(x.getPriceQuotaList()))
                .forEach(x -> {
                    priceGroupCodeMap.stream().forEach(y -> {
                        BoProjectPriceQuotaMap boProjectPriceQuotaMap = new BoProjectPriceQuotaMap();
                        boProjectPriceQuotaMap.setQuotaGroupMapId(y.getId());
                        boProjectPriceQuotaMap.setQuotaCode(y.getCode());
                        boProjectPriceQuotaMap.setProjectPriceExtendId(versionId);
                        boProjectPriceQuotaMap.setCreaterId(currentUser.getId());
                        boProjectPriceQuotaMap.setId(UUIDUtils.create());
                        boProjectPriceQuotaMap.setCreaterName(currentUser.getName());
                        boProjectPriceQuotaMap.setCreateTime(LocalDateTime.now());
                        boProjectPriceQuotaMap.setQuotaId(y.getQuotaId());
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), boProjectPriceQuotaMap);
                        boProjectPriceQuotaMap.setRefId(SecureUtil.md5(x.getProductTypeCode()));
                        boProjectPriceQuotaMapList.add(boProjectPriceQuotaMap);
                        if (x.getPriceQuotaList() == null) {
                            x.setPriceQuotaList(new ArrayList<>());
                        }
                        QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                        quotaBodyVO.setQuotaValue(boProjectPriceQuotaMap.getQuotaValue());
                        quotaBodyVO.setQuotaCode(boProjectPriceQuotaMap.getQuotaCode());
                        x.getPriceQuotaList().add(quotaBodyVO);
                    });
                });
        if (!boProjectPriceQuotaMapList.isEmpty()) {
            boProjectPriceQuotaMapService.saveBatch(boProjectPriceQuotaMapList, 100);
        }

        List<BuildingProductTypePriceVO> finalList = buildingProductTypePriveList.stream().filter(x -> {
            List<QuotaBodyVO> quotaList = x.getQuotaList();
            for (QuotaBodyVO quotaBodyVO : quotaList) {
                if (QuotaCodeEnum.TOTAL_CAN_SALE_AREA.getKey().equals(quotaBodyVO.getQuotaCode())) {
                    return new BigDecimal(quotaBodyVO.getQuotaValue()).doubleValue() > 0;
                }
            }
            return false;
        }).collect(Collectors.toList());
        getBuildingProductTypeQuotaPriceVO.setBuildingProductTypePriceList(finalList);
        return JsonResultBuilder.successWithData(getBuildingProductTypeQuotaPriceVO);
    }

    private QuotaBodyVO makeQuotaBody(BoProjectPriceQuotaMap z) {
        QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
        quotaBodyVO.setQuotaCode(z.getQuotaCode());
        quotaBodyVO.setQuotaValue(z.getQuotaValue());
        return quotaBodyVO;
    }

    @ApiOperation(value = "更新业态价格", notes = "更新业态价格")
    @RequestMapping(value = "/updateProductTypePrice", method = {RequestMethod.POST})
    public JSONResult updateProductTypePrice(@Validated @RequestBody UpdateProductTypePriceReqBody param) {
        String versionId = param.getVersionId();
        BoProjectPriceExtend boProjectPriceExtend = boProjectPriceExtendService.getById(versionId);
        if (boProjectPriceExtend == null) {
            return JsonResultBuilder.failed("版本不存在");
        }
        VersionStatusEnum versionStatusEnum = VersionStatusEnum.getByKey(boProjectPriceExtend.getVersionStatus());
        if (versionStatusEnum != VersionStatusEnum.CREATING && versionStatusEnum != VersionStatusEnum.REJECTED) {
            return JsonResultBuilder.failed("当前版本禁止编辑");
        }
        List<BoProjectPriceQuotaMap> projectPriceQuotaList = boProjectPriceQuotaMapService.getProjectPriceQuotaList(versionId);
        if (!projectPriceQuotaList.isEmpty()) {
            CurrentUserVO currentUser = getCurrentUser();
            List<UpdateProductTypePriceReqBody.Price> priceList = param.getPriceList();
            projectPriceQuotaList.stream().forEach(x -> {
                priceList.stream().forEach(y -> {
                    if (x.getRefId().equals(y.getRefId())) {
                        List<QuotaBodyVO> quotaList = y.getQuotaList();
                        quotaList.stream().forEach(z -> {
                            if (x.getQuotaCode().equals(z.getQuotaCode())) {
                                x.setQuotaValue(z.getQuotaValue());
                                x.setUpdaterId(currentUser.getId());
                                x.setUpdaterName(currentUser.getName());
                                x.setUpdateTime(LocalDateTime.now());
                            }
                        });
                    }
                });
                ;
            });
            boProjectPriceQuotaMapService.updateBatchById(projectPriceQuotaList, 100);
        }
        return JsonResultBuilder.success();
    }


}
