package com.tahoecn.bo.controller.webapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.schedule.SyncUcTask;
import com.tahoecn.core.json.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "UcAPI", value = "UcAPI")
@RestController
@RequestMapping(value = "/api/uc")
public class UcSynchApi {

	@Autowired 
	private SyncUcTask syncUcTask;
	
	private static final Logger logger = LoggerFactory.getLogger(UcSynchApi.class);
	
	/**
	 * @Title: getUcOrgData 
	 * @Description: 手动同步UcOrg数据
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午3:39:13 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "UcOrg数据信息", notes = "UcOrg数据信息")
	@RequestMapping(value = "/getUcOrgData", method = RequestMethod.POST)
	public JSONResult getUcOrgData() {
		JSONResult jsonResult = new JSONResult();
		logger.info("syncUcOrg START");
		syncUcTask.syncUcOrgData();
		logger.info("syncUcOrg End");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getUcUserData 
	 * @Description: 手动同步UcUser数据
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月24日 下午3:38:58 
	*/
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "UcUser数据信息", notes = "UcUser数据信息")
	@RequestMapping(value = "/getUcUserData", method = RequestMethod.POST)
	public JSONResult getUcUserData() {
		JSONResult jsonResult = new JSONResult();
		logger.info("syncUcUser START");
		syncUcTask.syncUcUserData();
		logger.info("syncUcUser End");
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
}
