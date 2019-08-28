package com.tahoecn.bo.controller.webapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.schedule.SyncTutouTask;
import com.tahoecn.core.json.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "土投API", value = "土投API")
@RestController
@RequestMapping(value = "/api/tutou")
public class TutouThSynchApi {

	@Autowired 
	private SyncTutouTask syncTutouTask;
	
	private static final Logger logger = LoggerFactory.getLogger(UcSynchApi.class);
	
	/**
	 * @Title: getLandInfoListDS 
	 * @Description: 同步土投地块信息
	 * @param landId
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午1:54:55 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步土投地块信息", notes = "手动同步土投地块信息")
	@RequestMapping(value = "/getLandInfoListDS", method = RequestMethod.POST)
	public JSONResult getLandInfoListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getLandInfoListDS START");
		syncTutouTask.getLandInfoListDS();
		logger.info("getLandInfoListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getLandareaListDS 
	 * @Description: 同步土投地块面积信息列表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午4:52:59 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步土投地块面积信息列表", notes = "手动同步土投地块面积信息列表")
	@RequestMapping(value = "/getLandareaListDS", method = RequestMethod.POST)
	public JSONResult getLandareaListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getLandareaListDS START");
		syncTutouTask.getLandareaListDS();
		logger.info("getLandareaListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getLandareaNewListDS 
	 * @Description: 同步土投地块面积New信息列表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午4:54:48 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步土投地块面积New信息列表", notes = "手动同步土投地块面积New信息列表")
	@RequestMapping(value = "/getLandareaNewListDS", method = RequestMethod.POST)
	public JSONResult getLandareaNewListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getLandareaNewListDS START");
		syncTutouTask.getLandareaNewListDS();
		logger.info("getLandareaNewListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getLanduseListDS 
	 * @Description: 同步土地性质表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午5:00:27 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步土地性质表", notes = "手动同步土地性质表")
	@RequestMapping(value = "/getLanduseListDS", method = RequestMethod.POST)
	public JSONResult getLanduseListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getLanduseListDS START");
		syncTutouTask.getLanduseListDS();
		logger.info("getLanduseListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getProductPositListDS 
	 * @Description: 同步产品定位信息表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午4:57:21 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步产品定位信息表", notes = "手动同步产品定位信息表")
	@RequestMapping(value = "/getProductPositListDS", method = RequestMethod.POST)
	public JSONResult getProductPositListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getProductPositListDS START");
		syncTutouTask.getProductPositListDS();
		logger.info("getProductPositListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getStateListDS 
	 * @Description: 同步投委会状态码表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午4:57:39 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步投委会状态码表", notes = "手动同步投委会状态码表")
	@RequestMapping(value = "/getStateListDS", method = RequestMethod.POST)
	public JSONResult getStateListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getStateListDS START");
		syncTutouTask.getStateListDS();
		logger.info("getStateListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getBudgetListDS 
	 * @Description: 同步测算对比表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午5:00:42 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步测算对比表", notes = "手动同步测算对比表")
	@RequestMapping(value = "/getBudgetListDS", method = RequestMethod.POST)
	public JSONResult getBudgetListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getBudgetListDS START");
		syncTutouTask.getBudgetListDS();
		logger.info("getBudgetListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getCooperationMoodListDS 
	 * @Description: 同步合作方式表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午5:00:59 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步合作方式表", notes = "手动同步合作方式表")
	@RequestMapping(value = "/getCooperationMoodListDS", method = RequestMethod.POST)
	public JSONResult getCooperationMoodListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getCooperationMoodListDS START");
		syncTutouTask.getCooperationMoodListDS();
		logger.info("getCooperationMoodListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getBusinessInfoListDS 
	 * @Description: 同步商务信息表
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午5:01:33 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "手动同步商务信息表", notes = "手动同步商务信息表")
	@RequestMapping(value = "/getBusinessInfoListDS", method = RequestMethod.POST)
	public JSONResult getBusinessInfoListDS() {
		JSONResult jsonResult = new JSONResult();
		logger.info("getBusinessInfoListDS START");
		syncTutouTask.getBusinessInfoListDS();
		logger.info("getBusinessInfoListDS END");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
}
