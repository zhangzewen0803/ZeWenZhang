package com.tahoecn.bo.controller.webapi;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.BusinessObjectUtils;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.dto.BoPushProjectSubDto;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.collection.ConcurrentHashSet;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.uc.UcClient;
import com.tahoecn.uc.exception.UcException;
import com.tahoecn.uc.request.UcV1OrgListRequest;
import com.tahoecn.uc.response.UcV1OrgListResponse;
import com.tahoecn.uc.vo.UcV1OrgListResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author panglx
 */
@Api(tags = "*管理员使用的特殊管理接口", value = "*管理员使用的特殊管理接口")
@RestController
@RequestMapping(value = "/api/admin")
public class AdminController extends TahoeBaseController {


    private static final Log logger = LogFactory.get();

    public static final String PASSWORD = "Th123456";

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private MdmProjectInfoService mdmProjectInfoService;

    @Autowired
    private BoProjectQuotaMapService boProjectQuotaMapService;

    @Autowired
    private BoProjectQuotaExtendService boProjectQuotaExtendService;

    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private BoProjectLandPartProductTypeMapService boProjectLandPartProductTypeMapService;

    @Autowired
    private BoProjectLandPartProductTypeQuotaMapService boProjectLandPartProductTypeQuotaMapService;

    @Autowired
    private BoBuildingService boBuildingService;

    @Autowired
    private BoBuildingQuotaMapService boBuildingQuotaMapService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoProjectPriceExtendService boProjectPriceExtendService;

    @Autowired
    private BoProjectPriceQuotaMapService boProjectPriceQuotaMapService;

    @Autowired
    private BoManageOverviewPriceService boManageOverviewPriceService;

    @Autowired
    private BoManageOverviewAreaService boManageOverviewAreaService;

    @Autowired
    private SalePushBuildingDataService salePushBuildingDataService;


    @ApiOperation(value = "临时同步主数据项目分期表中的所属城市与所属区域", httpMethod = "POST", notes = "临时同步主数据项目分期表中的所属城市与所属区域")
    @ResponseBody
    @RequestMapping(value = "/mdm/make")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
    })
    public JSONResult mdmMake(String pwd) {
        if (PASSWORD.equals(pwd)) {
            List<MdmProjectInfo> list = mdmProjectInfoService.list();
            UcV1OrgListRequest request = new UcV1OrgListRequest();
            request.setOrgType("ORG3,ORG4,ORG4-1,ORG5-1,ORG5-2");
            request.setReturnType(2);
            try {
                UcV1OrgListResponse ucV1OrgListResponse = UcClient.v1OrgList(request);
                Map<String, UcV1OrgListResultVO> ucOrgMap = new ConcurrentHashMap<>();
                List<UcV1OrgListResultVO> result = ucV1OrgListResponse.getResult();
                result.parallelStream().forEach(x -> ucOrgMap.put(x.getFdSid(), x));
                List<MdmProjectInfo> projectList = Collections.synchronizedList(new ArrayList<>());
                list.parallelStream().forEach(x -> {
                    if (LevelTypeEnum.PROJECT.getKey().equals(x.getLevelType())) {
                        //虚拟目录
                        UcV1OrgListResultVO ucOrg = ucOrgMap.get(x.getParentSid());
                        if (ucOrg != null) {
                            //城市公司
                            UcV1OrgListResultVO city = ucOrgMap.get(ucOrg.getFdPsid());
                            if (city != null) {
                                MdmProjectInfo mdmProjectInfo = new MdmProjectInfo();
                                mdmProjectInfo.setSid(x.getSid());
                                mdmProjectInfo.setCityCompanyId(city.getFdSid());
                                mdmProjectInfo.setCityCompanyName(city.getFdName());
                                UcV1OrgListResultVO region = ucOrgMap.get(city.getFdPsid());
                                if (region != null) {
                                    mdmProjectInfo.setRegionId(region.getFdSid());
                                    mdmProjectInfo.setRegionName(region.getFdName());
                                }
                                projectList.add(mdmProjectInfo);
                            }
                        }
                    }
                });

                mdmProjectInfoService.updateBatchById(projectList, 100);
            } catch (UcException e) {
                logger.error(e);
                return JsonResultBuilder.failed(e.getMessage());
            }
            return JsonResultBuilder.success();
        }
        return JsonResultBuilder.failed("pwd need");
    }

    @ApiOperation(value = "为所有项目、分期、投决会阶段面积生成一版初始数据（initHostory）", httpMethod = "POST", notes = "为所有项目、分期、投决会阶段面积生成一版初始数据")
    @ResponseBody
    @RequestMapping(value = "/history/init")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
    })
    public JSONResult initHistory(String pwd) {
        if (PASSWORD.equals(pwd)) {
            mdmProjectInfoService.makeHistoryInit(getCurrentUser());
        }
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "为指定项目初始化一版数据(initHistory)", httpMethod = "POST", notes = "为所有项目、分期、投决会阶段面积生成一版初始数据")
    @ResponseBody
    @RequestMapping(value = "/history/initProject")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "projectIds", value = "项目ID,多个逗号隔开", required = true, paramType = "query"),
            @ApiImplicitParam(name = "landPartStartNo", value = "地块起始号（会用于生成地块id、名称、code，勿重复！）", required = true, paramType = "query"),
    })
    public JSONResult initProject(@NotBlank String pwd,@NotBlank String projectIds,@NotNull Integer landPartStartNo) {
        if (PASSWORD.equals(pwd)) {
            mdmProjectInfoService.makeHistoryInit(getCurrentUser(),projectIds,landPartStartNo);
        }
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "删除面积、价格版本信息", httpMethod = "POST", notes = "删除面积、价格版本信息")
    @ResponseBody
    @RequestMapping(value = "/remove")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "0-面积版本，1-价格版本", required = true, paramType = "query"),
            @ApiImplicitParam(name = "id", value = "版本ID", required = true, paramType = "query"),
    })
    public JSONResult remove(@NotBlank String pwd, @NotNull Integer type, @NotBlank String id) {
        if (PASSWORD.equals(pwd)) {
            switch (type) {
                case 0:
                    //删除面积
                    BoProjectQuotaExtend boProjectQuotaExtend = new BoProjectQuotaExtend();
                    boProjectQuotaExtend.setId(id);
                    boProjectQuotaExtend.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectQuotaExtend.setUpdateTime(LocalDateTime.now());
                    boProjectQuotaExtendService.updateById(boProjectQuotaExtend);

                    //删除楼栋
                    BoBuilding boBuilding = new BoBuilding();
                    boBuilding.setIsDelete(IsDeleteEnum.YES.getKey());
                    boBuilding.setUpdateTime(LocalDateTime.now());
                    UpdateWrapper<BoBuilding> buildingUpdateWrapper = new UpdateWrapper<>();
                    buildingUpdateWrapper
                            .eq("project_quota_extend_id", id);
                    boBuildingService.update(boBuilding, buildingUpdateWrapper);

                    //删除楼栋指标
                    BoBuildingQuotaMap boBuildingQuotaMap = new BoBuildingQuotaMap();
                    boBuildingQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boBuildingQuotaMap.setUpdateTime(LocalDateTime.now());
                    UpdateWrapper<BoBuildingQuotaMap> boBuildingQuotaMapUpdateWrapper = new UpdateWrapper<>();
                    boBuildingQuotaMapUpdateWrapper.inSql("building_id", "select id from bo_building where project_quota_extend_id='" + id.replace(",", "") + "'");
                    boBuildingQuotaMapService.update(boBuildingQuotaMap, boBuildingQuotaMapUpdateWrapper);

                    //删除楼栋业态
                    BoBuildingProductTypeMap boBuildingProductTypeMap = new BoBuildingProductTypeMap();
                    boBuildingProductTypeMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boBuildingProductTypeMap.setUpdateTime(LocalDateTime.now());
                    UpdateWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapUpdateWrapper = new UpdateWrapper<>();
                    boBuildingProductTypeMapUpdateWrapper.eq("project_quota_extend_id", id);
                    boBuildingProductTypeMapService.update(boBuildingProductTypeMap, boBuildingProductTypeMapUpdateWrapper);

                    //删除楼栋业态指标
                    BoBuildingProductTypeQuotaMap boBuildingProductTypeQuotaMap = new BoBuildingProductTypeQuotaMap();
                    boBuildingProductTypeQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boBuildingProductTypeQuotaMap.setUpdateTime(LocalDateTime.now());
                    UpdateWrapper<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapUpdateWrapper = new UpdateWrapper<>();
                    boBuildingProductTypeQuotaMapUpdateWrapper.inSql("building_product_type_id", "select id from bo_building_product_type_map where project_quota_extend_id='" + id.replace(",", "") + "'");
                    boBuildingProductTypeQuotaMapService.update(boBuildingProductTypeQuotaMap, boBuildingProductTypeQuotaMapUpdateWrapper);
                    break;
                case 1:
                    //删除价格
                    BoProjectPriceExtend boProjectPriceExtend = new BoProjectPriceExtend();
                    boProjectPriceExtend.setId(id);
                    boProjectPriceExtend.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectPriceExtend.setUpdateTime(LocalDateTime.now());
                    boProjectPriceExtendService.updateById(boProjectPriceExtend);

                    //删除价格指标
                    BoProjectPriceQuotaMap boProjectPriceQuotaMap = new BoProjectPriceQuotaMap();
                    boProjectPriceQuotaMap.setIsDelete(IsDeleteEnum.YES.getKey());
                    boProjectPriceQuotaMap.setUpdateTime(LocalDateTime.now());
                    UpdateWrapper<BoProjectPriceQuotaMap> boProjectPriceQuotaMapUpdateWrapper = new UpdateWrapper<>();
                    boProjectPriceQuotaMapUpdateWrapper.eq("project_price_extend_id", id);
                    boProjectPriceQuotaMapService.update(boProjectPriceQuotaMap, boProjectPriceQuotaMapUpdateWrapper);
                    break;
                default:
            }
        }
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "刷新缓存", httpMethod = "POST", notes = "刷新缓存")
    @ResponseBody
    @RequestMapping(value = "/refreshCache")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "key", value = "缓存Key", required = true, paramType = "query"),
            @ApiImplicitParam(name = "must", value = "默认可空，为防意外，系统内置列表不存在的key，需传此项，二次确认。传1表示已确认，坚决处理。", required = false, paramType = "query"),
    })
    public JSONResult refreshCache(@NotBlank(message = "pwd null") String pwd, @NotBlank(message = "must null") Integer must, @NotBlank(message = "key null") String key) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        //部分缓存刷新是直接在redis删除就行，如果是特殊的则需要特殊处理
        switch (key) {
            default:
                if (key.contains("*")) {
                    Set keys = redisTemplate.keys(key);
                    if (CollectionUtils.isNotEmpty(keys)) {
                        redisTemplate.delete(keys);
                    }
                } else {
                    redisTemplate.delete(key);
                }
        }
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "手动计算项目/分期规划指标", httpMethod = "POST", notes = "手动计算项目/分期规划指标")
    @ResponseBody
    @RequestMapping(value = "/calcProjectQuotaMap")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "versionIds", value = "版本ID，多个逗号隔开", required = true, paramType = "query"),
    })
    public JSONResult calcProjectQuotaMap(@NotBlank(message = "pwd null") String pwd, @NotBlank(message = "versionId null") String versionIds) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        String[] split = versionIds.split(",");
        boProjectQuotaMapService.noticeCalcProjectQuota(split);
        return JsonResultBuilder.success("已提交计算任务，当前任务队列尚有 " + redisTemplate.opsForList().size(RedisConstants.PROJECT_QUOTA_CALC_LIST) + " 个任务待处理");
    }


    @ApiOperation(value = "批量计算所有项目/分期规划指标", httpMethod = "POST", notes = "批量计算所有项目/分期规划指标")
    @ResponseBody
    @RequestMapping(value = "/calcProjectQuotaMapMulti")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
    })
    public JSONResult calcProjectQuotaMapMulti(@NotBlank(message = "pwd null") String pwd) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        QueryWrapper<BoProjectQuotaExtend> quotaExtendQueryWrapper = new QueryWrapper<>();
        quotaExtendQueryWrapper.eq("is_delete", 0).eq("is_disable", 0);
        List<BoProjectQuotaExtend> list = boProjectQuotaExtendService.list(quotaExtendQueryWrapper);
        if (!list.isEmpty()) {
            String[] strings = list.parallelStream().map(x -> x.getId()).collect(Collectors.toList()).toArray(new String[]{});
            boProjectQuotaMapService.noticeCalcProjectQuota(strings);
        }
        return JsonResultBuilder.success("已提交计算任务，当前任务队列尚有 " + redisTemplate.opsForList().size(RedisConstants.PROJECT_QUOTA_CALC_LIST) + " 个任务待处理");
    }

    @ApiOperation(value = "经营概览 - 货值 - 手动生成一版货值并入库", httpMethod = "POST", notes = "经营概览 - 货值 - 手动生成一版货值并入库")
    @ResponseBody
    @RequestMapping(value = "/manageOverview/makePrice")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "截止时间,格式：yyyy-MM-dd，默认为当前时间", required = false, paramType = "query"),
    })
    public JSONResult makePrice(@NotBlank(message = "pwd null") String pwd, String endDate) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        LocalDate localDate = null;
        try {
            localDate = StringUtils.isBlank(endDate) ? LocalDate.now() : LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return JsonResultBuilder.failed("日期格式错误");
        }
        boManageOverviewPriceService.makeData(localDate);
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "经营概览 - 面积 - 手动生成一版并入库", httpMethod = "POST", notes = "经营概览 - 面积 - 手动生成一版并入库")
    @ResponseBody
    @RequestMapping(value = "/manageOverview/makeArea")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "截止时间,格式：yyyy-MM-dd，默认为当前时间", required = false, paramType = "query"),
    })
    public JSONResult makeArea(@NotBlank(message = "pwd null") String pwd, String endDate) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        LocalDate localDate = null;
        try {
            localDate = StringUtils.isBlank(endDate) ? LocalDate.now() : LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return JsonResultBuilder.failed("日期格式错误");
        }
        boManageOverviewAreaService.makeData(localDate);
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "为老数据新增指标项", httpMethod = "POST", notes = "为老数据新增指标项")
    @ResponseBody
    @RequestMapping(value = "/oldDataAddQuota")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "quotaGroupMapId", value = "指标与组映射ID，多个逗号隔开", required = true, paramType = "query"),
    })
    public JSONResult oldDataAddQuota(@NotBlank(message = "pwd null") String pwd, @NotBlank(message = "quotaGroupMapId null") String quotaGroupMapId) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        String[] split = quotaGroupMapId.split(",");
        Arrays.stream(split).parallel().forEach(x -> {
            BoQuotaGroupMap boQuotaGroupMap = boQuotaGroupMapService.getById(x);
            if (boQuotaGroupMap != null) {
                String groupCode = boQuotaGroupMap.getGroupCode();
                QuotaGroupCodeEnum quotaGroupCodeEnum = QuotaGroupCodeEnum.getByKey(groupCode);
                if (quotaGroupCodeEnum != null) {
                    switch (quotaGroupCodeEnum) {
                        case LAND_PART_PRODUCT_TYPE:
                            landPartProductTypeQuotaAdd(boQuotaGroupMap);
                            break;
                        case BUILDING_PRODUCT_TYPE:
                            buildingProductTypeQuotaAdd(boQuotaGroupMap);
                            break;
                        default:
                    }
                }
            }
        });
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "全量向销售系统推送楼栋业态", httpMethod = "POST", notes = "全量向销售系统推送楼栋业态")
    @ResponseBody
    @RequestMapping(value = "/allBuildingProductTypePush2Sale")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pwd", value = "密码", required = true, paramType = "query"),
    })
    public JSONResult allBuildingProductTypePush2Sale(@NotBlank(message = "pwd null") String pwd) {
        if (!PASSWORD.equals(pwd)) {
            return JsonResultBuilder.failed("密码错误");
        }
        AtomicInteger success = new AtomicInteger(0);
        Set<String> bdpSet = new ConcurrentHashSet<>();
        List<Map> failedList = Collections.synchronizedList(new ArrayList<>());
        QueryWrapper<BoProjectQuotaExtend> queryWrapper = new QueryWrapper();
        queryWrapper
                .inSql("id","select substring_index(group_concat(id order by create_time desc),',',1) from bo_project_quota_extend where is_delete=0 and is_disable=0 and version_status=2 and stage_code in ('STAGE_02','STAGE_03','STAGE_04') group by project_id");
        List<BoProjectQuotaExtend> list = boProjectQuotaExtendService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)){
            list.parallelStream().forEach(boProjectQuotaExtend -> {
                    BoPushProjectSubDto dto = boBuildingProductTypeMapService.getBuildingProductInfoListByVersionId(boProjectQuotaExtend.getId());
                    if (dto == null){
                        Map map = new HashMap();
                        map.put("version",boProjectQuotaExtend);
                        map.put("param","null");
                        map.put("result","dto为null");
                        failedList.add(map);
                        return;
                    }
                    if (CollectionUtils.isEmpty(dto.getBuildingList())){
                        Map map = new HashMap();
                        map.put("version",boProjectQuotaExtend);
                        map.put("param",dto.toString());
                        map.put("result","buildingList为空");
                        failedList.add(map);
                        return;
                    }
                    dto.getBuildingList().parallelStream().forEach(x->{
                        x.getProductInfo().parallelStream().forEach(y->{
                            bdpSet.add(x.getBuildingId()+y.getProductTypeCode());
                        });
                    });
                    synchronized (AdminController.class){
                        String result = salePushBuildingDataService.pushBuildingData(dto.toString());
                        JSONObject jsonObj = JSONObject.parseObject(result);
                        if (jsonObj.getInteger("code")!= null && jsonObj.getInteger("code") == 0){
                            success.incrementAndGet();
                        }else {
                            Map map = new HashMap();
                            map.put("version",boProjectQuotaExtend);
                            map.put("param",dto.toString());
                            map.put("result",result);
                            failedList.add(map);
                        }
                    }
            });
        }
        return JsonResultBuilder.create().setDefault(CodeEnum.SUCCESS).setMessage("总面积版本数量："+list.size()+",成功: "+success.get()+",失败: "+failedList.size()+"，失败记录见data字段。总推送楼栋业态数量："+bdpSet.size()).setData(failedList).build();
    }

    private void buildingProductTypeQuotaAdd(BoQuotaGroupMap boQuotaGroupMap) {
        //面积管理 -- 楼栋业态新增
        QueryWrapper<BoBuildingProductTypeMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.notExists("select 1 from bo_building_product_type_quota_map where building_product_type_id = bo_building_product_type_map.id and is_delete = 0 and is_disable = 0 and quota_code = '" + boQuotaGroupMap.getCode() + "'")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoBuildingProductTypeMap> list = boBuildingProductTypeMapService.list(queryWrapper);
        if (!list.isEmpty()) {
            List<BoBuildingProductTypeQuotaMap> quotaList = list.parallelStream().map(y -> {
                BoBuildingProductTypeQuotaMap boBuildingProductTypeMap = new BoBuildingProductTypeQuotaMap();
                boBuildingProductTypeMap.setId(UUIDUtils.create());
                BusinessObjectUtils.quotaValueSetDefaultForObject(boQuotaGroupMap.getValueType(), boBuildingProductTypeMap);
                boBuildingProductTypeMap.setBuildingProductTypeId(y.getId());
                boBuildingProductTypeMap.setQuotaCode(boQuotaGroupMap.getCode());
                boBuildingProductTypeMap.setQuotaGroupMapId(boQuotaGroupMap.getId());
                boBuildingProductTypeMap.setQuotaId(boQuotaGroupMap.getQuotaId());
                boBuildingProductTypeMap.setCreateTime(LocalDateTime.now());
                return boBuildingProductTypeMap;
            }).collect(Collectors.toList());
            boBuildingProductTypeQuotaMapService.saveBatch(quotaList);
        }
    }

    private void landPartProductTypeQuotaAdd(BoQuotaGroupMap boQuotaGroupMap) {
        //面积管理 -- 地块业态新增
        QueryWrapper<BoLandPartProductTypeMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.notExists("select 1 from bo_land_part_product_type_quota_map where land_part_product_type_id = bo_land_part_product_type_map.id and is_delete = 0 and is_disable = 0 and quota_code = '" + boQuotaGroupMap.getCode() + "'")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoLandPartProductTypeMap> list = boLandPartProductTypeMapService.list(queryWrapper);
        if (!list.isEmpty()) {
            List<BoLandPartProductTypeQuotaMap> quotaList = list.parallelStream().map(y -> {
                BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = new BoLandPartProductTypeQuotaMap();
                boLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                BusinessObjectUtils.quotaValueSetDefaultForObject(boQuotaGroupMap.getValueType(), boLandPartProductTypeQuotaMap);
                boLandPartProductTypeQuotaMap.setLandPartProductTypeId(y.getId());
                boLandPartProductTypeQuotaMap.setQuotaCode(boQuotaGroupMap.getCode());
                boLandPartProductTypeQuotaMap.setQuotaGroupMapId(boQuotaGroupMap.getId());
                boLandPartProductTypeQuotaMap.setQuotaId(boQuotaGroupMap.getQuotaId());
                boLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                return boLandPartProductTypeQuotaMap;
            }).collect(Collectors.toList());
            boLandPartProductTypeQuotaMapService.saveBatch(quotaList);
        }

        //项目分期地块信息 -- 地块业态新增
        QueryWrapper<BoProjectLandPartProductTypeMap> projectLandQueryWrapper = new QueryWrapper<>();
        projectLandQueryWrapper.notExists("select 1 from bo_project_land_part_product_type_quota_map where project_land_part_product_type_id = bo_project_land_part_product_type_map.id and is_delete = 0 and is_disable = 0 and quota_code = '" + boQuotaGroupMap.getCode() + "'")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey());
        List<BoProjectLandPartProductTypeMap> projectLandPartProductTypeList = boProjectLandPartProductTypeMapService.list(projectLandQueryWrapper);
        if (!projectLandPartProductTypeList.isEmpty()) {
            List<BoProjectLandPartProductTypeQuotaMap> quotaList = projectLandPartProductTypeList.parallelStream().map(y -> {
                BoProjectLandPartProductTypeQuotaMap boProjectLandPartProductTypeMap = new BoProjectLandPartProductTypeQuotaMap();
                boProjectLandPartProductTypeMap.setId(UUIDUtils.create());
                BusinessObjectUtils.quotaValueSetDefaultForObject(boQuotaGroupMap.getValueType(), boProjectLandPartProductTypeMap);
                boProjectLandPartProductTypeMap.setProjectLandPartProductTypeId(y.getId());
                boProjectLandPartProductTypeMap.setQuotaCode(boQuotaGroupMap.getCode());
                boProjectLandPartProductTypeMap.setQuotaGroupMapId(boQuotaGroupMap.getId());
                boProjectLandPartProductTypeMap.setQuotaId(boQuotaGroupMap.getQuotaId());
                boProjectLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                return boProjectLandPartProductTypeMap;
            }).collect(Collectors.toList());
            boProjectLandPartProductTypeQuotaMapService.saveBatch(quotaList);
        }
    }


}
