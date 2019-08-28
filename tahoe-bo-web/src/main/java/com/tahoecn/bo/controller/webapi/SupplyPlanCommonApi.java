/**
 * 项目名：泰禾资产
 * 包名：com.tahoecn.controller.common
 * 文件名：BpmReceiveApi.java
 * 版本信息：1.0.0
 * 日期：2018年10月16日-上午10:49:09
 * Copyright (c) 2018 Pactera 版权所有
 */
package com.tahoecn.bo.controller.webapi;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.constants.RedisConstants;
import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.enums.LevelTypeEnum;
import com.tahoecn.bo.common.enums.StageCodeEnum;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.common.utils.JsonResultBuilder;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.entity.BoProjectPriceExtend;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.model.entity.BoProjectSupplyPlanVersion;
import com.tahoecn.bo.model.entity.MdmProjectInfo;
import com.tahoecn.bo.model.vo.SupplyPlanBuidingProductTypeVo;
import com.tahoecn.bo.model.vo.SupplyPlanLandProductTypeVo;
import com.tahoecn.bo.model.vo.SupplyVersionNumVo;
import com.tahoecn.bo.model.vo.UpdateSupplyPlanBuidingProductTypeParam;
import com.tahoecn.bo.model.vo.UpdateSupplyPlanLandProductTypeParam;
import com.tahoecn.bo.service.BoProjectPriceExtendService;
import com.tahoecn.bo.service.BoProjectQuotaExtendService;
import com.tahoecn.bo.service.BoProjectSupplyPlanVersionService;
import com.tahoecn.bo.service.MdmProjectInfoService;
import com.tahoecn.core.json.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName：SupplyPlanCommonApi
 * @Description：供货计划API
 * @author liyongxu 
 * @date 2019年8月9日 上午9:33:16 
 * @version 1.0.0 
 */
@Api(tags = "供货计划API", value = "供货计划API")
@RestController
@RequestMapping(value = "/api/supply")
public class SupplyPlanCommonApi extends TahoeBaseController{
	
	@Autowired
	private BoProjectSupplyPlanVersionService boProjectSupplyPlanVersionService;
	
	@Autowired
	private BoProjectPriceExtendService boProjectPriceExtendService;
	
	@Autowired
	private BoProjectQuotaExtendService boProjectQuotaExtendService;
	
	@Autowired
    private MdmProjectInfoService mdmProjectInfoService;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * @Title: getVersionNumByProjectId 
	 * @Description: 根据项目/分期id获取供货版本号
	 * @param projectId
	 * @return JSONResult<SupplyVersionNumVo>
	 * @author liyongxu
	 * @date 2019年8月9日 上午10:00:38 
	*/
	@ApiOperation(value = "根据项目/分期id获取供货版本号", notes = "根据项目/分期id获取供货版本号")
    @RequestMapping(value = "/getVersionNumByProjectId", method = RequestMethod.POST)
	public JSONResult<List<SupplyVersionNumVo>> getVersionNumByProjectId(@ApiParam(name = "projectId", value = "项目/分期Id", required = true) @RequestParam(value = "projectId", defaultValue = "") String projectId,
			@ApiParam(name = "stageCode", value = "阶段CODE", required = true) @RequestParam(value = "stageCode", defaultValue = "") String stageCode,
			@ApiParam(name = "planType", value = "计划类型（月度计划：Month，年度计划：Year）", required = true) @RequestParam(value = "planType", defaultValue = "") String planType) {
		JSONResult<List<SupplyVersionNumVo>> jsonResult = new JSONResult<List<SupplyVersionNumVo>>();
		QueryWrapper<BoProjectSupplyPlanVersion> projectSupplyPlanVersionWrapper = new QueryWrapper<>();
		projectSupplyPlanVersionWrapper.eq("projectId", projectId)
			.eq("plan_type", planType)
			.eq("stage_code", stageCode)
        	.eq("is_delete", IsDeleteEnum.NO.getKey())
        	.eq("is_disable", IsDisableEnum.NO.getKey())
        	.orderByDesc("create_time");
		List<BoProjectSupplyPlanVersion> projectSupplyPlanVersion = boProjectSupplyPlanVersionService.list(projectSupplyPlanVersionWrapper);
		List<SupplyVersionNumVo> supplyVersionNumList = new ArrayList<SupplyVersionNumVo>();
		for (BoProjectSupplyPlanVersion boProjectSupplyPlanVersion : projectSupplyPlanVersion) {
			SupplyVersionNumVo supplyVersionNumVo = new SupplyVersionNumVo();
	        supplyVersionNumVo.setVersionId(boProjectSupplyPlanVersion.getId());
	        supplyVersionNumVo.setVersionName(boProjectSupplyPlanVersion.getVersion());
	        supplyVersionNumVo.setVersionStatus(boProjectSupplyPlanVersion.getVersionStatus());
	        supplyVersionNumVo.setVersionDate(boProjectSupplyPlanVersion.getCreateTime().toString());
	        supplyVersionNumVo.setVersionProcessId(boProjectSupplyPlanVersion.getApproveId());
	        supplyVersionNumVo.setVersionStatusDesc(VersionStatusEnum.getValueByKey(boProjectSupplyPlanVersion.getVersionStatus()));
	        //查询价格信息
	        QueryWrapper<BoProjectPriceExtend> wrapper = new QueryWrapper<>();
			wrapper.eq("id", boProjectSupplyPlanVersion.getProjectPriceExtendId())
	        	.eq("is_delete", IsDeleteEnum.NO.getKey())
	        	.eq("is_disable", IsDisableEnum.NO.getKey());
			BoProjectPriceExtend boProjectPriceExtend = boProjectPriceExtendService.getOne(wrapper);
			DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_TEXT);
			String priceStartTime = df.format(boProjectPriceExtend.getApproveStartTime());
			String priceEndTime = df.format(boProjectPriceExtend.getApproveEndTime());
			supplyVersionNumVo.setPriceStartTime(priceStartTime);
			supplyVersionNumVo.setPriceEndTime(priceEndTime);
			supplyVersionNumVo.setPriceStageName(boProjectPriceExtend.getStageName());
			supplyVersionNumVo.setPriceVersion(boProjectPriceExtend.getVersion());
			//查询面积信息
			BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getAreaVersionInfos(boProjectSupplyPlanVersion.getProjectPriceExtendId());
			String areaStartTime = df.format(boProjectQuotaExtend.getApproveStartTime());
			String areaEndTime = df.format(boProjectQuotaExtend.getApproveEndTime());
			supplyVersionNumVo.setAreaStartTime(areaStartTime);
			supplyVersionNumVo.setAreaEndTime(areaEndTime);
			supplyVersionNumVo.setAreaStageName(boProjectQuotaExtend.getStageName());
			supplyVersionNumVo.setAreaVersion(boProjectQuotaExtend.getVersion());
	        supplyVersionNumList.add(supplyVersionNumVo);
		}
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		jsonResult.setData(supplyVersionNumList);
		return jsonResult;
	}
	
	/**
	 * @Title: createSupplyVersion 
	 * @Description: 创建供货计划新版本
	 * @param projectId
	 * @param stageCode
	 * @return JSONResult<VersionVO>
	 * @author liyongxu
	 * @date 2019年8月9日 上午10:13:38 
	*/
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "创建供货计划新版本", notes = "创建供货计划新版本")
    @RequestMapping(value = "/createSupplyVersion", method = {RequestMethod.POST})
    public JSONResult<SupplyVersionNumVo> createSupplyVersion(
    		@ApiParam(name = "projectId", value = "投决会阶段传项目ID，其他阶段传分期ID", required = true) @RequestParam(value = "projectId", defaultValue = "") String projectId,
    		@ApiParam(name = "stageCode", value = "阶段CODE", required = true) @RequestParam(value = "stageCode", defaultValue = "") String stageCode,
			@ApiParam(name = "planType", value = "计划类型（月度计划：Month，年度计划：Year）", required = true) @RequestParam(value = "planType", defaultValue = "") String planType) {
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

        String key = RedisConstants.SUPPLY_CREATE_VERSION_LOCK + projectId + stageCode;
        //禁止重复创建
        if (!redisTemplate.opsForValue().setIfAbsent(key, 1)) {
            return JsonResultBuilder.failed("系统繁忙");
        }
        try {
            //判断是否存在编制中、审批中、审批驳回的数据
            if (boProjectSupplyPlanVersionService.hasCanEditData(projectId, stageCode)) {
                return JsonResultBuilder.failed("当前阶段下存在编制中、审批中或已驳回的数据，禁止创建新版本。");
            }
        	BoProjectSupplyPlanVersion newVersion = boProjectSupplyPlanVersionService.createVersion(projectId,stageCode,planType);
            SupplyVersionNumVo supplyVersionNumVo = new SupplyVersionNumVo();
            supplyVersionNumVo.setVersionId(newVersion.getId());
            supplyVersionNumVo.setVersionStatus(newVersion.getVersionStatus());
            supplyVersionNumVo.setVersionName(newVersion.getVersion());
            supplyVersionNumVo.setVersionStatusDesc(VersionStatusEnum.getValueByKey(newVersion.getVersionStatus()));
            return JsonResultBuilder.successWithData(supplyVersionNumVo);
        } finally {
            redisTemplate.delete(key);
        }
    }

	/**
	 * @Title: getSupplyPlanTypeInfo 
	 * @Description: 获取供货计划阶段信息
	 * @param versionId
	 * @return JSONResult<Map<String,Object>>
	 * @author liyongxu
	 * @date 2019年8月9日 下午3:23:10 
	*/
	@ApiOperation(value = "获取供货计划阶段信息", notes = "获取供货计划阶段信息")
    @RequestMapping(value = "/getSupplyPlanTypeInfo", method = {RequestMethod.POST})
    public JSONResult<Map<String, Object>> getSupplyPlanTypeInfo(
			@ApiParam(name = "versionId", value = "版本Id", required = false) @RequestParam(value = "versionId", defaultValue = "") String versionId) {
		JSONResult<Map<String, Object>> jsonResult = new JSONResult<Map<String, Object>>();
		QueryWrapper<BoProjectSupplyPlanVersion> projectSupplyPlanVersionWrapper = new QueryWrapper<>();
		projectSupplyPlanVersionWrapper.eq("id", versionId)
        	.eq("is_delete", IsDeleteEnum.NO.getKey())
        	.eq("is_disable", IsDisableEnum.NO.getKey());
		BoProjectSupplyPlanVersion projectSupplyPlanVersion = boProjectSupplyPlanVersionService.getOne(projectSupplyPlanVersionWrapper);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stageCode", projectSupplyPlanVersion.getStageCode());
		jsonResult.setData(map);
		return jsonResult;
    }
	
	/**
	 * @Title: getSupplyPlanLandProductTypeList 
	 * @Description: 获取地块业态版供货计划列表数据
	 * @param versionId
	 * @return JSONResult<BoProjectSupplyPlanVersion>
	 * @author liyongxu
	 * @date 2019年8月9日 上午11:14:58 
	*/
	@ApiOperation(value = "获取地块业态版供货计划列表数据", notes = "获取地块业态版供货计划列表数据")
    @RequestMapping(value = "/getSupplyPlanLandProductTypeList", method = {RequestMethod.POST})
    public JSONResult<SupplyPlanLandProductTypeVo> getSupplyPlanLandProductTypeList(
			@ApiParam(name = "versionId", value = "版本Id", required = true) @RequestParam(value = "versionId", defaultValue = "") String versionId) {
		JSONResult<SupplyPlanLandProductTypeVo> jsonResult = new JSONResult<SupplyPlanLandProductTypeVo>();
		QueryWrapper<BoProjectSupplyPlanVersion> projectSupplyPlanVersionWrapper = new QueryWrapper<>();
		projectSupplyPlanVersionWrapper.eq("id", versionId)
        	.eq("is_delete", IsDeleteEnum.NO.getKey())
        	.eq("is_disable", IsDisableEnum.NO.getKey());
		BoProjectSupplyPlanVersion projectSupplyPlanVersion = boProjectSupplyPlanVersionService.getOne(projectSupplyPlanVersionWrapper);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
//		jsonResult.setData(projectSupplyPlanVersion);
		return jsonResult;
    }
	
	/**
	 * @Title: getSupplyPlanBuidingProductTypeList 
	 * @Description: 获取楼栋业态版供货计划列表数据
	 * @param versionId
	 * @return JSONResult<SupplyPlanBuidingProductTypeVo>
	 * @author liyongxu
	 * @date 2019年8月9日 下午3:17:26 
	*/
	@ApiOperation(value = "获取楼栋业态版供货计划列表数据", notes = "获取楼栋业态版供货计划列表数据")
    @RequestMapping(value = "/getSupplyPlanBuidingProductTypeList", method = {RequestMethod.POST})
    public JSONResult<SupplyPlanBuidingProductTypeVo> getSupplyPlanBuidingProductTypeList(
			@ApiParam(name = "versionId", value = "版本Id", required = true) @RequestParam(value = "versionId", defaultValue = "") String versionId) {
		JSONResult<SupplyPlanBuidingProductTypeVo> jsonResult = new JSONResult<SupplyPlanBuidingProductTypeVo>();
		QueryWrapper<BoProjectSupplyPlanVersion> projectSupplyPlanVersionWrapper = new QueryWrapper<>();
		projectSupplyPlanVersionWrapper.eq("id", versionId)
        	.eq("is_delete", IsDeleteEnum.NO.getKey())
        	.eq("is_disable", IsDisableEnum.NO.getKey());
		BoProjectSupplyPlanVersion projectSupplyPlanVersion = boProjectSupplyPlanVersionService.getOne(projectSupplyPlanVersionWrapper);
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
//		jsonResult.setData(projectSupplyPlanVersion);
		return jsonResult;
    }
	
	/**
	 * @Title: updateSupplyPlanLandProductType
	 * @Description: 更新供货计划地块业态版数据
	 * @param versionId
	 * @return JSONResult<BoProjectSupplyPlanVersion>
	 * @author liyongxu
	 * @date 2019年8月9日 上午11:16:23 
	*/
	@ApiOperation(value = "更新供货计划地块业态版数据", notes = "更新供货计划地块业态版数据")
    @RequestMapping(value = "/updateSupplyPlanLandProductType", method = {RequestMethod.POST})
    public JSONResult updateSupplyPlanLandProductType(@RequestBody UpdateSupplyPlanLandProductTypeParam updateSupplyPlanLandProductTypeParam) {
		JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
    }
	
	/**
	 * @Title: updateSupplyPlanBuidingProductType 
	 * @Description: 更新供货计划楼栋业态版数据
	 * @param versionId
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年8月9日 下午3:25:34 
	*/
	@ApiOperation(value = "更新供货计划楼栋业态版数据", notes = "更新供货计划楼栋业态版数据")
    @RequestMapping(value = "/updateSupplyPlanBuidingProductType", method = {RequestMethod.POST})
    public JSONResult updateSupplyPlanBuidingProductType(@RequestBody UpdateSupplyPlanBuidingProductTypeParam updateSupplyPlanBuidingProductTypeParam) {
		JSONResult jsonResult = new JSONResult();
        jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
    }
}
