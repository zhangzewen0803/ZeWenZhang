package com.tahoecn.bo.controller.webapi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.constants.RegexConstant;
import com.tahoecn.bo.common.enums.*;
import com.tahoecn.bo.common.utils.*;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.dto.BoCheckBuildingDto;
import com.tahoecn.bo.model.dto.BoPushProjectSubDto;
import com.tahoecn.bo.model.entity.*;
import com.tahoecn.bo.model.vo.*;
import com.tahoecn.bo.service.*;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;
import com.tahoecn.uc.sso.annotation.Action;
import com.tahoecn.uc.sso.annotation.Login;
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
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 面积管理
 *
 * @author panglx
 */
@Api(tags = "面积管理API", value = "面积管理API")
@Validated
@RestController
@RequestMapping(value = "/api/area")
public class AreaController extends TahoeBaseController {

    private static final Log logger = LogFactory.get();

    @Autowired
    private BoProjectQuotaExtendService boProjectQuotaExtendService;

    @Autowired
    private BoProjectQuotaMapService boProjectQuotaMapService;

    @Autowired
    private BoQuotaGroupMapService boQuotaGroupMapService;

    @Autowired
    private BoLandPartProductTypeMapService boLandPartProductTypeMapService;

    @Autowired
    private BoLandPartProductTypeQuotaMapService boLandPartProductTypeQuotaMapService;

    @Autowired
    private BoBuildingProductTypeMapService boBuildingProductTypeMapService;

    @Autowired
    private BoBuildingProductTypeQuotaMapService boBuildingProductTypeQuotaMapService;

    @Autowired
    private BoBuildingService boBuildingService;

    @Autowired
    private BoBuildingQuotaMapService boBuildingQuotaMapService;

    @Autowired
    private BoProjectExtendService boProjectExtendService;

    @Autowired
    private MdmProjectInfoService mdmProjectInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private BoGovPlanCardService boGovPlanCardService;

    @Autowired
    private BoProjectPlanCardService boProjectPlanCardService;

    @Autowired
    private BoGovPlanCardBuildingMapService boGovPlanCardBuildingMapService;

    @Autowired
    private BoProjectPlanCardBuildingMapService boProjectPlanCardBuildingMapService;

    @Autowired
    private BoProductTypeService boProductTypeService;

    @Autowired
    private BoQuotaStoreService boQuotaStoreService;
    
    @Autowired
	private SalePushBuildingDataService salePushBuildingDataService;

    @Autowired
    private BoApproveRecordService boApproveRecordService;

    @Autowired
    private UcUserService userService;

    @ApiOperation(value = "进入审批前校验", httpMethod = "POST", notes = "进入审批前校验")
    @RequestMapping(value = "/validateBeforApproval", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "版本ID", required = true, paramType = "query"),
    })
    public JSONResult updateVersion(@NotBlank(message = "versionId不能为空") String versionId) {
        BoProjectQuotaExtend projectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
        if (projectQuotaExtend == null){
            return JsonResultBuilder.failed("版本不存在");
        }
        //无processId，且状态为【审批通过】，不允许进入审批
        if (StringUtils.isBlank(projectQuotaExtend.getApproveId()) && VersionStatusEnum.PASSED.getKey() == projectQuotaExtend.getVersionStatus()){
            return JsonResultBuilder.create(CodeEnum.APPROVAL_EXTENDS);
        }
        //有processId，且申请人不是当前用户，不允许进入审批
        if (StringUtils.isNotBlank(projectQuotaExtend.getApproveId())){
            QueryWrapper<BoApproveRecord> queryWrapper = new QueryWrapper();
            queryWrapper.eq("approve_id",projectQuotaExtend.getApproveId())
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

                return JsonResultBuilder.create(CodeEnum.APPROVAL_PROCESS_USE_EXTENDS.getKey(), CodeEnum.APPROVAL_PROCESS_USE_EXTENDS.getValue().replace("**",user==null?"":user.getFdName()));
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
    public JSONResult<String> updateVersion(@NotBlank(message = "versionId不能为空") String versionId, Integer versionStatus) {
    	JSONResult<String> JSONResult = new JSONResult<String>();
    	BoProjectQuotaExtend t = new BoProjectQuotaExtend();
        t.setId(versionId);
        t.setVersionStatus(versionStatus == null ? VersionStatusEnum.PASSED.getKey() : versionStatus);
        CurrentUserVO currentUser = getCurrentUser();
        t.setUpdaterId(currentUser.getId());
        t.setUpdaterName(currentUser.getName());
        t.setUpdateTime(LocalDateTime.now());
        boProjectQuotaExtendService.updateById(t);
        JSONResult.setCode(CodeEnum.SUCCESS.getKey());
        JSONResult.setMsg(CodeEnum.SUCCESS.getValue());
        if(versionStatus == null || versionStatus == VersionStatusEnum.PASSED.getKey()) {
        	try {
        		QueryWrapper<BoProjectQuotaExtend> wrapper = new QueryWrapper<>();
	            wrapper.eq("id", versionId)
	            	.eq("is_delete", IsDeleteEnum.NO.getKey())
	            	.eq("is_disable", IsDisableEnum.NO.getKey());
	            BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getOne(wrapper);
				if(boProjectQuotaExtend != null && !boProjectQuotaExtend.getStageName().equals(StageCodeEnum.STAGE_01.getValue()) && boProjectQuotaExtend.getVersionStatus().equals(VersionStatusEnum.PASSED.getKey())) {
					BoPushProjectSubDto boProjectSubInfoList = boBuildingProductTypeMapService.getBuildingProductInfoListByVersionId(versionId);
					if(DataUtils.isNotEmpty(boProjectSubInfoList.getBuildingList())) {
						String josnStr = salePushBuildingDataService.pushBuildingData(boProjectSubInfoList.toString());
				 		JSONObject josnObj = JSONObject.parseObject(josnStr);
				 		JSONResult.setCode(josnObj.getInteger("code"));
				 		JSONResult.setMsg(josnObj.getString("msg"));
						if(josnObj.get("data") != null) {
							JSONResult.setData(josnObj.getString("data"));
					 	}
				 	}
				}
            } catch (Exception e) {
				logger.error("推送楼栋业态数据到营销失败！");
			}
        }
        return JSONResult;
    }

	/**
	 * @Title: checkAndDelBuildingProductType 
	 * @Description: 校验删除楼栋业态信息
	 * @param boCheckBuildingParam
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年7月16日 上午10:43:29 
	*/
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@ApiOperation(value = "校验删除楼栋业态信息", notes = "校验删除楼栋业态信息")
	@Login(action = Action.Skip)
    @RequestMapping(value = "/checkAndDelBuildingProductType", method = RequestMethod.POST)
	public JSONResult checkAndDelBuildingProductType(@RequestBody BoCheckBuildingParam boCheckBuildingParam) {
		String josnStr = null;
		JSONResult jsonResult = new JSONResult();
        List<BoCheckBuildingDto> checkBulList = new ArrayList<BoCheckBuildingDto>();
        List<BoBuildingProductTypeMap> bulProductTypeList = new ArrayList<BoBuildingProductTypeMap>();
        if(boCheckBuildingParam.getType() == 0) {
            bulProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeListByBulId(boCheckBuildingParam.getBuildingId());
        }else {
        	bulProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeListByProductId(boCheckBuildingParam.getProductTypeId());
		}
        for (BoBuildingProductTypeMap boBuildingProductTypeMap : bulProductTypeList) {
            BoCheckBuildingDto boCheckBuildingDto = new BoCheckBuildingDto();
            boCheckBuildingDto.setBuildingId(boBuildingProductTypeMap.getBuildingOriginId());
            boCheckBuildingDto.setProductTypeCode(boBuildingProductTypeMap.getProductTypeCode());
            checkBulList.add(boCheckBuildingDto);
        }
		if(DataUtils.isNotEmpty(checkBulList)) {
			josnStr = salePushBuildingDataService.checkBuildingData(checkBulList.toString());
		}
		boolean isDelBuld = false;
		boolean cannotDel = false;
		JSONObject josnObj = JSONObject.parseObject(josnStr);
		if(StrUtils.isNotEmpty(josnStr)) {
			JSONArray checkBuildingList = josnObj.getJSONArray("data");
			for (int i = 0; i < checkBuildingList.size(); i++) {
				JSONObject checkBuildingJson = checkBuildingList.getJSONObject(i);
				//校验删除业态
				if(boCheckBuildingParam.getType() == 1) {
					//isDelete:0是不可删1是可删
					if(checkBuildingJson.getString("isDelete").equals("1")) {
						//删除业态
						boBuildingProductTypeMapService.deleteBuildingProductTypeByProductId(boCheckBuildingParam.getProductTypeId(),getCurrentUser());
			        	boBuildingProductTypeQuotaMapService.deletedBuildingProductTypeQuotaByProductTypeId(boCheckBuildingParam.getProductTypeId(),getCurrentUser());
			        	jsonResult.setCode(CodeEnum.SUCCESS.getKey());
						jsonResult.setMsg("删除业态成功！");
						jsonResult.setData(boCheckBuildingParam.getType());
						//查询楼栋下业态
						bulProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeListByBulId(boCheckBuildingParam.getBuildingId());
						if(bulProductTypeList.size() <= 0) {
							boBuildingService.deleteBuildingByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
							boBuildingQuotaMapService.deleteBuildingQuotaByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
							boBuildingProductTypeMapService.deleteBuildingProductTypeByProductId(boCheckBuildingParam.getProductTypeId(),getCurrentUser());
				        	boBuildingProductTypeQuotaMapService.deletedBuildingProductTypeQuotaByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
							jsonResult.setMsg("删除最后一个业态且楼栋成功！");
						}
					}else if (checkBuildingJson.getString("isDelete").equals("0")){
						QueryWrapper<BoBuildingProductTypeMap> boBuildingProductTypeMapQueryWrapper = new QueryWrapper<>();
						boBuildingProductTypeMapQueryWrapper.eq("product_type_code", checkBuildingJson.getString("productTypeCode"))
				        	.eq("building_id", boCheckBuildingParam.getBuildingId())    
				        	.eq("is_delete", IsDeleteEnum.NO.getKey())
				            .eq("is_disable", IsDisableEnum.NO.getKey());
						List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.list(boBuildingProductTypeMapQueryWrapper);
						if(buildingProductTypeList.size() > 1) {
							boBuildingProductTypeMapService.deleteBuildingProductTypeByProductId(boCheckBuildingParam.getProductTypeId(),getCurrentUser());
				        	boBuildingProductTypeQuotaMapService.deletedBuildingProductTypeQuotaByProductTypeId(boCheckBuildingParam.getProductTypeId(),getCurrentUser());
							jsonResult.setCode(CodeEnum.SUCCESS.getKey());
							jsonResult.setMsg("删除业态成功！");
							jsonResult.setData(boCheckBuildingParam.getType());
						}else {
							jsonResult.setCode(CodeEnum.ERROR.getKey());
							jsonResult.setMsg(CodeEnum.ERROR.getValue());
							jsonResult.setData(boCheckBuildingParam.getType());
						}
					}
				}
				
				//校验删除楼栋
				if(boCheckBuildingParam.getType() == 0) {
					if(checkBuildingJson.getString("isDelete").equals("0")) {
						cannotDel = true;
					}else {
						isDelBuld = true;
					}
				}
			}
		}
		
		//校验删除楼栋
		if(boCheckBuildingParam.getType() == 0)  {
			if(isDelBuld && !cannotDel) {
	        	boBuildingService.deleteBuildingByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
	        	boBuildingProductTypeMapService.deleteBuildingProductTypeByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
	        	boBuildingQuotaMapService.deleteBuildingQuotaByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
	        	boBuildingProductTypeQuotaMapService.deletedBuildingProductTypeQuotaByBulId(boCheckBuildingParam.getBuildingId(),getCurrentUser());
	        	jsonResult.setCode(CodeEnum.SUCCESS.getKey());
	        	jsonResult.setMsg("删除楼栋成功！");
	        	jsonResult.setData(boCheckBuildingParam.getType());
	        }else {
				jsonResult.setCode(CodeEnum.ERROR.getKey());
				jsonResult.setMsg(CodeEnum.ERROR.getValue());
				jsonResult.setData(boCheckBuildingParam.getType());
			}
		}
		
		return jsonResult;
	}
    
    /**
	   * 分为项目和分期
	   * 项目的创建版本，仅在投决会阶段有效，且前提条件是必有一个审批通过的项目基础信息版本
	   * 项目版本的复制仅在当前阶段复制，复制包含规划指标、地块业态
	 * <p>
	   * 分期的创建版本
     *
     * @param projectId
     * @param stageCode
     * @return
     */
    @ApiOperation(value = "创建新版本面积", httpMethod = "POST", notes = "创建新版本面积，结果data返回新的版本ID。projectId，stageCode为必填")
    @RequestMapping(value = "/createVersion", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "投决会阶段传项目ID，其他阶段传分期ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stageCode", value = "阶段CODE", required = true, paramType = "query")
    })
    public JSONResult<VersionVO> createVersion(@NotBlank(message = "projectId不能为空") String projectId, @NotBlank(message = "stageCode不能为空") String stageCode) {
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


        String key = RedisConstants.AREA_CREATE_VERSION_LOCK + projectId + stageCode;
        //禁止重复创建
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }
        try {
            //判断是否存在编制中、审批中、审批驳回的数据
            if (boProjectQuotaExtendService.hasCanEditData(projectId, stageCode)) {
                return JsonResultBuilder.failed("当前阶段下存在编制中、审批中或已驳回的数据，禁止创建新版本。");
            }

            //判断项目/分期的基础信息是否存在一个已审批通过的版本
            BoProjectExtend lastProjectVersion = boProjectExtendService.getLastPassedVersion(projectId);
            if (lastProjectVersion == null) {
                return JsonResultBuilder.failed("创建失败，" + projectName + "不存在已审批通过版本。");
            }

            BoProjectQuotaExtend newVersion = boProjectQuotaExtendService.createVersion(projectId, stageCode, getCurrentUser());
            VersionVO versionVO = new VersionVO();
            versionVO.setVersionId(newVersion.getId());
            versionVO.setVersionStatus(newVersion.getVersionStatus());
            versionVO.setVersionName(newVersion.getVersion());
            versionVO.setVersionStatusDesc(VersionStatusEnum.getValueByKey(newVersion.getVersionStatus()));
            return JsonResultBuilder.successWithData(versionVO);
        } finally {
            redisTemplate.delete(key);
        }
    }

    @ApiOperation(value = "查询所有面积版本信息", httpMethod = "POST", notes = "查询所有面积版本信息")
    @RequestMapping(value = "/getAreaVersionList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "投决会阶段传项目ID，其他阶段传分期ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "stageCode", value = "阶段CODE", required = true, paramType = "query")
    })
    public JSONResult<List<VersionVO>> getAreaVersionList(@NotBlank(message = "projectId不能为空") String projectId, @NotBlank(message = "stageCode不能为空") String stageCode) {
        QueryWrapper<BoProjectQuotaExtend> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("stage_code", stageCode)
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .orderByDesc("create_time");
        List<BoProjectQuotaExtend> list = boProjectQuotaExtendService.list(wrapper);
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

    @ApiOperation(value = "查询面积阶段列表与阶段状态", httpMethod = "POST", notes = "查询面积阶段列表与阶段状态")
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
        QueryWrapper<BoProjectQuotaExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("version_status", "max(create_time) create_time", "stage_code")
                .eq("is_delete", IsDeleteEnum.NO.getKey())
                .eq("is_disable", IsDisableEnum.NO.getKey())
                .eq("project_id", projectId)
                .groupBy("stage_code")
                .orderByAsc("stage_code");
        List<BoProjectQuotaExtend> list = boProjectQuotaExtendService.list(queryWrapper);
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


    @ApiOperation(value = "查询面积规划指标", httpMethod = "POST", notes = "查询面积规划指标")
    @RequestMapping(value = "/getAreaQuotaList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "面积版本ID", required = true, paramType = "query"),
    })
    public JSONResult<GetAreaQuotaListVO> getAreaQuotaList(@NotBlank(message = "versionId不能为空") String versionId) {
        GetAreaQuotaListVO getAreaQuotaListVO = new GetAreaQuotaListVO();
        //查指标头
        List<QuotaHeadVO> head = boQuotaGroupMapService.getQuotaGroupMapListReturnQuotaHeadVO(QuotaGroupCodeEnum.PROJECT_SUB_AREA_PLAN.getKey());
        getAreaQuotaListVO.setHead(head);
        Map<String, String> codeValueType = boQuotaStoreService.getCodeValueTypeMap();

        //查指标值
        List<BoProjectQuotaMap> boProjectQuotaMapList = boProjectQuotaMapService.getProjectQuotaMapList(versionId);
        List<QuotaBodyVO> body = boProjectQuotaMapList.parallelStream().map(x -> {
            QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
            quotaBodyVO.setQuotaCode(x.getQuotaCode());
            quotaBodyVO.setQuotaValue(x.getQuotaValue());
            BusinessObjectUtils.quotaValueSetDefaultForObject(codeValueType.get(x.getQuotaCode()), quotaBodyVO);
            return quotaBodyVO;
        }).collect(Collectors.toList());
        getAreaQuotaListVO.setBody(body);
        return JsonResultBuilder.successWithData(getAreaQuotaListVO);
    }

    @ApiOperation(value = "更新面积规划指标", httpMethod = "POST", notes = "更新面积规划指标")
    @RequestMapping(value = "/updateAreaQuota", method = {RequestMethod.POST})
    public JSONResult updateAreaQuotaList(@Validated @RequestBody UpdateAreaQuotaReqBody param) {
        String key = RedisConstants.PROJECT_QUOTA_MAP_UPDATE_LOCK + param.getVersionId();
        try {
            if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
                return JsonResultBuilder.failed("系统繁忙，请勿重复提交");
            }
            String versionId = param.getVersionId();
            BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
            if (boProjectQuotaExtend == null) {
                return JsonResultBuilder.failed("当前版本不存在");
            }
            if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
                return JsonResultBuilder.failed("审批通过的版本禁止编辑");
            }
            if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
                return JsonResultBuilder.failed("审批中的版本禁止编辑");
            }
            List<QuotaBodyVO> data = param.getData();
            if (!data.isEmpty()) {
                List<BoProjectQuotaMap> projectQuotaMapList = boProjectQuotaMapService.getProjectQuotaMapList(versionId);
                if (!projectQuotaMapList.isEmpty()) {
                    CurrentUserVO currentUser = getCurrentUser();
                    projectQuotaMapList.parallelStream().forEach(x -> {
                        x.setUpdaterId(currentUser.getId());
                        x.setUpdaterName(currentUser.getName());
                        x.setUpdateTime(LocalDateTime.now());
                        data.parallelStream().forEach(y -> {
                            if (x.getQuotaCode().equals(y.getQuotaCode())) {
                                x.setQuotaValue(y.getQuotaValue());
                            }
                        });
                    });
                    boProjectQuotaMapService.updateBatchById(projectQuotaMapList,100);
                }
            }
            return JsonResultBuilder.success();
        } finally {
            redisTemplate.delete(key);
        }
    }

    @ApiOperation(value = "查询地块业态指标数据", httpMethod = "POST", notes = "查询地块业态指标数据")
    @RequestMapping(value = "/getLandPartProductTypeQuotaList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "面积版本ID", required = true, paramType = "query"),
    })
    public JSONResult<GetLandPartProductTypeQuotaListVO> getLandPartProductTypeQuotaList(@NotBlank(message = "versionId不能为空") String versionId) {
        GetLandPartProductTypeQuotaListVO getLandPartProductTypeQuotaListVO = new GetLandPartProductTypeQuotaListVO();
        //查指标头
        List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey());
        Map<String, String> codeValueTypeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(quotaGroupMapList)) {
            List<QuotaHeadVO> head = VOUtils.makeQuotaHeadVOList(quotaGroupMapList);
            getLandPartProductTypeQuotaListVO.setHead(head);
            quotaGroupMapList.forEach(x -> codeValueTypeMap.put(x.getCode(), x.getValueType()));
        } else {
            getLandPartProductTypeQuotaListVO.setHead(new ArrayList<>(0));
        }

        //查地块业态关系
        List<BoLandPartProductTypeMap> boLandPartProductTypeMapList = boLandPartProductTypeMapService.getLandPartProductTypeList(versionId);
        if (CollectionUtils.isNotEmpty(boLandPartProductTypeMapList)) {
            List<LandPartProductTypeVO> landPartProductTypeVOList = boLandPartProductTypeMapList.parallelStream().map(x -> {
                LandPartProductTypeVO landPartProductTypeVO = new LandPartProductTypeVO();
                try {
                    PropertyUtils.copyProperties(landPartProductTypeVO, x);
                } catch (Exception e) {
                    logger.error(e);
                }
                return landPartProductTypeVO;
            }).collect(Collectors.toList());
            getLandPartProductTypeQuotaListVO.setLandPartProductTypeList(landPartProductTypeVOList);

            //查地块业态指标
            List<String> landPartProductTypeIdList = boLandPartProductTypeMapList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
            List<BoLandPartProductTypeQuotaMap> quotaMapList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaList(landPartProductTypeIdList);
            if (CollectionUtils.isNotEmpty(quotaMapList)) {
                //指标与关系对象进行关联
                quotaMapList.stream().forEach(x -> {
                    landPartProductTypeVOList.stream().forEach(y -> {
                        if (x.getLandPartProductTypeId().equals(y.getId())) {
                            if (y.getQuotaList() == null) {
                                y.setQuotaList(new ArrayList<>());
                            }
                            QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                            quotaBodyVO.setQuotaCode(x.getQuotaCode());
                            quotaBodyVO.setQuotaValue(x.getQuotaValue());
                            BusinessObjectUtils.quotaValueSetDefaultForObject(codeValueTypeMap.get(x.getQuotaCode()), quotaBodyVO);
                            y.getQuotaList().add(quotaBodyVO);
                        }
                    });
                });
            }
            //手动排序
            Map<String, Integer> codeSortMap = boProductTypeService.getCodeSortMap();
            landPartProductTypeVOList.sort((x, y) -> {
                Integer xSort = codeSortMap.get(x.getProductTypeCode());
                Integer ySort = codeSortMap.get(y.getProductTypeCode());
                if (!xSort.equals(ySort)) {
                    return xSort - ySort;
                }
                for (QuotaBodyVO xBody : x.getQuotaList()) {
                    if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(xBody.getQuotaCode())) {
                        for (QuotaBodyVO yBody : y.getQuotaList()) {
                            if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(xBody.getQuotaCode())) {
                                return StringUtils.compare(xBody.getQuotaValue(), yBody.getQuotaValue());
                            }
                        }
                    }
                }
                return 0;
            });

            //排序
            landPartProductTypeVOList.sort((a, b) -> {
                try {
                    String aVal = a.getQuotaList().stream().filter(x -> QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(x.getQuotaCode())).findFirst().get().getQuotaValue();
                    String bVal = b.getQuotaList().stream().filter(x -> QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(x.getQuotaCode())).findFirst().get().getQuotaValue();
                    return StageCodeEnum.getByKey(aVal).getOrder() - StageCodeEnum.getByKey(bVal).getOrder();
                } catch (Exception e) {
                    return 0;
                }
            });
        } else {
            getLandPartProductTypeQuotaListVO.setLandPartProductTypeList(new ArrayList<>(0));
        }

        return JsonResultBuilder.successWithData(getLandPartProductTypeQuotaListVO);
    }

    @ApiOperation(value = "更新地块业态指标数据（包含新增、删除、更新）", httpMethod = "POST", notes = "更新地块业态指标数据（包含新增、删除、更新）")
    @RequestMapping(value = "/updateLandPartProductTypeQuotaData", method = {RequestMethod.POST})
    public JSONResult updateLandPartProductTypeQuotaData(@Validated @RequestBody UpdateLandPartProductTypeQuotaReqBody param) {
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(param.getVersionId());
        if (boProjectQuotaExtend == null) {
            return JsonResultBuilder.failed("当前版本不存在");
        }
        if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批通过的版本禁止编辑");
        }
        if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批中的版本禁止编辑");
        }
        if (StageCodeEnum.getByKey(boProjectQuotaExtend.getStageCode()).getOrder() > 2) {
            return JsonResultBuilder.failed("当前版本不支持编辑地块业态指标数据");
        }
        //以下List保存将各种对应关系处理之后的结果，用于最终与数据库交互
        List<BoLandPartProductTypeMap> deleteList = new ArrayList<>();
        List<BoLandPartProductTypeQuotaMap> deleteQuotaList = new ArrayList<>();
        List<BoLandPartProductTypeMap> updateList = new ArrayList<>();
        List<BoLandPartProductTypeQuotaMap> updateQuotaList = new ArrayList<>();
        List<BoLandPartProductTypeMap> insertList = new ArrayList<>();
        List<BoLandPartProductTypeQuotaMap> insertQuotaList = new ArrayList<>();

        //封装中间过程，将前端数据转换为数据库可处理的数据
        makeLandPartProductTypePreData2FinalData(param, deleteList, deleteQuotaList, updateList, updateQuotaList, insertList, insertQuotaList);

        //进行增删改
        boLandPartProductTypeMapService.saveOrUpdateOrRemoveLandPartProductType(insertList, insertQuotaList, deleteList, deleteQuotaList, updateList, updateQuotaList);
        boProjectQuotaMapService.noticeCalcProjectQuota(param.getVersionId());

        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "更新地块业态指标数据Mini版（仅更新，无新增或删除）", httpMethod = "POST", notes = "更新地块业态指标数据Mini版（仅更新，无新增或删除）")
    @RequestMapping(value = "/updateLandPartProductTypeQuotaMini", method = {RequestMethod.POST})
    public JSONResult updateLandPartProductTypeQuotaMini(@Validated @RequestBody UpdateLandPartProductTypeQuotaMiniReqBody param) {
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(param.getVersionId());
        if (boProjectQuotaExtend == null) {
            return JsonResultBuilder.failed("当前版本不存在");
        }
        if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批通过的版本禁止编辑");
        }
        if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批中的版本禁止编辑");
        }
        if (StageCodeEnum.getByKey(boProjectQuotaExtend.getStageCode()).getOrder() > 2) {
            return JsonResultBuilder.failed("当前版本不支持编辑地块业态指标数据");
        }

        List<LandPartProductTypeMiniVO> landPartProductTypeList = param.getLandPartProductTypeList();
        if (CollectionUtils.isNotEmpty(landPartProductTypeList)) {
            List<String> idList = landPartProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(idList)) {
                List<BoLandPartProductTypeQuotaMap> landPartProductTypeQuotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaList(idList);
                if (CollectionUtils.isNotEmpty(landPartProductTypeQuotaList)) {

                    List<BoLandPartProductTypeQuotaMap> needUpdateList = new ArrayList<>();
                    Map<String, BoLandPartProductTypeQuotaMap> queryMap = new HashMap<>();
                    landPartProductTypeQuotaList.stream().forEach(x -> queryMap.put(x.getLandPartProductTypeId() + x.getQuotaCode(), x));

                    landPartProductTypeList.stream().forEach(x -> {
                        if (StringUtils.isEmpty(x.getId())) {
                            return;
                        }
                        if (CollectionUtils.isEmpty(x.getQuotaList())) {
                            return;
                        }
                        x.getQuotaList().stream().forEach(y -> {
                            BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = queryMap.get(x.getId() + y.getQuotaCode());
                            if (boLandPartProductTypeQuotaMap != null) {
                                boLandPartProductTypeQuotaMap.setQuotaValue(y.getQuotaValue());
                                needUpdateList.add(boLandPartProductTypeQuotaMap);
                            }
                        });

                    });
                    if (CollectionUtils.isNotEmpty(needUpdateList)) {
                        boLandPartProductTypeQuotaMapService.updateBatchById(needUpdateList,100);
                    }
                }

            }
        }

        boProjectQuotaMapService.noticeCalcProjectQuota(param.getVersionId());
        return JsonResultBuilder.success();
    }

    /**
     * 私有，封装了，将前端地块业态指标数据转换为内部可供数据库可直接处理使用的数据
     *
     * @param param
     * @param deleteList
     * @param deleteQuotaList
     * @param updateList
     * @param updateQuotaList
     * @param insertList
     * @param insertQuotaList
     */
    private void makeLandPartProductTypePreData2FinalData(UpdateLandPartProductTypeQuotaReqBody param, List<BoLandPartProductTypeMap> deleteList, List<BoLandPartProductTypeQuotaMap> deleteQuotaList, List<BoLandPartProductTypeMap> updateList, List<BoLandPartProductTypeQuotaMap> updateQuotaList, List<BoLandPartProductTypeMap> insertList, List<BoLandPartProductTypeQuotaMap> insertQuotaList) {
        String versionId = param.getVersionId();
        List<LandPartProductTypeVO> newList = param.getLandPartProductTypeList();
        List<BoLandPartProductTypeMap> originList = boLandPartProductTypeMapService.getLandPartProductTypeList(versionId);
        Map<String, BoLandPartProductTypeMap> originMap = new HashMap<>();
        originList.stream().forEach(x -> originMap.put(x.getId(), x));
        //需要删除的关系对象列表
        List<BoLandPartProductTypeMap> preDeleteList = new ArrayList<>();
        //需要更新的关系对象Map，使用Map便于判断删除的对象
        Map<String, LandPartProductTypeVO> preUpdateMap = new HashMap<>();
        //需要新增的关系对象列表
        List<LandPartProductTypeVO> preInsertList = new ArrayList<>();

        //遍历新关系列表，判断出新增与修改的数据
        newList.stream().forEach(x -> {
            //id为空表示新增
            if (StringUtils.isBlank(x.getId())) {
                preInsertList.add(x);
                return;
            }

            //id不为空,且在原关系列表中存在，则表示修改
            if (originMap.containsKey(x.getId())) {
                preUpdateMap.put(x.getId(), x);
            }
        });

        //遍历原来的关系对象，如果此对象在新修改的对象列表中不存在，则表示已被删除
        originList.stream().forEach(x -> {
            if (!preUpdateMap.containsKey(x.getId())) {
                preDeleteList.add(x);
            }
        });

        CurrentUserVO currentUser = getCurrentUser();
        //处理数据
        if (!preDeleteList.isEmpty()) {
            //待删除地块业态关系数据
            preDeleteList.parallelStream().forEach(x -> {
                x.setIsDelete(IsDeleteEnum.YES.getKey());
                x.setUpdateTime(LocalDateTime.now());
                x.setUpdaterName(currentUser.getName());
                x.setUpdaterId(currentUser.getId());
            });
            deleteList.addAll(preDeleteList);

            //待删除地块业态指标数据
            List<String> ids = preDeleteList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
            List<BoLandPartProductTypeQuotaMap> quotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaList(ids);
            quotaList.parallelStream().forEach(x -> {
                x.setIsDelete(IsDeleteEnum.YES.getKey());
                x.setUpdateTime(LocalDateTime.now());
                x.setUpdaterName(currentUser.getName());
                x.setUpdaterId(currentUser.getId());
            });
            deleteQuotaList.addAll(quotaList);
        }
        if (!preUpdateMap.isEmpty()) {
            //待更新地块业态关系
            List<BoLandPartProductTypeMap> list = new ArrayList<>();
            preUpdateMap.forEach((k, v) -> {
                BoLandPartProductTypeMap boLandPartProductTypeMap = new BoLandPartProductTypeMap();
                boLandPartProductTypeMap.setUpdateTime(LocalDateTime.now());
                boLandPartProductTypeMap.setUpdaterName(currentUser.getName());
                boLandPartProductTypeMap.setUpdaterId(currentUser.getId());
                try {
                    PropertyUtils.copyProperties(boLandPartProductTypeMap, v);
                } catch (Exception e) {
                    logger.error(e);
                }
                list.add(boLandPartProductTypeMap);
            });
            updateList.addAll(list);

            //待更新地块业态指标数据
            List<String> ids = list.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
            List<BoLandPartProductTypeQuotaMap> quotaList = boLandPartProductTypeQuotaMapService.getLandPartProductTypeQuotaList(ids);
            quotaList.parallelStream().forEach(x -> {
                LandPartProductTypeVO landPartProductTypeVO = preUpdateMap.get(x.getLandPartProductTypeId());
                List<QuotaBodyVO> newQuotaList = landPartProductTypeVO.getQuotaList();
                newQuotaList.parallelStream().forEach(y -> {
                    x.setUpdateTime(LocalDateTime.now());
                    x.setUpdaterName(currentUser.getName());
                    x.setUpdaterId(currentUser.getId());
                    if (x.getQuotaCode().equals(y.getQuotaCode())) {
                        x.setQuotaValue(y.getQuotaValue());
                    }
                });
            });
            updateQuotaList.addAll(quotaList);
        }
        if (!preInsertList.isEmpty()) {
            //插入数据
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.LAND_PART_PRODUCT_TYPE.getKey());
            preInsertList.stream().forEach(x -> {
                BoLandPartProductTypeMap boLandPartProductTypeMap = new BoLandPartProductTypeMap();
                try {
                    PropertyUtils.copyProperties(boLandPartProductTypeMap, x);
                    boLandPartProductTypeMap.setId(UUIDUtils.create());
                    boLandPartProductTypeMap.setCreaterName(currentUser.getName());
                    boLandPartProductTypeMap.setCreateTime(LocalDateTime.now());
                    boLandPartProductTypeMap.setCreaterId(currentUser.getId());
                    boLandPartProductTypeMap.setProjectQuotaExtendId(versionId);
                    insertList.add(boLandPartProductTypeMap);
                    List<BoLandPartProductTypeQuotaMap> tmpQuotaList = quotaGroupMapList.parallelStream().map(y -> {
                        BoLandPartProductTypeQuotaMap boLandPartProductTypeQuotaMap = new BoLandPartProductTypeQuotaMap();
                        boLandPartProductTypeQuotaMap.setId(UUIDUtils.create());
                        boLandPartProductTypeQuotaMap.setCreaterId(currentUser.getId());
                        boLandPartProductTypeQuotaMap.setCreaterName(currentUser.getName());
                        boLandPartProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                        boLandPartProductTypeQuotaMap.setLandPartProductTypeId(boLandPartProductTypeMap.getId());
                        boLandPartProductTypeQuotaMap.setQuotaCode(y.getCode());
                        boLandPartProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                        boLandPartProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                        return boLandPartProductTypeQuotaMap;
                    }).collect(Collectors.toList());
                    tmpQuotaList.parallelStream().forEach(y -> {
                        List<QuotaBodyVO> quotaList = x.getQuotaList();
                        quotaList.parallelStream().forEach(z -> {
                            if (y.getQuotaCode().equals(z.getQuotaCode())) {
                                y.setQuotaValue(z.getQuotaValue());
                            }
                        });
                    });
                    insertQuotaList.addAll(tmpQuotaList);
                } catch (Exception e) {
                    logger.error(e);
                }
            });
        }
    }

    @ApiOperation(value = "查询楼栋业态指标数据", httpMethod = "POST", notes = "查询楼栋业态指标数据")
    @RequestMapping(value = "/getBuildingProductTypeQuotaList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "面积版本ID", required = true, paramType = "query"),
    })
    public JSONResult<GetBuildingProductTypeQuotaListVO> getBuildingProductTypeQuotaList(@NotBlank(message = "versionId不能为空") String versionId) {
        GetBuildingProductTypeQuotaListVO getBuildingProductTypeQuotaListVO = new GetBuildingProductTypeQuotaListVO();
        Map<String, String> codeValueTypeMap = boQuotaStoreService.getCodeValueTypeMap();
        //查head
        List<QuotaHeadVO> head = boQuotaGroupMapService.getQuotaGroupMapListReturnQuotaHeadVO(QuotaGroupCodeEnum.BUILDING_PRODUCT_TYPE.getKey(), QuotaGroupCodeEnum.BUILDING.getKey());
        getBuildingProductTypeQuotaListVO.setHead(head);


        //查询楼栋
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
        if (!buildingList.isEmpty()) {
            List<BuildingVO> buildingVOList = buildingList.parallelStream().map(x -> {
                BuildingVO buildingVO = new BuildingVO();
                try {
                    PropertyUtils.copyProperties(buildingVO, x);
                } catch (Exception e) {
                    logger.error(e);
                }
                return buildingVO;
            }).collect(Collectors.toList());
            getBuildingProductTypeQuotaListVO.setBuildingList(buildingVOList);

            //楼栋排序
            buildingVOList.sort((a, b) -> {
                String aName = a.getName();
                String bName = b.getName();
                Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
                Integer aNo = null;
                if (aMatcher.find()) {
                    aNo = Integer.valueOf(aMatcher.group(1));
                }
                Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
                Integer bNo = null;
                if (bMatcher.find()) {
                    bNo = Integer.valueOf(bMatcher.group(1));
                }
                if (aNo == null && bNo == null) {
                    return aName.compareTo(bName);
                }

                return aNo == null ? 1 : bNo == null ? -1 : aNo - bNo;
            });

            //查楼栋指标列表
            List<BoBuildingQuotaMap> buildingQuotaList = boBuildingQuotaMapService.getBuildingQuotaList(versionId);
            buildingQuotaList.stream().forEach(x -> {
                for (BuildingVO buildingVO : buildingVOList) {
                    if (x.getBuildingId().equals(buildingVO.getId())) {
                        if (buildingVO.getQuotaList() == null) {
                            buildingVO.setQuotaList(new ArrayList<>());
                        }
                        QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                        quotaBodyVO.setQuotaCode(x.getQuotaCode());
                        quotaBodyVO.setQuotaValue(x.getQuotaValue());
                        BusinessObjectUtils.quotaValueSetDefaultForObject(codeValueTypeMap.get(x.getQuotaCode()), quotaBodyVO);
                        buildingVO.getQuotaList().add(quotaBodyVO);
                        return;
                    }
                }
            });

            //查楼栋业态关系
            List<BoBuildingProductTypeMap> buildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(versionId);
            List<BuildingProductTypeVO> buildingProductTypeVOList = buildingProductTypeList.parallelStream().map(x -> {
                BuildingProductTypeVO buildingProductTypeVO = new BuildingProductTypeVO();
                try {
                    PropertyUtils.copyProperties(buildingProductTypeVO, x);
                } catch (Exception e) {
                    logger.error(e);
                }
                return buildingProductTypeVO;
            }).collect(Collectors.toList());

            //查楼栋业态指标
            List<BoBuildingProductTypeQuotaMap> quotaMapList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(versionId);

            //指标与关系对象进行关联
            quotaMapList.stream().forEach(x -> {
                buildingProductTypeVOList.stream().forEach(y -> {
                    if (x.getBuildingProductTypeId().equals(y.getId())) {
                        if (y.getQuotaList() == null) {
                            y.setQuotaList(new ArrayList<>());
                        }
                        QuotaBodyVO quotaBodyVO = new QuotaBodyVO();
                        quotaBodyVO.setQuotaCode(x.getQuotaCode());
                        quotaBodyVO.setQuotaValue(x.getQuotaValue());
                        BusinessObjectUtils.quotaValueSetDefaultForObject(codeValueTypeMap.get(x.getQuotaCode()), quotaBodyVO);
                        y.getQuotaList().add(quotaBodyVO);
                    }
                });
            });
            getBuildingProductTypeQuotaListVO.setBuildingProductTypeList(buildingProductTypeVOList);

            //排序 规则：先按业态CODE排序，业态CODE相同再按楼栋名称排序，楼栋名称相同，再按产权、精装、层高排序
            Set<String> queryQuotaSet = Stream.of(QuotaCodeEnum.PROPERTY_RIGHT.getKey(), QuotaCodeEnum.REFINED_DECORATION.getKey(), QuotaCodeEnum.FLOOR_HIGH.getKey()).collect(Collectors.toSet());
            buildingProductTypeVOList.sort((a, b) -> {
                //不等则按业态CODE排序
                if (!a.getProductTypeCode().equals(b.getProductTypeCode())){
                    return a.getProductTypeCode().compareTo(b.getProductTypeCode());
                }

                // 业态CODE相等按楼栋名称排序
                String aName = a.getBuildingName();
                String bName = b.getBuildingName();
                Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
                Integer aNo = null;
                if (aMatcher.find()) {
                    aNo = Integer.valueOf(aMatcher.group(1));
                }
                Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
                Integer bNo = null;
                if (bMatcher.find()) {
                    bNo = Integer.valueOf(bMatcher.group(1));
                }
                if (aNo == null && bNo == null) {
                    return aName.compareTo(bName);
                }
                if (aNo == null){
                    return 1;
                }
                if (bNo == null){
                    return -1;
                }
                if (!aNo.equals(bNo)){
                    return aNo - bNo;
                }

                //如果楼栋名称相同，按照3属性排序
               try {
                    Integer aSort = a.getQuotaList().stream().filter(x -> queryQuotaSet.contains(x.getQuotaCode())).map(x -> DictItemEnum.getByKey(x.getQuotaValue()).getSortNo()).reduce((x, y) -> x + y).get();
                    Integer bSort = b.getQuotaList().stream().filter(x -> queryQuotaSet.contains(x.getQuotaCode())).map(x -> DictItemEnum.getByKey(x.getQuotaValue()).getSortNo()).reduce((x, y) -> x + y).get();
                    if (aSort < bSort){
                        return -1;
                    }else if (aSort > bSort){
                        return 1;
                    }else{
                        return 0;
                    }
                } catch (Exception e) {
                    return 0;
                }
            });
        } else {
            getBuildingProductTypeQuotaListVO.setBuildingList(new ArrayList<>(0));
            getBuildingProductTypeQuotaListVO.setBuildingProductTypeList(new ArrayList<>(0));
        }
        return JsonResultBuilder.successWithData(getBuildingProductTypeQuotaListVO);
    }

    @ApiOperation(value = "查询楼栋列表数据", httpMethod = "POST", notes = "查询楼栋列表数据")
    @RequestMapping(value = "/getBuildingList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "面积版本ID", required = true, paramType = "query"),
    })
    public JSONResult<List<GetBuildingListVO>> getBuildingList(@NotBlank(message = "versionId不能为空") String versionId) {
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
        if (buildingList.isEmpty()) {
            return JsonResultBuilder.successWithData(new ArrayList(0));
        }
        List<GetBuildingListVO> buildingVOList = buildingList.stream().map(x -> {
            GetBuildingListVO getBuildingListVO = new GetBuildingListVO();
            getBuildingListVO.setId(x.getId());
            getBuildingListVO.setName(x.getName());
            getBuildingListVO.setProjectLandPartId(x.getProjectLandPartId());
            getBuildingListVO.setProjectLandPartName(x.getProjectLandPartName());
            return getBuildingListVO;
        }).collect(Collectors.toList());

        //楼栋排序
        buildingVOList.sort((a, b) -> {
            String aName = a.getName();
            String bName = b.getName();
            Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
            Integer aNo = null;
            if (aMatcher.find()) {
                aNo = Integer.valueOf(aMatcher.group(1));
            }
            Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
            Integer bNo = null;
            if (bMatcher.find()) {
                bNo = Integer.valueOf(bMatcher.group(1));
            }
            if (aNo == null && bNo == null) {
                return aName.compareTo(bName);
            }

            return aNo == null ? 1 : bNo == null ? -1 : aNo - bNo;
        });
        return JsonResultBuilder.successWithData(buildingVOList);
    }


    @ApiOperation(value = "更新楼栋业态指标数据（包含对所有楼栋的增删改，对楼栋所有业态的增删改）", httpMethod = "POST", notes = "更新楼栋业态指标数据（包含对楼栋的增删改，对楼栋业态的增删改）")
    @RequestMapping(value = "/updateBuildingProductTypeQuotaData", method = {RequestMethod.POST})
    public JSONResult updateBuildingProductTypeQuotaData(@Validated @RequestBody UpdateBuildingProductTypeQuotaReqBody param) {
        String key = RedisConstants.BUILDING_PRODUCT_TYPE_LOCK + param.getVersionId();
        try {
            if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
                return JsonResultBuilder.failed("系统繁忙，请勿重复提交。");
            }
            String versionId = param.getVersionId();
            BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
            if (boProjectQuotaExtend == null) {
                return JsonResultBuilder.failed("当前版本不存在");
            }
            if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
                return JsonResultBuilder.failed("审批通过的版本禁止编辑");
            }
            if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
                return JsonResultBuilder.failed("审批中的版本禁止编辑");
            }

            List<BuildingVO> buildingList = param.getBuildingList();
            List<BuildingProductTypeVO> buildingProductTypeList = param.getBuildingProductTypeList();

            //判断楼栋列表与楼栋业态关系中的楼栋信息是否同步
            Set<String> buildingNameSet1 = buildingList.parallelStream().map(x -> x.getName()).collect(Collectors.toSet());
            Set<String> buildingNameSet2 = buildingProductTypeList.parallelStream().map(x -> x.getBuildingName()).collect(Collectors.toSet());
            if (buildingNameSet1.size() != buildingNameSet2.size()) {
                return JsonResultBuilder.failed("buildList与buildingProductTypeList的楼栋数据不同步，请检查数据");
            }
            for (String name : buildingNameSet1) {
                if (!buildingNameSet2.contains(name)) {
                    return JsonResultBuilder.failed("buildList与buildingProductTypeList的楼栋数据不同步，请检查数据");
                }
            }

            List<BoBuilding> insertBuildingList = new ArrayList<>();
            List<BoBuildingQuotaMap> insertBuildingQuotaList = new ArrayList<>();
            List<BoBuilding> updateBuildingList = new ArrayList<>();
            List<BoBuildingQuotaMap> updateBuildingQuotaList = new ArrayList<>();
            List<BoBuilding> deleteBuildingList = new ArrayList<>();

            List<BoBuildingProductTypeMap> insertBuildingProductTypeList = new ArrayList<>();
            List<BoBuildingProductTypeQuotaMap> insertBuildingProductTypeQuotaList = new ArrayList<>();
            List<BoBuildingProductTypeQuotaMap> updateBuildingProductTypeQuotaList = new ArrayList<>();
            List<BoBuildingProductTypeMap> deleteBuildingProductTypeList = new ArrayList<>();


            //处理楼栋
            makePreBuildingData2FinalBuildingData(versionId, buildingList, insertBuildingList, insertBuildingQuotaList, updateBuildingList, updateBuildingQuotaList, deleteBuildingList);

            //处理楼栋业态
            makePreBuildingProductTypeData2FinalBuildingProductTypeData(versionId, buildingProductTypeList, insertBuildingList, updateBuildingList, insertBuildingProductTypeList, insertBuildingProductTypeQuotaList, updateBuildingProductTypeQuotaList, deleteBuildingProductTypeList);

            Map<String, Object> servMap = new HashMap<>();
            servMap.put("insertBuildingList", insertBuildingList);
            servMap.put("deleteBuildingList", deleteBuildingList);

            servMap.put("insertBuildingQuotaList", insertBuildingQuotaList);
//            servMap.put("updateBuildingQuotaList", updateBuildingQuotaList);

            servMap.put("insertBuildingProductTypeList", insertBuildingProductTypeList);
            servMap.put("deleteBuildingProductTypeList", deleteBuildingProductTypeList);

            servMap.put("insertBuildingProductTypeQuotaList", insertBuildingProductTypeQuotaList);
//            servMap.put("updateBuildingProductTypeQuotaList", updateBuildingProductTypeQuotaList);

            boBuildingProductTypeMapService.saveOrDeleteOrUpdateBuildingProductType(servMap);
            boProjectQuotaMapService.noticeCalcProjectQuota(param.getVersionId());
            return JsonResultBuilder.success();
        } finally {
            redisTemplate.delete(key);
        }
    }

    @ApiOperation(value = "更新楼栋业态指标数据Mini版（仅包含对单栋数据编辑，并支持复制到批量选择的楼栋）", httpMethod = "POST", notes = "更新楼栋业态指标数据Mini版（仅包含对单栋数据编辑，并支持复制的批量选择的楼栋）")
    @RequestMapping(value = "/updateBuildingProductTypeQuotaMini", method = {RequestMethod.POST})
    public JSONResult updateBuildingProductTypeQuotaMini(@Validated @RequestBody UpdateBuildingProductTypeQuotaMiniReqBody param) {
        String key = RedisConstants.BUILDING_PRODUCT_TYPE_LOCK + "mini" + param.getVersionId();
        try {
            if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
                return JsonResultBuilder.failed("系统繁忙，请勿重复提交");
            }
            String versionId = param.getVersionId();
            BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
            if (boProjectQuotaExtend == null) {
                return JsonResultBuilder.failed("当前版本不存在");
            }
            if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
                return JsonResultBuilder.failed("审批通过的版本禁止编辑");
            }
            if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
                return JsonResultBuilder.failed("审批中的版本禁止编辑");
            }
            UpdateBuildingBaseEnum buildingBaseEnum = UpdateBuildingBaseEnum.getByKey(param.getBase());
            if (buildingBaseEnum == null) {
                return JsonResultBuilder.failed("base不存在");
            }
            CurrentUserVO currentUser = getCurrentUser();
            switch (buildingBaseEnum) {
                case BUILDING:
                    processBaseBuilding(param, versionId, currentUser);
                    break;
                case PRODUCT_TYPE:
                    processBaseProductType(param, versionId, currentUser);

                    break;
                default:
            }
            boProjectQuotaMapService.noticeCalcProjectQuota(param.getVersionId());
            return JsonResultBuilder.success();
        } finally {
            redisTemplate.delete(key);
        }
    }

    private void processBaseProductType(UpdateBuildingProductTypeQuotaMiniReqBody param, String versionId, CurrentUserVO currentUser) {
        //楼栋指标，全部相同
        List<String> buildingIds = Arrays.stream(param.getBuildingIds().split(",")).collect(Collectors.toList());
        List<BoBuildingQuotaMap> buildingQuotaList = boBuildingQuotaMapService.getBuildingQuotaList(buildingIds);

        //Map<楼栋CODE,指标体>
        Map<String, QuotaBodyVO> paramBuildingQuotaQueryMap = new HashMap<>();
        param.getBuildingQuotaList().stream().forEach(x -> paramBuildingQuotaQueryMap.put(x.getQuotaCode(), x));

        //待更新的楼栋指标列表
        List<BoBuildingQuotaMap> updateBuildingQuotaList = buildingQuotaList.parallelStream().filter(x -> {
            QuotaBodyVO quotaBodyVO = paramBuildingQuotaQueryMap.get(x.getQuotaCode());
            if (quotaBodyVO == null) {
                return false;
            }
            x.setQuotaValue(quotaBodyVO.getQuotaValue());
            x.setUpdateTime(LocalDateTime.now());
            x.setUpdaterName(currentUser.getName());
            x.setUpdaterId(currentUser.getId());
            return true;
        }).collect(Collectors.toList());


        //楼栋业态指标全部相同，增改，有则改，无则增
        List<BoBuildingProductTypeMap> originUpdateBuildingProductTypeList = new ArrayList<>();
        List<BoBuildingProductTypeMap> insertBuildingProductTypeList = new ArrayList<>();
        List<BoBuildingProductTypeQuotaMap> updateBuildingProductTypeQuotaList = new ArrayList<>();
        List<BoBuildingProductTypeQuotaMap> insertBuildingProductTypeQuotaList = new ArrayList<>();

        //参数传来的基准业态，所有业态按照此基准业态修改
        //Map<业态CODE+产精高,对应业态的指标列表>
        Map<String, Map<String, String>> paramProductType3prodAndQuotaMap = new HashMap<>();
        param.getBuildingProductTypeList().stream().forEach(x -> {
            List<QuotaBodyVO> quotaList = x.getQuotaList();
            //0:产权，1：精装，2：层高
            String[] vals = new String[3];
            if (CollectionUtils.isNotEmpty(quotaList)){
                for (QuotaBodyVO quotaBodyVO:quotaList){
                    if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(quotaBodyVO.getQuotaCode())){
                        vals[0] = quotaBodyVO.getQuotaValue();
                    }else if (QuotaCodeEnum.REFINED_DECORATION.getKey().equals(quotaBodyVO.getQuotaCode())){
                        vals[1] = quotaBodyVO.getQuotaValue();
                    }else if (QuotaCodeEnum.FLOOR_HIGH.getKey().equals(quotaBodyVO.getQuotaCode())){
                        vals[2] = quotaBodyVO.getQuotaValue();
                    }else {
                        continue;
                    }
                }
            }
            String key = x.getProductTypeCode()+","+vals[0]+","+vals[1]+","+vals[2];
            Map<String, String> map = paramProductType3prodAndQuotaMap.get(key);
            if (map == null) {
                map = new HashMap<>();
                paramProductType3prodAndQuotaMap.put(key, map);
            }
            Map<String, String> finalMap = map;
            x.getQuotaList().stream().forEach(y -> finalMap.put(y.getQuotaCode(), y.getQuotaValue()));
        });

        //查楼栋的所有业态，用于判断哪些楼的业态需要增/改
        List<BoBuildingProductTypeMap> originBuildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeListByBuildingIdList(buildingIds);
        List<String> originBuildingProductTypeIdList = originBuildingProductTypeList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
        List<BoBuildingProductTypeQuotaMap> originAllBuildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(originBuildingProductTypeIdList);

        //Map<原楼栋业态ID，Map<指标CODE,指标值>>
        Map<String,Map<String,String>> originBuildingProductTypeIdAndQuotaMap = new ConcurrentHashMap<>();
        originAllBuildingProductTypeQuotaList.parallelStream().forEach(x->{
            Map<String, String> map = originBuildingProductTypeIdAndQuotaMap.get(x.getBuildingProductTypeId());
            if (map == null){
                map = new ConcurrentHashMap<>();
                Map<String, String> last = originBuildingProductTypeIdAndQuotaMap.putIfAbsent(x.getBuildingProductTypeId(), map);
                if (last != null){
                    map = last;
                }
            }
            if (StringUtils.isNoneEmpty(x.getQuotaCode(),x.getQuotaValue())){
                map.put(x.getQuotaCode(),x.getQuotaValue());
            }
        });


        List<String> buildingProductTypeMapIds = new ArrayList<>();

        originBuildingProductTypeList.stream().forEach(x -> {
            buildingProductTypeMapIds.add(x.getId());
            Map<String, String> map = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
            if (map == null){
                return;
            }
            String key = x.getProductTypeCode()+","+map.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey())+","+map.get(QuotaCodeEnum.REFINED_DECORATION.getKey())+","+map.get(QuotaCodeEnum.FLOOR_HIGH.getKey());
            if (paramProductType3prodAndQuotaMap.containsKey(key)) {
                //判断更新的业态
                originUpdateBuildingProductTypeList.add(x);
            }
        });

        //查询所有业态的指标信息，用于增改指标信息
        Map<String, List<BoBuildingProductTypeQuotaMap>> originBuildingProductTypeAndQuotaListMap = new HashMap<>();
        originAllBuildingProductTypeQuotaList.stream().forEach(x -> {
            List<BoBuildingProductTypeQuotaMap> tmpList = originBuildingProductTypeAndQuotaListMap.get(x.getBuildingProductTypeId());
            if (tmpList == null) {
                tmpList = new ArrayList<>();
                originBuildingProductTypeAndQuotaListMap.put(x.getBuildingProductTypeId(), tmpList);
            }
            tmpList.add(x);
        });

        //判断需要新增业态的楼栋
        //对楼栋与业态进行分组，可用于判断哪些楼栋需要增加业态
        Map<String, Set<String>> originBuildingIdAndProductTypeCode3propMap = new HashMap<>();
        originBuildingProductTypeList.stream().forEach(x -> {
            Set<String> set = originBuildingIdAndProductTypeCode3propMap.get(x.getBuildingId());
            if (set == null) {
                set = new HashSet<>();
                originBuildingIdAndProductTypeCode3propMap.put(x.getBuildingId(), set);
            }
            Map<String, String> map = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
            if (map == null){
                return;
            }
            String key = x.getProductTypeCode()+","+map.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey())+","+map.get(QuotaCodeEnum.REFINED_DECORATION.getKey())+","+map.get(QuotaCodeEnum.FLOOR_HIGH.getKey());
            set.add(key);
        });

        //楼栋信息，用于取楼栋名称、originId等
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(buildingIds);
        Map<String, BoBuilding> queryBuildingMap = new HashMap<>();
        buildingList.stream().forEach(x -> queryBuildingMap.put(x.getId(), x));

        //已删除的业态信息,用于获取已删除业态的originId
        /*List<BoBuildingProductTypeMap> deletedBuildingProductTypeList = boBuildingProductTypeMapService.getDeletedBuildingProductTypeList(versionId);
        Map<String, BoBuildingProductTypeMap> queryDeletedBuildingProductTypeMap = new HashMap<>();
        makeDeletedBuildingProductTypeQueryMap(deletedBuildingProductTypeList, queryDeletedBuildingProductTypeMap);*/
        //业态对应的名称信息，用于获取业态的名称、ID等信息
        Map<String, BoBuildingProductTypeMap> originBuildingProductTypeCode3propAndExpampleMap = new HashMap<>();
        originUpdateBuildingProductTypeList.stream().forEach(x -> {
            Map<String, String> map = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
            if (map == null){
                return;
            }
            String key = x.getProductTypeCode()+","+map.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey())+","+map.get(QuotaCodeEnum.REFINED_DECORATION.getKey())+","+map.get(QuotaCodeEnum.FLOOR_HIGH.getKey());
            if (originBuildingProductTypeCode3propAndExpampleMap.containsKey(key)) {
                return;
            }
            originBuildingProductTypeCode3propAndExpampleMap.put(key, x);
        });

        //判断需要新增的业态
        originBuildingProductTypeCode3propAndExpampleMap.forEach((productTypeCode3prop, exmProductTypeMap) -> {
            originBuildingIdAndProductTypeCode3propMap.forEach((buildingId, productTypeCode3propSet) -> {
                if (!productTypeCode3propSet.contains(productTypeCode3prop)) {
                    BoBuildingProductTypeMap newObj = new BoBuildingProductTypeMap();
                    newObj.setBuildingId(buildingId);
                    newObj.setId(UUIDUtils.create());
                    newObj.setProjectQuotaExtendId(versionId);
                    BoBuilding boBuilding = queryBuildingMap.get(buildingId);
                    newObj.setBuildingName(boBuilding.getName());
                     /*Map<String, String> newQuota = paramProductType3prodAndQuotaMap.get(productTypeCode3prop);
                   String key = boBuilding.getOriginId() + productTypeCode3prop + QuotaCodeEnum.PROPERTY_RIGHT.getKey() + newQuota.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey()) + QuotaCodeEnum.REFINED_DECORATION.getKey() + newQuota.get(QuotaCodeEnum.REFINED_DECORATION.getKey()) + QuotaCodeEnum.FLOOR_HIGH.getKey() + newQuota.get(QuotaCodeEnum.FLOOR_HIGH.getKey());
                    BoBuildingProductTypeMap oldObj = queryDeletedBuildingProductTypeMap.get(key);
                    if (oldObj == null) {
                        newObj.setOriginId(UUIDUtils.create());
                    } else {
                        newObj.setOriginId(oldObj.getOriginId());
                    }*/
                    newObj.setBuildingOriginId(boBuilding.getOriginId());
                    newObj.setProductTypeCode(productTypeCode3prop.split(",")[0]);
                    newObj.setProductTypeId(exmProductTypeMap.getProductTypeId());
                    newObj.setProductTypeName(exmProductTypeMap.getProductTypeName());
                    newObj.setCreateTime(LocalDateTime.now());
                    newObj.setCreaterName(currentUser.getName());
                    newObj.setCreaterId(currentUser.getId());
                    insertBuildingProductTypeList.add(newObj);
                }
            });
        });

        //处理需要更新的楼栋业态指标
        if (CollectionUtils.isNotEmpty(originUpdateBuildingProductTypeList)) {
            originUpdateBuildingProductTypeList.stream().forEach(x -> {
                List<BoBuildingProductTypeQuotaMap> tmp = originBuildingProductTypeAndQuotaListMap.get(x.getId());
                Map<String, String> map = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
                if (map == null){
                    return;
                }
                String key = x.getProductTypeCode()+","+map.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey())+","+map.get(QuotaCodeEnum.REFINED_DECORATION.getKey())+","+map.get(QuotaCodeEnum.FLOOR_HIGH.getKey());
                Map<String, String> quotaMap = paramProductType3prodAndQuotaMap.get(key);
                tmp.stream().forEach(y -> {

                    y.setUpdateTime(LocalDateTime.now());
                    y.setUpdaterName(currentUser.getName());
                    y.setUpdaterId(currentUser.getId());
                    y.setQuotaValue(quotaMap.get(y.getQuotaCode()));
                });
                updateBuildingProductTypeQuotaList.addAll(tmp);
            });
        }

        //处理需要新增的楼栋业态指标
        if (CollectionUtils.isNotEmpty(insertBuildingProductTypeList)) {
            List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.BUILDING_PRODUCT_TYPE.getKey());
            insertBuildingProductTypeList.stream().forEach(x -> {
                String key = x.getProductTypeCode()+","+paramBuildingQuotaQueryMap.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey())+","+paramBuildingQuotaQueryMap.get(QuotaCodeEnum.REFINED_DECORATION.getKey())+","+paramBuildingQuotaQueryMap.get(QuotaCodeEnum.FLOOR_HIGH.getKey());
                Map<String, String> map = paramProductType3prodAndQuotaMap.get(key);
                quotaGroupMapList.stream().forEach(y -> {
                    BoBuildingProductTypeQuotaMap tmp = new BoBuildingProductTypeQuotaMap();
                    tmp.setBuildingProductTypeId(x.getId());
                    if (map != null) {
                        tmp.setQuotaValue(map.get(y.getCode()));
                    } else {
                        BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), tmp);
                    }
                    tmp.setQuotaCode(y.getCode());
                    tmp.setCreaterId(currentUser.getId());
                    tmp.setCreaterName(currentUser.getName());
                    tmp.setCreateTime(LocalDateTime.now());
                    tmp.setId(UUIDUtils.create());
                    tmp.setQuotaGroupMapId(y.getId());
                    tmp.setQuotaId(y.getQuotaId());
                    insertBuildingProductTypeQuotaList.add(tmp);
                });

            });
        }
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("updateBuildingQuotaList", updateBuildingQuotaList);
        objectMap.put("insertBuildingProductTypeList", insertBuildingProductTypeList);
        objectMap.put("insertBuildingProductTypeQuotaList", insertBuildingProductTypeQuotaList);
        objectMap.put("updateBuildingProductTypeQuotaList", updateBuildingProductTypeQuotaList);
        boBuildingProductTypeMapService.saveOrDeleteOrUpdateBuildingProductType(objectMap);
    }

    /**
     * 将已删除的楼栋业态对象组织成一个可通过 楼栋origin_id + product_type_code + (产权code+value) + (精装code+value) + (层高code+value)
     * 用于构成一个数据唯一的判断条件，并以此判断历史数据中是否这个关系
     *
     * @param deletedBuildingProductTypeList
     * @param queryDeletedBuildingProductTypeMap
     */
    private void makeDeletedBuildingProductTypeQueryMap(List<BoBuildingProductTypeMap> deletedBuildingProductTypeList, Map<String, BoBuildingProductTypeMap> queryDeletedBuildingProductTypeMap) {
        if (!deletedBuildingProductTypeList.isEmpty()) {
            List<BoBuildingProductTypeQuotaMap> deletedBuildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getDeletedBuildingProductTypeQuotaList(deletedBuildingProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toList()));
            deletedBuildingProductTypeList.stream().forEach(x -> {
                //0存储产权属性code+value，1存储精装code+value，2存储层高code+value
                String[] limitCodeStrArr = new String[3];
                deletedBuildingProductTypeQuotaList.stream().forEach(y -> {
                    if (!x.getId().equals(y.getBuildingProductTypeId())) {
                        return;
                    }
                    if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(y.getQuotaCode())) {
                        limitCodeStrArr[0] = y.getQuotaCode() + y.getQuotaValue();
                        return;
                    }
                    if (QuotaCodeEnum.REFINED_DECORATION.getKey().equals(y.getQuotaCode())) {
                        limitCodeStrArr[1] = y.getQuotaCode() + y.getQuotaValue();
                        return;
                    }
                    if (QuotaCodeEnum.FLOOR_HIGH.getKey().equals(y.getQuotaCode())) {
                        limitCodeStrArr[2] = y.getQuotaCode() + y.getQuotaValue();
                        return;
                    }
                });
                String key = x.getBuildingOriginId() + x.getProductTypeCode() + limitCodeStrArr[0] + limitCodeStrArr[1] + limitCodeStrArr[2];
                queryDeletedBuildingProductTypeMap.put(key, x);
            });
        }
    }

    private void processBaseBuilding(UpdateBuildingProductTypeQuotaMiniReqBody param, String versionId, CurrentUserVO currentUser) {
        //楼栋指标，全部相同

        List<String> buildingIds = Arrays.stream(param.getBuildingIds().split(",")).collect(Collectors.toList());

        //楼栋指标更新数据线程
        ForkJoinTask<List<BoBuildingQuotaMap>> updateBuildingQuotaJob = ForkJoinPool.commonPool().submit(() -> {
            //查询所传楼栋Id的所有楼栋指标信息
            List<BoBuildingQuotaMap> buildingQuotaList = boBuildingQuotaMapService.getBuildingQuotaList(buildingIds);

            //构造方便查询的QuotaCode /value Map
            Map<String, QuotaBodyVO> queryMap = new HashMap<>();
            param.getBuildingQuotaList().stream().forEach(x -> queryMap.put(x.getQuotaCode(), x));

            //批量为每个楼栋的楼栋指标赋予新值
            List<BoBuildingQuotaMap> updateBuildingQuotaList = buildingQuotaList.parallelStream().filter(x -> {
                QuotaBodyVO quotaBodyVO = queryMap.get(x.getQuotaCode());
                if (quotaBodyVO == null) {
                    return false;
                }
                x.setQuotaValue(quotaBodyVO.getQuotaValue());
                return true;
            }).collect(Collectors.toList());
            return updateBuildingQuotaList;
        });


        //楼栋业态指标全部相同，增删改
        List<BoBuildingProductTypeQuotaMap> updateBuildingProductTypeQuotaList = new ArrayList<>();
        List<BoBuildingProductTypeQuotaMap> insertBuildingProductTypeQuotaList = new ArrayList<>();
        List<BoBuildingProductTypeQuotaMap> deleteBuildingProductTypeQuotaList = new ArrayList<>();
        List<BoBuildingProductTypeMap> deleteBuildingProductTypeList = new ArrayList<>();
        List<BoBuildingProductTypeMap> updateBuildingProductTypeList = new ArrayList<>();
        List<BoBuildingProductTypeMap> insertBuildingProductTypeList = new ArrayList<>();

        //基准业态，用于判断哪些楼的业态需要增/删/改
        Map<String, Map<String, String>> paramBtcRefineHighAndQuotaMap = new HashMap<>();
        List<BuildingProductTypeMiniVO> buildingProductTypeList = param.getBuildingProductTypeList();
        buildingProductTypeList.stream().forEach(x -> {
            String[] ptcRefineHigh = new String[4];
            ptcRefineHigh[0] = x.getProductTypeCode();
            List<QuotaBodyVO> quotaList = x.getQuotaList();
            for (QuotaBodyVO quotaBodyVO:quotaList){
                QuotaCodeEnum quotaCodeEnum = QuotaCodeEnum.getByKey(quotaBodyVO.getQuotaCode());
                if (quotaCodeEnum == null){
                    continue;
                }
                switch (quotaCodeEnum){
                    case REFINED_DECORATION:
                        ptcRefineHigh[1] = quotaBodyVO.getQuotaValue();
                        break;
                    case PROPERTY_RIGHT:
                        ptcRefineHigh[2] = quotaBodyVO.getQuotaValue();
                        break;
                    case FLOOR_HIGH:
                        ptcRefineHigh[3] = quotaBodyVO.getQuotaValue();
                        break;
                    default:
                }
            }
            String key = StringUtils.join(ptcRefineHigh,",");
            Map<String, String> map = paramBtcRefineHighAndQuotaMap.get(key);
            if (map == null) {
                map = new HashMap<>();
                paramBtcRefineHighAndQuotaMap.put(key, map);
            }
            Map<String, String> finalMap = map;
            x.getQuotaList().stream().forEach(y -> finalMap.put(y.getQuotaCode(), y.getQuotaValue()));
        });

        //查楼栋的所有业态，用于判断哪些楼的业态需要增/删/改
        List<BoBuildingProductTypeMap> originAllBuildingsBuildingProductTypeMapList = boBuildingProductTypeMapService.getBuildingProductTypeListByBuildingIdList(buildingIds);
        //查询所有业态的指标信息，用于增删改指标信息
        List<BoBuildingProductTypeQuotaMap> orginAllBuildingsBtcQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(originAllBuildingsBuildingProductTypeMapList.stream().map(x->x.getId()).collect(Collectors.toList()));
        Map<String, List<BoBuildingProductTypeQuotaMap>> originBuildingProductTypeIdAndQuotaMap = new HashMap<>();
        orginAllBuildingsBtcQuotaList.stream().forEach(x -> {
            List<BoBuildingProductTypeQuotaMap> tmpList = originBuildingProductTypeIdAndQuotaMap.get(x.getBuildingProductTypeId());
            if (tmpList == null) {
                tmpList = new ArrayList<>();
                originBuildingProductTypeIdAndQuotaMap.put(x.getBuildingProductTypeId(), tmpList);
            }
            tmpList.add(x);
        });


        originAllBuildingsBuildingProductTypeMapList.stream().forEach(x -> {
            List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMaps = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
            String[] ptcRefineHigh = new String[4];
            ptcRefineHigh[0] = x.getProductTypeCode();
            makePtcRefineHigh(boBuildingProductTypeQuotaMaps, ptcRefineHigh);
            String key = StringUtils.join(ptcRefineHigh,",");
            if (!paramBtcRefineHighAndQuotaMap.containsKey(key)) {
                //判断删除的业态
                deleteBuildingProductTypeList.add(x);
            } else {
                //判断更新的业态
                updateBuildingProductTypeList.add(x);
            }
        });

        //判断需要新增业态的楼栋
        //对楼栋与业态进行分组，可用于判断哪些楼栋需要增加业态
        Map<String, Set<String>> originBuildingIdAndPtcRefineHighMap = new HashMap<>();
        originAllBuildingsBuildingProductTypeMapList.stream().forEach(x -> {
            Set<String> set = originBuildingIdAndPtcRefineHighMap.get(x.getBuildingId());
            if (set == null) {
                set = new HashSet<>();
                originBuildingIdAndPtcRefineHighMap.put(x.getBuildingId(), set);
            } else {
                set = originBuildingIdAndPtcRefineHighMap.get(x.getBuildingId());
            }
            List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMaps = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
            String[] ptcRefineHigh = new String[4];
            ptcRefineHigh[0] = x.getProductTypeCode();
            makePtcRefineHigh(boBuildingProductTypeQuotaMaps, ptcRefineHigh);
            String key = StringUtils.join(ptcRefineHigh,",");
            set.add(key);
        });
        //楼栋信息，用于取楼栋名称、originId等
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(buildingIds);
        Map<String, BoBuilding> queryBuildingMap = new HashMap<>();
        buildingList.stream().forEach(x -> queryBuildingMap.put(x.getId(), x));

        //已删除的业态信息,用于获取已删除业态的originId(不必记录楼栋业态的originId了)
        /*List<BoBuildingProductTypeMap> deletedBuildingProductTypeList = boBuildingProductTypeMapService.getDeletedBuildingProductTypeList(versionId);
        Map<String, BoBuildingProductTypeMap> queryDeletedBuildingProductTypeMap = new HashMap<>();
        if (!deletedBuildingProductTypeList.isEmpty()) {
            List<BoBuildingProductTypeQuotaMap> deletedBuildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getDeletedBuildingProductTypeQuotaList(deletedBuildingProductTypeList.stream().map(x -> x.getId()).collect(Collectors.toList()));
            if (!deletedBuildingProductTypeQuotaList.isEmpty()) {
                deletedBuildingProductTypeList.stream().forEach(x -> {
                    deletedBuildingProductTypeQuotaList.stream().forEach(y -> {
                        if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(y.getQuotaCode())) {
                            queryDeletedBuildingProductTypeMap.put(x.getBuildingOriginId() + x.getProductTypeCode() + y.getQuotaValue(), x);
                        }
                    });
                });
            }
        }*/

        //业态对应的名称信息，用于获取业态的名称、ID等信息
        Map<String, BoBuildingProductTypeMap> originPtcRefineHighAndBuildingProductTypeMap = new HashMap<>();
        updateBuildingProductTypeList.stream().forEach(x -> {
            List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMaps = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
            String[] ptcRefineHigh = new String[4];
            ptcRefineHigh[0] = x.getProductTypeCode();
            makePtcRefineHigh(boBuildingProductTypeQuotaMaps, ptcRefineHigh);
            String key = StringUtils.join(ptcRefineHigh,",");
            if (originPtcRefineHighAndBuildingProductTypeMap.containsKey(key) ){
                return;
            }
            originPtcRefineHighAndBuildingProductTypeMap.put(key, x);
        });

        //判断需要新增的业态 处理需要新增的楼栋业态指标
        List<BoQuotaGroupMap> quotaGroupMapList = new ArrayList<>();
        originPtcRefineHighAndBuildingProductTypeMap.forEach((ptcRefineProHigh, exmBuildingProductTypeMap) -> {
            originBuildingIdAndPtcRefineHighMap.forEach((buildingId, ptcRefineProHighSet) -> {
                if (!ptcRefineProHighSet.contains(ptcRefineProHigh)) {
                    BoBuildingProductTypeMap newObj = new BoBuildingProductTypeMap();
                    newObj.setBuildingId(buildingId);
                    newObj.setId(UUIDUtils.create());
                    newObj.setProjectQuotaExtendId(versionId);
                    BoBuilding boBuilding = queryBuildingMap.get(buildingId);
                    newObj.setBuildingName(boBuilding.getName());
//                    Map<String, String> newQuota = queryProductTypeQuotaListMap.get(ptcRefineProHigh);
                    /*BoBuildingProductTypeMap oldObj = queryDeletedBuildingProductTypeMap.get(boBuilding.getOriginId() + ptcRefineProHigh + newQuota.get(QuotaCodeEnum.PROPERTY_RIGHT.getKey()));
                    if (oldObj == null) {
                        newObj.setOriginId(UUIDUtils.create());
                    } else {
                        newObj.setOriginId(oldObj.getOriginId());
                    }*/
                    newObj.setBuildingOriginId(boBuilding.getOriginId());
                    String[] ptcRefineHigh = ptcRefineProHigh.split(",");
                    newObj.setProductTypeCode(ptcRefineHigh[0]);
                    newObj.setProductTypeId(exmBuildingProductTypeMap.getProductTypeId());
                    newObj.setProductTypeName(exmBuildingProductTypeMap.getProductTypeName());
                    newObj.setCreateTime(LocalDateTime.now());
                    newObj.setCreaterName(currentUser.getName());
                    newObj.setCreaterId(currentUser.getId());
                    insertBuildingProductTypeList.add(newObj);

                    //指标
                    if (quotaGroupMapList.isEmpty()){
                        quotaGroupMapList.addAll(boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.BUILDING_PRODUCT_TYPE.getKey()));
                    }
                    Map<String, String> map = paramBtcRefineHighAndQuotaMap.get(ptcRefineProHigh);
                    quotaGroupMapList.stream().forEach(y -> {
                        BoBuildingProductTypeQuotaMap tmp = new BoBuildingProductTypeQuotaMap();
                        tmp.setBuildingProductTypeId(newObj.getId());
                        if (map != null) {
                            tmp.setQuotaValue(map.get(y.getCode()));
                        } else {
                            BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(), tmp);
                        }
                        tmp.setQuotaCode(y.getCode());
                        tmp.setCreaterId(currentUser.getId());
                        tmp.setCreaterName(currentUser.getName());
                        tmp.setCreateTime(LocalDateTime.now());
                        tmp.setId(UUIDUtils.create());
                        tmp.setQuotaGroupMapId(y.getId());
                        tmp.setQuotaId(y.getQuotaId());
                        insertBuildingProductTypeQuotaList.add(tmp);
                    });
                }
            });
        });

        //处理需要删除的楼栋业态指标，也一块删除
        if (CollectionUtils.isNotEmpty(deleteBuildingProductTypeList)) {
            deleteBuildingProductTypeList.stream().forEach(x -> {
                x.setUpdaterName(currentUser.getName());
                x.setUpdateTime(LocalDateTime.now());
                x.setUpdaterId(currentUser.getId());
                x.setIsDelete(IsDeleteEnum.YES.getKey());
                List<BoBuildingProductTypeQuotaMap> tmp = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
                deleteBuildingProductTypeQuotaList.addAll(tmp);
            });
        }

        //处理需要更新的楼栋业态指标
        if (CollectionUtils.isNotEmpty(updateBuildingProductTypeList)) {
            updateBuildingProductTypeList.stream().forEach(x -> {
                List<BoBuildingProductTypeQuotaMap> tmp = originBuildingProductTypeIdAndQuotaMap.get(x.getId());
                String[] ptcRefineHigh = new String[4];
                ptcRefineHigh[0] = x.getProductTypeCode();
                makePtcRefineHigh(tmp, ptcRefineHigh);
                String key = StringUtils.join(ptcRefineHigh,",");
                Map<String, String> map = paramBtcRefineHighAndQuotaMap.get(key);
                tmp.stream().forEach(y -> {
                    y.setUpdateTime(LocalDateTime.now());
                    y.setUpdaterName(currentUser.getName());
                    y.setUpdaterId(currentUser.getId());
                    y.setQuotaValue(map.get(y.getQuotaCode()));
                });
                updateBuildingProductTypeQuotaList.addAll(tmp);
            });
        }


        Map<String, Object> objectMap = new HashMap<>();
        try {
            List<BoBuildingQuotaMap> updateBuildingQuotaList = updateBuildingQuotaJob.get();
            objectMap.put("updateBuildingQuotaList", updateBuildingQuotaList);
            objectMap.put("insertBuildingProductTypeList", insertBuildingProductTypeList);
            objectMap.put("deleteBuildingProductTypeList", deleteBuildingProductTypeList);
            objectMap.put("insertBuildingProductTypeQuotaList", insertBuildingProductTypeQuotaList);
            objectMap.put("updateBuildingProductTypeQuotaList", updateBuildingProductTypeQuotaList);
            objectMap.put("deleteBuildingProductTypeQuotaList", deleteBuildingProductTypeQuotaList);
        } catch (Exception e) {
            logger.error(e);
        }
        boBuildingProductTypeMapService.saveOrDeleteOrUpdateBuildingProductType(objectMap);
    }

    private void makePtcRefineHigh(List<BoBuildingProductTypeQuotaMap> tmp, String[] ptcRefineHigh) {
        for (BoBuildingProductTypeQuotaMap boBuildingProductTypeMap:tmp){
            QuotaCodeEnum quotaCodeEnum = QuotaCodeEnum.getByKey(boBuildingProductTypeMap.getQuotaCode());
            if (quotaCodeEnum == null){
                continue;
            }
            switch (quotaCodeEnum){
                case REFINED_DECORATION:
                    ptcRefineHigh[1] = boBuildingProductTypeMap.getQuotaValue();
                    break;
                case PROPERTY_RIGHT:
                    ptcRefineHigh[2] = boBuildingProductTypeMap.getQuotaValue();
                    break;
                case FLOOR_HIGH:
                    ptcRefineHigh[3] = boBuildingProductTypeMap.getQuotaValue();
                    break;
                default:
            }
        }
    }

    @ApiOperation(value = "新增政府方案证", httpMethod = "POST", notes = "新增政府方案证")
    @RequestMapping(value = "/addGovPlanCard", method = {RequestMethod.POST})
    public JSONResult addGovPlanCard(@Validated @RequestBody AddGovPlanCardReqBody param) {
        String versionId = param.getVersionId();
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
        if (boProjectQuotaExtend == null) {
            return JsonResultBuilder.failed("版本信息不存在");
        }
        if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批通过的版本禁止新增政府方案");
        }
        if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批中的版本禁止新增政府方案");
        }
        if (StageCodeEnum.getByKey(boProjectQuotaExtend.getStageCode()) != StageCodeEnum.STAGE_03) {
            return JsonResultBuilder.failed("新增政府方案失败，该版本不处于" + StageCodeEnum.STAGE_03.getValue() + "阶段");
        }
        LocalDateTime replyTime = DateUtils.parseDate(param.getReplyTime(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (replyTime == null) {
            return JsonResultBuilder.failed("replyTime格式错误");
        }
        String key = RedisConstants.AREA_ADD_GOV_PLAN_CARD_LOCK + param.getPlanCode();
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }
        try {
            if (boGovPlanCardService.existsPlanCode(versionId, param.getPlanCode())) {
                return JsonResultBuilder.failed("planCode已存在，禁止重复添加");
            }

            List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
            if (CollectionUtils.isEmpty(buildingList)) {
                return JsonResultBuilder.failed("新增失败，当前版本不存在楼栋信息");
            }
            Map<String, BoBuilding> buildingMap = new HashMap<>();
            buildingList.stream().forEach(x -> buildingMap.put(x.getId(), x));


            BoGovPlanCard boGovPlanCard = new BoGovPlanCard();
            boGovPlanCard.setId(UUIDUtils.create());
            boGovPlanCard.setProjectQuotaExtendId(versionId);
            CurrentUserVO currentUser = getCurrentUser();
            boGovPlanCard.setCreaterId(currentUser.getId());
            boGovPlanCard.setCreaterName(currentUser.getName());
            boGovPlanCard.setCreateTime(LocalDateTime.now());
            boGovPlanCard.setPlanCode(param.getPlanCode());
            boGovPlanCard.setReplyTime(replyTime);


            List<String> buildingIdList = param.getBuildingIdList();
            List<BoGovPlanCardBuildingMap> govPlanCardBuildingMapList = new ArrayList<>();
            for (String buildingId : buildingIdList) {
                BoBuilding boBuilding = buildingMap.get(buildingId);
                if (boBuilding == null) {
                    return JsonResultBuilder.failed("楼栋信息不存在,ID：" + buildingId);
                }
                BoGovPlanCardBuildingMap boGovPlanCardBuildingMap = new BoGovPlanCardBuildingMap();
                boGovPlanCardBuildingMap.setId(UUIDUtils.create());
                boGovPlanCardBuildingMap.setGovPlanCardId(boGovPlanCard.getId());
                boGovPlanCardBuildingMap.setBuildingId(buildingId);
                boGovPlanCardBuildingMap.setOriginBuildingId(buildingMap.get(buildingId).getOriginId());
                boGovPlanCardBuildingMap.setCreaterId(currentUser.getId());
                boGovPlanCardBuildingMap.setCreaterName(currentUser.getName());
                boGovPlanCardBuildingMap.setCreateTime(LocalDateTime.now());
                govPlanCardBuildingMapList.add(boGovPlanCardBuildingMap);
            }
            boGovPlanCardService.addGovPlanCardAndBuilding(boGovPlanCard, govPlanCardBuildingMapList);
            return JsonResultBuilder.success();
        } finally {
            redisTemplate.delete(key);
        }
    }

    @ApiOperation(value = "修改政府方案证", httpMethod = "POST", notes = "修改政府方案证")
    @RequestMapping(value = "/updateGovPlanCard", method = {RequestMethod.POST})
    public JSONResult updateGovPlanCard(@Validated @RequestBody UpdateGovPlanCardReqBody param) {
        String govPlanCardId = param.getGovPlanCardId();
        BoGovPlanCard boGovPlanCard = boGovPlanCardService.getById(govPlanCardId);
        if (boGovPlanCard == null) {
            return JsonResultBuilder.failed("政府方案不存在");
        }
        String versionId = boGovPlanCard.getProjectQuotaExtendId();
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
        if (boProjectQuotaExtend == null) {
            JsonResultBuilder.failed("政府方案版本信息不存在");
        }
        if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批通过的版本禁止修改政府方案");
        }
        if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批中的版本禁止修改政府方案");
        }
        LocalDateTime replyTime = DateUtils.parseDate(param.getReplyTime(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (replyTime == null) {
            return JsonResultBuilder.failed("replyTime格式错误");
        }

        //政府方案
        boGovPlanCard.setPlanCode(param.getPlanCode());
        boGovPlanCard.setReplyTime(replyTime);
        CurrentUserVO currentUser = getCurrentUser();
        boGovPlanCard.setUpdaterName(currentUser.getName());
        boGovPlanCard.setUpdaterId(currentUser.getId());
        boGovPlanCard.setUpdateTime(LocalDateTime.now());

        //政府方案楼栋新增、删除，无修改
        List<BoGovPlanCardBuildingMap> originGovPlanCardBuildingList = boGovPlanCardBuildingMapService.getGovPlanCardBuildingList(boGovPlanCard.getId());
        Set<String> originBuildingIdSet = originGovPlanCardBuildingList.parallelStream().map(x -> x.getBuildingId()).collect(Collectors.toSet());
        List<String> newBuildingIdList = param.getBuildingIdList();

        List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
        Map<String, BoBuilding> buildingMap = new HashMap<>();
        buildingList.stream().forEach(x -> buildingMap.put(x.getId(), x));

        List<BoGovPlanCardBuildingMap> deleteList = new ArrayList<>();
        List<BoGovPlanCardBuildingMap> insertList = new ArrayList<>();
        for (String newBuildingId : newBuildingIdList) {
            if (!originBuildingIdSet.contains(newBuildingId)) {
                BoBuilding boBuilding = buildingMap.get(newBuildingId);
                if (boBuilding == null) {
                    return JsonResultBuilder.failed("楼栋信息不存在，ID: " + newBuildingId);
                }
                BoGovPlanCardBuildingMap boGovPlanCardBuildingMap = new BoGovPlanCardBuildingMap();
                boGovPlanCardBuildingMap.setId(UUIDUtils.create());
                boGovPlanCardBuildingMap.setGovPlanCardId(boGovPlanCard.getId());
                boGovPlanCardBuildingMap.setBuildingId(newBuildingId);
                boGovPlanCardBuildingMap.setOriginBuildingId(boBuilding.getOriginId());
                boGovPlanCardBuildingMap.setCreaterId(currentUser.getId());
                boGovPlanCardBuildingMap.setCreaterName(currentUser.getName());
                boGovPlanCardBuildingMap.setCreateTime(LocalDateTime.now());
                insertList.add(boGovPlanCardBuildingMap);
            }
        }

        originGovPlanCardBuildingList.stream().forEach(x -> {
            if (!newBuildingIdList.contains(x.getBuildingId())) {
                x.setIsDelete(IsDeleteEnum.YES.getKey());
                x.setUpdaterName(currentUser.getName());
                x.setUpdaterId(currentUser.getId());
                x.setUpdateTime(LocalDateTime.now());
                deleteList.add(x);
            }
        });
        boGovPlanCardService.updateGovPlanCardAndBuilding(boGovPlanCard, deleteList, insertList);
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "删除政府方案证", httpMethod = "POST", notes = "删除政府方案证")
    @RequestMapping(value = "/removeGovPlanCard", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "政府方案证ID，支持批量删除，一次最多20条，多个以','隔开。", required = true, paramType = "query"),
    })
    public JSONResult removeGovPlanCard(@NotEmpty(message = "ids不能为空") String[] ids) {
        if (ids.length > 20) {
            return JsonResultBuilder.failed("删除失败，一次最多支持20条数据，本次提交：" + ids.length + "条数据");
        }
        boGovPlanCardService.removeGovPlanCard(ids, getCurrentUser());
        return JsonResultBuilder.success();

    }

    @ApiOperation(value = "查询政府方案证", httpMethod = "POST", notes = "删除政府方案证")
    @RequestMapping(value = "/getGovPlanCardList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "面积版本ID", required = true, paramType = "query"),
    })
    public JSONResult<List<GetGovPlanCardListVO>> getGovPlanCardList(@NotBlank(message = "面积版本ID") String versionId) {
        List<BoGovPlanCard> govPlanCardList = boGovPlanCardService.getGovPlanCardList(versionId);
        if (CollectionUtils.isEmpty(govPlanCardList)) {
            return JsonResultBuilder.successWithData(new ArrayList<>(0));
        }
        List<String> refIds = govPlanCardList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
        List<BoGovPlanCardBuildingMap> govPlanCardBuildingList = boGovPlanCardBuildingMapService.getGovPlanCardBuildingList(refIds);
        List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
        Map<String, BoBuilding> boBuildingMap = new HashMap<>();
        buildingList.stream().forEach(x -> boBuildingMap.put(x.getId(), x));
        List<GetGovPlanCardListVO> result = govPlanCardList.parallelStream().map(x -> {
            GetGovPlanCardListVO getGovPlanCardListVO = new GetGovPlanCardListVO();
            getGovPlanCardListVO.setPlanCode(x.getPlanCode());
            getGovPlanCardListVO.setId(x.getId());
            getGovPlanCardListVO.setReplyTime(x.getReplyTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
            getGovPlanCardListVO.setBuildingList(new ArrayList<>());
            govPlanCardBuildingList.stream().forEach(y -> {
                if (x.getId().equals(y.getGovPlanCardId())) {
                    BoBuilding boBuilding = boBuildingMap.get(y.getBuildingId());
                    if (boBuilding != null) {
                        GetGovPlanCardListVO.Building building = new GetGovPlanCardListVO.Building();
                        building.setId(boBuilding.getId());
                        building.setName(boBuilding.getName());
                        getGovPlanCardListVO.getBuildingList().add(building);
                    }
                }
            });

            List<GetGovPlanCardListVO.Building> tmp = getGovPlanCardListVO.getBuildingList();
            if (CollectionUtils.isNotEmpty(tmp)){
                //楼栋排序
                tmp.sort((a, b) -> {
                    String aName = a.getName();
                    String bName = b.getName();
                    Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
                    Integer aNo = null;
                    if (aMatcher.find()) {
                        aNo = Integer.valueOf(aMatcher.group(1));
                    }
                    Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
                    Integer bNo = null;
                    if (bMatcher.find()) {
                        bNo = Integer.valueOf(bMatcher.group(1));
                    }
                    if (aNo == null && bNo == null) {
                        return aName.compareTo(bName);
                    }

                    return aNo == null ? 1 : bNo == null ? -1 : aNo - bNo;
                });
            }
            return getGovPlanCardListVO;
        }).collect(Collectors.toList());
        return JsonResultBuilder.successWithData(result);
    }


    @ApiOperation(value = "新增工规证", httpMethod = "POST", notes = "新增工规证")
    @RequestMapping(value = "/addProjectPlanCard", method = {RequestMethod.POST})
    public JSONResult addProjectPlanCard(@Validated @RequestBody AddProjectPlanCardReqBody param) {
        String versionId = param.getVersionId();
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
        if (boProjectQuotaExtend == null) {
            return JsonResultBuilder.failed("版本信息不存在");
        }
        if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批通过的版本禁止新增工规证");
        }
        if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批中的版本禁止新增工规证");
        }
        if (StageCodeEnum.getByKey(boProjectQuotaExtend.getStageCode()) != StageCodeEnum.STAGE_04) {
            return JsonResultBuilder.failed("新增工规证失败，该版本不处于" + StageCodeEnum.STAGE_04.getValue() + "阶段");
        }
        LocalDateTime replyTime = DateUtils.parseDate(param.getReplyTime(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (replyTime == null) {
            return JsonResultBuilder.failed("replyTime格式错误");
        }
        String key = RedisConstants.AREA_ADD_PROJECT_PLAN_CARD_LOCK + param.getPlanCode();
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }

        try {
            if (boProjectPlanCardService.existsPlanCode(versionId, param.getPlanCode())) {
                return JsonResultBuilder.failed("planCode已存在，禁止重复添加");
            }

            List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
            if (CollectionUtils.isEmpty(buildingList)) {
                return JsonResultBuilder.failed("新增失败，当前版本不存在楼栋信息");
            }
            Map<String, BoBuilding> buildingMap = new HashMap<>();
            buildingList.stream().forEach(x -> buildingMap.put(x.getId(), x));

            BoProjectPlanCard boProjectPlanCard = new BoProjectPlanCard();
            boProjectPlanCard.setId(UUIDUtils.create());
            boProjectPlanCard.setProjectQuotaExtendId(versionId);
            CurrentUserVO currentUser = getCurrentUser();
            boProjectPlanCard.setCreaterId(currentUser.getId());
            boProjectPlanCard.setCreaterName(currentUser.getName());
            boProjectPlanCard.setCreateTime(LocalDateTime.now());
            boProjectPlanCard.setPlanCode(param.getPlanCode());
            boProjectPlanCard.setReplyTime(replyTime);


            List<String> buildingIdList = param.getBuildingIdList();
            List<BoProjectPlanCardBuildingMap> govPlanCardBuildingMapList = new ArrayList<>();
            for (String buildingId : buildingIdList) {
                BoBuilding boBuilding = buildingMap.get(buildingId);
                if (boBuilding == null) {
                    return JsonResultBuilder.failed("楼栋信息不存在,ID：" + buildingId);
                }
                BoProjectPlanCardBuildingMap boProjectPlanCardBuildingMap = new BoProjectPlanCardBuildingMap();
                boProjectPlanCardBuildingMap.setId(UUIDUtils.create());
                boProjectPlanCardBuildingMap.setProjectPlanCardId(boProjectPlanCard.getId());
                boProjectPlanCardBuildingMap.setBuildingId(buildingId);
                boProjectPlanCardBuildingMap.setOriginBuildingId(boBuilding.getOriginId());
                boProjectPlanCardBuildingMap.setCreaterId(currentUser.getId());
                boProjectPlanCardBuildingMap.setCreaterName(currentUser.getName());
                boProjectPlanCardBuildingMap.setCreateTime(LocalDateTime.now());
                govPlanCardBuildingMapList.add(boProjectPlanCardBuildingMap);
            }
            boProjectPlanCardService.addProjectPlanCard(boProjectPlanCard, govPlanCardBuildingMapList);
            return JsonResultBuilder.success();
        } finally {
            redisTemplate.delete(key);
        }
    }

    @ApiOperation(value = "修改工规证", httpMethod = "POST", notes = "修改工规证")
    @RequestMapping(value = "/updateProjetPlanCard", method = {RequestMethod.POST})
    public JSONResult updateProjetPlanCard(@Validated @RequestBody UpdateProjectPlanCardReqBody param) {
        String govPlanCardId = param.getProjectPlanCardId();
        BoProjectPlanCard boProjectPlanCard = boProjectPlanCardService.getById(govPlanCardId);
        if (boProjectPlanCard == null) {
            return JsonResultBuilder.failed("工规证不存在");
        }
        String versionId = boProjectPlanCard.getProjectQuotaExtendId();
        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getById(versionId);
        if (boProjectQuotaExtend == null) {
            JsonResultBuilder.failed("工规证版本信息不存在");
        }
        if (VersionStatusEnum.PASSED.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批通过的版本禁止修改工规证");
        }
        if (VersionStatusEnum.CHECKING.getKey() == boProjectQuotaExtend.getVersionStatus()) {
            return JsonResultBuilder.failed("审批中的版本禁止修改工规证");
        }
        LocalDateTime replyTime = DateUtils.parseDate(param.getReplyTime(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (replyTime == null) {
            return JsonResultBuilder.failed("replyTime格式错误");
        }
        //工规证
        boProjectPlanCard.setPlanCode(param.getPlanCode());
        boProjectPlanCard.setReplyTime(replyTime);
        CurrentUserVO currentUser = getCurrentUser();
        boProjectPlanCard.setUpdaterName(currentUser.getName());
        boProjectPlanCard.setUpdaterId(currentUser.getId());
        boProjectPlanCard.setUpdateTime(LocalDateTime.now());

        //工规证楼栋新增、删除，无修改
        List<BoProjectPlanCardBuildingMap> boProjectPlanCardBuildingList = boProjectPlanCardBuildingMapService.getBoProjectPlanCardBuildingList(boProjectPlanCard.getId());
        Set<String> originBuildingIdSet = boProjectPlanCardBuildingList.parallelStream().map(x -> x.getBuildingId()).collect(Collectors.toSet());
        List<String> newBuildingIdList = param.getBuildingIdList();

        List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
        Map<String, BoBuilding> buildingMap = new HashMap<>();
        buildingList.stream().forEach(x -> buildingMap.put(x.getId(), x));

        List<BoProjectPlanCardBuildingMap> deleteList = new ArrayList<>();
        List<BoProjectPlanCardBuildingMap> insertList = new ArrayList<>();
        for (String newBuildingId : newBuildingIdList) {
            if (!originBuildingIdSet.contains(newBuildingId)) {
                BoBuilding boBuilding = buildingMap.get(newBuildingId);
                if (boBuilding == null) {
                    return JsonResultBuilder.failed("楼栋信息不存在，ID: " + newBuildingId);
                }
                BoProjectPlanCardBuildingMap boProjectPlanCardBuildingMap = new BoProjectPlanCardBuildingMap();
                boProjectPlanCardBuildingMap.setId(UUIDUtils.create());
                boProjectPlanCardBuildingMap.setProjectPlanCardId(boProjectPlanCard.getId());
                boProjectPlanCardBuildingMap.setBuildingId(newBuildingId);
                boProjectPlanCardBuildingMap.setOriginBuildingId(boBuilding.getOriginId());
                boProjectPlanCardBuildingMap.setCreaterId(currentUser.getId());
                boProjectPlanCardBuildingMap.setCreaterName(currentUser.getName());
                boProjectPlanCardBuildingMap.setCreateTime(LocalDateTime.now());
                insertList.add(boProjectPlanCardBuildingMap);
            }
        }

        boProjectPlanCardBuildingList.stream().forEach(x -> {
            if (!newBuildingIdList.contains(x.getBuildingId())) {
                x.setIsDelete(IsDeleteEnum.YES.getKey());
                x.setUpdaterName(currentUser.getName());
                x.setUpdaterId(currentUser.getId());
                x.setUpdateTime(LocalDateTime.now());
                deleteList.add(x);
            }
        });
        boProjectPlanCardService.updateProjectPlanCardAndBuilding(boProjectPlanCard, deleteList, insertList);
        return JsonResultBuilder.success();
    }

    @ApiOperation(value = "删除工规证", httpMethod = "POST", notes = "删除工规证")
    @RequestMapping(value = "/removeProjectPlanCard", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "工规证ID，支持批量删除，一次最多20条，多个以','隔开。", required = true, paramType = "query"),
    })
    public JSONResult removeProjectPlanCard(@NotEmpty(message = "ids不能为空") String[] ids) {
        if (ids.length > 20) {
            return JsonResultBuilder.failed("删除失败，一次最多支持20条数据，本次提交：" + ids.length + "条数据");
        }
        boProjectPlanCardService.removeProjectPlanCard(ids, getCurrentUser());
        return JsonResultBuilder.success();

    }


    @ApiOperation(value = "查询工规证", httpMethod = "POST", notes = "查询工规证")
    @RequestMapping(value = "/getProjectPlanCardList", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "versionId", value = "面积版本ID", required = true, paramType = "query"),
    })
    public JSONResult<List<GetProjectPlanCardListVO>> getProjectPlanCardList(@NotBlank(message = "面积版本ID") String versionId) {
        List<BoProjectPlanCard> projectPlanCardList = boProjectPlanCardService.getProjectPlanCardList(versionId);
        if (CollectionUtils.isEmpty(projectPlanCardList)) {
            return JsonResultBuilder.successWithData(new ArrayList<>(0));
        }
        List<String> refIds = projectPlanCardList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
        List<BoProjectPlanCardBuildingMap> boProjectPlanCardBuildingList = boProjectPlanCardBuildingMapService.getBoProjectPlanCardBuildingList(refIds);

        List<BoBuilding> buildingList = boBuildingService.getBuildingList(versionId);
        Map<String, BoBuilding> boBuildingMap = new HashMap<>();
        buildingList.stream().forEach(x -> boBuildingMap.put(x.getId(), x));

        List<GetProjectPlanCardListVO> result = projectPlanCardList.parallelStream().map(x -> {
            GetProjectPlanCardListVO boProjectPlanCardListVO = new GetProjectPlanCardListVO();
            boProjectPlanCardListVO.setPlanCode(x.getPlanCode());
            boProjectPlanCardListVO.setId(x.getId());
            boProjectPlanCardListVO.setReplyTime(x.getReplyTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
            boProjectPlanCardListVO.setBuildingList(new ArrayList<>());
            boProjectPlanCardBuildingList.stream().forEach(y -> {
                if (x.getId().equals(y.getProjectPlanCardId())) {
                    BoBuilding boBuilding = boBuildingMap.get(y.getBuildingId());
                    if (boBuilding != null) {
                        GetProjectPlanCardListVO.Building building = new GetProjectPlanCardListVO.Building();
                        building.setId(boBuilding.getId());
                        building.setName(boBuilding.getName());
                        boProjectPlanCardListVO.getBuildingList().add(building);
                    }
                }
            });
            List<GetProjectPlanCardListVO.Building> tmp = boProjectPlanCardListVO.getBuildingList();
            if (CollectionUtils.isNotEmpty(tmp)){
                //楼栋排序
                tmp.sort((a, b) -> {
                    String aName = a.getName();
                    String bName = b.getName();
                    Matcher aMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(aName);
                    Integer aNo = null;
                    if (aMatcher.find()) {
                        aNo = Integer.valueOf(aMatcher.group(1));
                    }
                    Matcher bMatcher = RegexConstant.BUILDING_NAME_PATTERN.matcher(bName);
                    Integer bNo = null;
                    if (bMatcher.find()) {
                        bNo = Integer.valueOf(bMatcher.group(1));
                    }
                    if (aNo == null && bNo == null) {
                        return aName.compareTo(bName);
                    }

                    return aNo == null ? 1 : bNo == null ? -1 : aNo - bNo;
                });
            }
            return boProjectPlanCardListVO;
        }).collect(Collectors.toList());
        return JsonResultBuilder.successWithData(result);
    }


    /**
     * 将前端传入的楼栋业态信息 转换为 可供数据库直接操作的数据对象
     *
     * @param versionId
     * @param buildingProductTypeList
     * @param insertBuildingList
     * @param updateBuildingList
     * @param insertBuildingProductTypeList
     * @param insertBuildingProductTypeQuotaList
     * @param updateBuildingProductTypeQuotaList
     * @param deleteBuildingProductTypeList
     */
    private void makePreBuildingProductTypeData2FinalBuildingProductTypeData(String versionId, List<BuildingProductTypeVO> buildingProductTypeList, List<BoBuilding> insertBuildingList, List<BoBuilding> updateBuildingList, List<BoBuildingProductTypeMap> insertBuildingProductTypeList, List<BoBuildingProductTypeQuotaMap> insertBuildingProductTypeQuotaList, List<BoBuildingProductTypeQuotaMap> updateBuildingProductTypeQuotaList, List<BoBuildingProductTypeMap> deleteBuildingProductTypeList) {
        //楼栋业态指标信息增删改，ID为空为增，ID不为空为改，ID在原关系数据列表不存在为删
        List<BoBuildingProductTypeMap> originBuildingProductTypeList = boBuildingProductTypeMapService.getBuildingProductTypeList(versionId);
        Map<String, BoBuildingProductTypeMap> originBuildingProductTypeMap = new HashMap<>();
        originBuildingProductTypeList.stream().forEach(x -> originBuildingProductTypeMap.put(x.getId(), x));

        List<BuildingProductTypeVO> preInsertBuildingProductTypeList = new ArrayList<>();
        List<BoBuildingProductTypeMap> preDeleteBuildingProductTypeList = new ArrayList<>();
        Map<String, BuildingProductTypeVO> preUpdateBuildingProductTypeMap = new HashMap<>();

        buildingProductTypeList.stream().forEach(x -> {
            if (StringUtils.isBlank(x.getId())) {
                preInsertBuildingProductTypeList.add(x);
                return;
            }
            preUpdateBuildingProductTypeMap.put(x.getId(), x);
        });

        originBuildingProductTypeList.stream().forEach(x -> {
            if (!preUpdateBuildingProductTypeMap.containsKey(x.getId())) {
                preDeleteBuildingProductTypeList.add(x);
            }
        });

        CurrentUserVO currentUser = getCurrentUser();
        List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.BUILDING_PRODUCT_TYPE.getKey());
        //用于楼栋名称获取楼栋信息
        Map<String, BoBuilding> insertBuildingMap = new HashMap<>();
        insertBuildingList.stream().forEach(x -> insertBuildingMap.put(x.getName(), x));

        //用于楼栋ID获取楼栋信息
        Map<String, BoBuilding> updateBuildingMap = new HashMap<>();
        updateBuildingList.stream().forEach(x -> updateBuildingMap.put(x.getId(), x));
        if (!preInsertBuildingProductTypeList.isEmpty()) {

            //查询所有已删除的楼栋业态关系，用于获得唯一的楼栋业态ORIGIN_ID(由于推数据使用楼栋+业态代替，故弃用此操作)
            /*List<BoBuildingProductTypeMap> alreadyDeletedBuildingProductTypeList = boBuildingProductTypeMapService.getDeletedBuildingProductTypeList(versionId);
            Map<String, BoBuildingProductTypeMap> alreadyDeletedBuildingProductTypeMap = new HashMap<>();
            makeDeletedBuildingProductTypeQueryMap(alreadyDeletedBuildingProductTypeList, alreadyDeletedBuildingProductTypeMap);*/

            preInsertBuildingProductTypeList.stream().forEach(x -> {
                BoBuildingProductTypeMap boBuildingProductTypeMap = new BoBuildingProductTypeMap();
                List<QuotaBodyVO> quotaList = x.getQuotaList();
                try {
                    PropertyUtils.copyProperties(boBuildingProductTypeMap, x);
                    boBuildingProductTypeMap.setCreateTime(LocalDateTime.now());
                    boBuildingProductTypeMap.setCreaterId(currentUser.getId());
                    boBuildingProductTypeMap.setCreaterName(currentUser.getName());
                    boBuildingProductTypeMap.setId(UUIDUtils.create());
                    boBuildingProductTypeMap.setProjectQuotaExtendId(versionId);
                    //传递的楼栋ID有可能是失效的ID，因此先在insertBuildingMap中，使用名称看看该楼栋是否是个新增的楼栋，如果有则使用新增的楼栋ID
                    BoBuilding insertBuilding = insertBuildingMap.get(x.getBuildingName());
                    BoBuilding boBuilding = insertBuilding == null? updateBuildingMap.get(x.getBuildingId()):insertBuilding;
                    boBuildingProductTypeMap.setBuildingId(boBuilding.getId());
                    boBuildingProductTypeMap.setBuildingOriginId(boBuilding.getOriginId());

                    /*//0存储产权属性code+value，1存储精装code+value，2存储层高code+value
                    String[] limitCodeStrArr = new String[3];
                    for (QuotaBodyVO quotaBodyVO : quotaList) {
                        if (QuotaCodeEnum.PROPERTY_RIGHT.getKey().equals(quotaBodyVO.getQuotaCode())) {
                            limitCodeStrArr[0] = quotaBodyVO.getQuotaCode() + quotaBodyVO.getQuotaValue();
                            continue;
                        }
                        if (QuotaCodeEnum.REFINED_DECORATION.getKey().equals(quotaBodyVO.getQuotaCode())) {
                            limitCodeStrArr[1] = quotaBodyVO.getQuotaCode() + quotaBodyVO.getQuotaValue();
                            continue;
                        }
                        if (QuotaCodeEnum.FLOOR_HIGH.getKey().equals(quotaBodyVO.getQuotaCode())) {
                            limitCodeStrArr[2] = quotaBodyVO.getQuotaCode() + quotaBodyVO.getQuotaValue();
                            continue;
                        }
                    }
                    String key = boBuilding.getOriginId() + x.getProductTypeCode() + limitCodeStrArr[0] + limitCodeStrArr[1] + limitCodeStrArr[2];
                    BoBuildingProductTypeMap deletedBuildingProductType = alreadyDeletedBuildingProductTypeMap.get(key);
                    boBuildingProductTypeMap.setOriginId(deletedBuildingProductType == null ? UUIDUtils.create() : deletedBuildingProductType.getOriginId());*/
                } catch (Exception e) {
                    logger.error(e);
                }
                insertBuildingProductTypeList.add(boBuildingProductTypeMap);

                List<BoBuildingProductTypeQuotaMap> boBuildingProductTypeQuotaMapList = quotaGroupMapList.parallelStream().map(y -> {
                    BoBuildingProductTypeQuotaMap boBuildingProductTypeQuotaMap = new BoBuildingProductTypeQuotaMap();
                    boBuildingProductTypeQuotaMap.setId(UUIDUtils.create());
                    boBuildingProductTypeQuotaMap.setCreaterName(currentUser.getName());
                    boBuildingProductTypeQuotaMap.setCreaterId(currentUser.getId());
                    boBuildingProductTypeQuotaMap.setBuildingProductTypeId(boBuildingProductTypeMap.getId());
                    boBuildingProductTypeQuotaMap.setCreateTime(LocalDateTime.now());
                    boBuildingProductTypeQuotaMap.setQuotaCode(y.getCode());
                    boBuildingProductTypeQuotaMap.setQuotaGroupMapId(y.getId());
                    boBuildingProductTypeQuotaMap.setQuotaId(y.getQuotaId());
                    return boBuildingProductTypeQuotaMap;
                }).collect(Collectors.toList());


                boBuildingProductTypeQuotaMapList.parallelStream().forEach(y -> {
                    quotaList.parallelStream().forEach(z -> {
                        if (y.getQuotaCode().equals(z.getQuotaCode())) {
                            y.setQuotaValue(z.getQuotaValue());
                        }
                    });
                });

                insertBuildingProductTypeQuotaList.addAll(boBuildingProductTypeQuotaMapList);
            });
        }

        if (!preDeleteBuildingProductTypeList.isEmpty()) {
            preDeleteBuildingProductTypeList.parallelStream().forEach(x -> {
                x.setUpdaterId(currentUser.getId());
                x.setUpdaterName(currentUser.getName());
                x.setUpdateTime(LocalDateTime.now());
                x.setIsDelete(IsDeleteEnum.YES.getKey());
            });
            deleteBuildingProductTypeList.addAll(preDeleteBuildingProductTypeList);
        }

        //因为楼栋名称/ID/业态CODE/业态名称不会更新，因此可忽略对楼栋业态信息的更新
        /*if (!preUpdateBuildingProductTypeMap.isEmpty()) {

            *//*preUpdateBuildingProductTypeMap.forEach((k, v) -> {
                BoBuildingProductTypeMap boBuildingProductTypeMap = new BoBuildingProductTypeMap();
                try {
                    PropertyUtils.copyProperties(boBuildingProductTypeMap, v);
                    boBuildingProductTypeMap.setUpdaterId(currentUser.getId());
                    boBuildingProductTypeMap.setUpdaterName(currentUser.getName());
                    boBuildingProductTypeMap.setUpdateTime(LocalDateTime.now());
                    updateBuildingProductTypeList.add(boBuildingProductTypeMap);
                } catch (Exception e) {
                    logger.error(e);
                }
            });*//*

           *//* List<String> buildingProductTypeIdList = preUpdateBuildingProductTypeMap.values().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<BoBuildingProductTypeQuotaMap> buildingProductTypeQuotaList = boBuildingProductTypeQuotaMapService.getBuildingProductTypeQuotaList(buildingProductTypeIdList);
            buildingProductTypeQuotaList.stream().forEach(x -> {
                BuildingProductTypeVO buildingProductTypeVO = preUpdateBuildingProductTypeMap.get(x.getBuildingProductTypeId());
                List<QuotaBodyVO> quotaList = buildingProductTypeVO.getQuotaList();
                for (QuotaBodyVO quotaBodyVO : quotaList) {
                    if (x.getQuotaCode().equals(quotaBodyVO.getQuotaCode())) {
                        x.setQuotaValue(quotaBodyVO.getQuotaValue());
                        x.setUpdaterId(currentUser.getId());
                        x.setUpdaterName(currentUser.getName());
                        x.setUpdateTime(LocalDateTime.now());
                        updateBuildingProductTypeQuotaList.add(x);
                        return;
                    }
                }
            });*//*
        }*/
    }

    /**
     * 将前端传入的楼栋信息 转换为 可供数据库直接操作的数据对象
     *
     * @param versionId
     * @param buildingList
     * @param insertBuildingList
     * @param insertBuildingQuotaList
     * @param updateBuildingList
     * @param updateBuildingQuotaList
     * @param deleteBuildingList
     */
    private void makePreBuildingData2FinalBuildingData(String versionId, List<BuildingVO> buildingList, List<BoBuilding> insertBuildingList, List<BoBuildingQuotaMap> insertBuildingQuotaList, List<BoBuilding> updateBuildingList, List<BoBuildingQuotaMap> updateBuildingQuotaList, List<BoBuilding> deleteBuildingList) {
        //增删改楼栋，ID不为空且在元数据列表则为改，否则为增，为空则为增，在原数据列表不存在则为删除
        //原数据列表
        List<BoBuilding> originBuildingList = boBuildingService.getBuildingList(versionId);
        Map<String, BoBuilding> originBuildingMap = new HashMap<>();
        originBuildingList.stream().forEach(x -> originBuildingMap.put(x.getId(), x));

        List<BuildingVO> preInsertBuildingList = new ArrayList<>();
        List<BoBuilding> preDeleteBuildingList = new ArrayList<>();
        Map<String, BuildingVO> preUpdateBuildingMap = new HashMap<>();

        buildingList.stream().forEach(x -> {
            if (StringUtils.isBlank(x.getId())) {
                preInsertBuildingList.add(x);
                return;
            }
            if (originBuildingMap.containsKey(x.getId())){
                preUpdateBuildingMap.put(x.getId(), x);
            }else {
                x.setId(null);
                preInsertBuildingList.add(x);
            }
        });

        originBuildingList.stream().forEach(x -> {
            if (!preUpdateBuildingMap.containsKey(x.getId())) {
                preDeleteBuildingList.add(x);
            }
        });

        CurrentUserVO currentUser = getCurrentUser();
        List<BoQuotaGroupMap> quotaGroupMapList = boQuotaGroupMapService.getQuotaGroupMapList(QuotaGroupCodeEnum.BUILDING.getKey());
        if (!preInsertBuildingList.isEmpty()) {
            //为了根据名称判断，如果历史版本中有使用过这个楼栋，则需要设置ORIGIN_ID与此删除的楼栋相同
            Map<String, BoBuilding> historyBuildingMap = boBuildingService.getAllBuildingNameMap(versionId, preInsertBuildingList.stream().map(x -> x.getName()).collect(Collectors.toSet()));

            preInsertBuildingList.stream().forEach(x -> {
                BoBuilding boBuilding = new BoBuilding();
                boBuilding.setName(x.getName());
                boBuilding.setCreateTime(LocalDateTime.now());
                boBuilding.setCreaterId(currentUser.getId());
                boBuilding.setCreaterName(currentUser.getName());
                boBuilding.setId(UUIDUtils.create());
                boBuilding.setProjectQuotaExtendId(versionId);
                boBuilding.setProjectLandPartName(x.getProjectLandPartName());
                boBuilding.setProjectLandPartId(x.getProjectLandPartId());
                BoBuilding historyBuilding = historyBuildingMap.get(x.getName());
                boBuilding.setOriginId(historyBuilding == null ? UUIDUtils.create() : historyBuilding.getOriginId());
                insertBuildingList.add(boBuilding);

                List<BoBuildingQuotaMap> boBuildingQuotaMapList = quotaGroupMapList.parallelStream().map(y -> {
                    BoBuildingQuotaMap boBuildingQuotaMap = new BoBuildingQuotaMap();
                    boBuildingQuotaMap.setId(UUIDUtils.create());
                    boBuildingQuotaMap.setCreaterName(currentUser.getName());
                    boBuildingQuotaMap.setCreaterId(currentUser.getId());
                    boBuildingQuotaMap.setBuildingId(boBuilding.getId());
                    boBuildingQuotaMap.setCreateTime(LocalDateTime.now());
                    boBuildingQuotaMap.setQuotaCode(y.getCode());
                    boBuildingQuotaMap.setQuotaGroupMapId(y.getId());
                    boBuildingQuotaMap.setQuotaId(y.getQuotaId());
                    BusinessObjectUtils.quotaValueSetDefaultForObject(y.getValueType(),boBuildingQuotaMap);
                    return boBuildingQuotaMap;
                }).collect(Collectors.toList());

                /*List<QuotaBodyVO> quotaList = x.getQuotaList();
                boBuildingQuotaMapList.parallelStream().forEach(y -> {
                    quotaList.parallelStream().forEach(z -> {
                        if (y.getQuotaCode().equals(z.getQuotaCode())) {
                            y.setQuotaValue(z.getQuotaValue());
                        }
                    });
                });*/

                insertBuildingQuotaList.addAll(boBuildingQuotaMapList);
            });
        }

        if (!preDeleteBuildingList.isEmpty()) {
            preDeleteBuildingList.parallelStream().forEach(x -> {
                x.setUpdaterId(currentUser.getId());
                x.setUpdaterName(currentUser.getName());
                x.setUpdateTime(LocalDateTime.now());
                x.setIsDelete(IsDeleteEnum.YES.getKey());
            });
            deleteBuildingList.addAll(preDeleteBuildingList);
        }

        //去掉此内容，业态维护只有新增删除，修改去掉，2019-7-1 18:10:42
        /*if (!preUpdateBuildingMap.isEmpty()) {
            preUpdateBuildingMap.forEach((k, v) -> {
                BoBuilding boBuilding = new BoBuilding();
                try {
                    PropertyUtils.copyProperties(boBuilding, v);
                    boBuilding.setUpdaterId(currentUser.getId());
                    boBuilding.setUpdaterName(currentUser.getName());
                    boBuilding.setUpdateTime(LocalDateTime.now());
                    updateBuildingList.add(boBuilding);
                } catch (Exception e) {
                    logger.error(e);
                }
            });


            List<String> buildingIdList = updateBuildingList.parallelStream().map(x -> x.getId()).collect(Collectors.toList());
            List<BoBuildingQuotaMap> buildingQuotaList = boBuildingQuotaMapService.getBuildingQuotaList(buildingIdList);
            buildingQuotaList.stream().forEach(x -> {
                BuildingVO buildingVO = preUpdateBuildingMap.get(x.getBuildingId());
                List<QuotaBodyVO> quotaList = buildingVO.getQuotaList();
                for (QuotaBodyVO quotaBodyVO : quotaList) {
                    if (x.getQuotaCode().equals(quotaBodyVO.getQuotaCode())) {
                        x.setQuotaValue(quotaBodyVO.getQuotaValue());
                        x.setUpdaterId(currentUser.getId());
                        x.setUpdaterName(currentUser.getName());
                        x.setUpdateTime(LocalDateTime.now());
                        updateBuildingQuotaList.add(x);
                        return;
                    }
                }
            });

        }*/
    }


}
