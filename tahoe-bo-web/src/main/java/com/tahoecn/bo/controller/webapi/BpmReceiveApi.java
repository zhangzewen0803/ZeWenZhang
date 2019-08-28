/**
 * 项目名：泰禾资产
 * 包名：com.tahoecn.controller.common
 * 文件名：BpmReceiveApi.java
 * 版本信息：1.0.0
 * 日期：2018年10月16日-上午10:49:09
 * Copyright (c) 2018 Pactera 版权所有
 */
package com.tahoecn.bo.controller.webapi;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.model.dto.BoPushProjectSubDto;
import com.tahoecn.bo.model.entity.BoApproveCallbackLog;
import com.tahoecn.bo.model.entity.BoApproveRecord;
import com.tahoecn.bo.service.BoApproveCallbackLogService;
import com.tahoecn.bo.service.BoApproveRecordService;
import com.tahoecn.bo.service.BoBuildingProductTypeMapService;
import com.tahoecn.bo.service.SalePushBuildingDataService;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.uc.sso.annotation.Action;
import com.tahoecn.uc.sso.annotation.Login;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName：BpmReceiveApi
 * @Description：投模流程Api 
 * @author liyongxu 
 * @date 2019年6月1日 下午5:29:30 
 * @version 1.0.0 
 */
@Api(tags = "Bpm回调Api", value = "Bpm回调Api")
@RestController
@RequestMapping("/bpm/api")
public class BpmReceiveApi{
	
	@Autowired
	private BoApproveRecordService boApproveRecordService;
	
	@Autowired
	private BoApproveCallbackLogService boApproveCallbackLogService;
	
	@Autowired
	private BoBuildingProductTypeMapService boBuildingProductTypeMapService;
	
	@Autowired
	private SalePushBuildingDataService salePushBuildingDataService;
	
    private static final Logger logger = LoggerFactory.getLogger(BpmReceiveApi.class);
    
    private static final String NO_REJECT_FLAG = "0";
    
	/**
	 * @Title: handleProcessInfo 
	 * @Description: 处理流程返回信息
	 * @param request
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年6月3日 上午9:43:52 
	*/
	@Login(action = Action.Skip)
	@RequestMapping(value = "/receive", method = RequestMethod.GET)
	public JSONResult<String> handleProcessInfo(HttpServletRequest request) {
		JSONResult<String> jsonResult = new JSONResult<String>();
		String wfId = request.getParameter("wfId");
		String wfStatus = request.getParameter("wfStatus");
		String channel = request.getParameter("channel");
		String oaBackTyep = request.getParameter("backType");//返回类型
		logger.info("backType:" + oaBackTyep);
		String backtype = NO_REJECT_FLAG;
		if(StrUtils.isNotEmpty(oaBackTyep)) {
			backtype = oaBackTyep;
		}
		Long timestamp = Long.parseLong(request.getParameter("timestamp"));
		logger.info("##### BpmReceiveApi Call-Back Success !" + "---wfId ---"+ wfId + "---wfStatus---"+ wfStatus + "---timestamp---" + timestamp);
		BoApproveRecord boApproveRecord = boApproveRecordService.getProcessInfo(wfId);
		logger.info("boApproveRecord:" + boApproveRecord);
		String orderType = boApproveRecord.getRefTable();
		if(StrUtils.isNotEmpty(orderType)) {
			boApproveRecordService.updateApproveRecordByApproveId(orderType,wfId, wfStatus, backtype);
			this.saveProcessCallbackLog(wfId, wfStatus, timestamp, channel);
			if(orderType.equals(Constants.PROCESS_STATUS_AREA)) {
				if(wfStatus.equals("30") || wfStatus.equals("31")) {
					jsonResult = this.pushBuildingData(wfId);
				}
			}
		}
		jsonResult.setData(wfId);
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
 	}
	
	/**
	 * @Title: pushBuildingData 
	 * @Description: 定时推送楼栋业态到营销系统
	 * @param approveId void
	 * @author liyongxu
	 * @date 2019年6月11日 下午4:19:42 
	*/
	@Login(action = Action.Skip)
	@ApiOperation(value = "定时推送楼栋业态到营销系统", notes = "定时推送楼栋业态到营销系统")
	@RequestMapping(value = "/pushBuildingData", method = RequestMethod.POST)
    public JSONResult<String> pushBuildingData(@ApiParam(name = "approveId", value = "流程Id", required = true) @RequestParam(value = "approveId", required = false) String approveId) {
		JSONResult<String> JSONResult = new JSONResult<String>();
    	BoPushProjectSubDto boProjectSubInfoList = boBuildingProductTypeMapService.getBuildingProductInfoListByApproveId(approveId);
    	logger.info("boProjectSubInfoList-BuildingList SIZE:" + boProjectSubInfoList.getBuildingList().size());
    	if(DataUtils.isNotEmpty(boProjectSubInfoList.getBuildingList())) {
    		String josnStr = salePushBuildingDataService.pushBuildingData(boProjectSubInfoList.toString());
    		JSONObject josnObj = JSONObject.parseObject(josnStr);
    		JSONResult.setCode(josnObj.getInteger("code"));
    		JSONResult.setMsg(josnObj.getString("msg"));
    		if(josnObj.get("data") != null) {
    			JSONResult.setData(josnObj.getString("data"));
    		}
    	}else {
    		JSONResult.setCode(CodeEnum.ERROR.getKey());
    		JSONResult.setMsg("推送楼栋业态数据为空！");
		}
		return JSONResult;
    }
	
	/**
	 * @Title: saveProcessCallbackLog 
	 * @Description: 保存回调日志
	 * @param wfId
	 * @param wfStatus
	 * @param timestamp
	 * @param channel
	 * @param backtype void
	 * @author liyongxu
	 * @date 2019年6月5日 下午2:36:52 
	*/
	private void saveProcessCallbackLog(String wfId, String wfStatus, Long timestamp, String channel) {
		BoApproveRecord boApproveRecord = boApproveRecordService.getProcessInfo(wfId);
		logger.info("boApproveRecord:" + boApproveRecord);
		String logContent = "";
		switch(wfStatus) {
			case Constants.PROCESS_STATUS_DRAFT:
				logContent = Constants.PROCESS_LOG_DRAFT;
				break;
			case Constants.PROCESS_STATUS_EXAMINING:
				logContent = Constants.PROCESS_LOG_EXAMINING;
				break;
			case Constants.PROCESS_STATUS_REJECT_EXAMINING:
				logContent = Constants.PROCESS_LOG_REJECT_EXAMINING;
				break;
			case Constants.PROCESS_STATUS_REJECT:
				logContent = Constants.PROCESS_LOG_REJECT;
				break;
			case Constants.PROCESS_STATUS_PASS:
				logContent = Constants.PROCESS_LOG_PASS;
				break;
			case Constants.PROCESS_STATUS_REJECT_PASS:
				logContent = Constants.PROCESS_LOG_REJECT_PASS;
				break;
			case Constants.PROCESS_STATUS_WASTE:
				logContent = Constants.PROCESS_LOG_WASTE;
				boApproveRecordService.deleteApproveRecorById(boApproveRecord.getId());
				break;
			case Constants.PROCESS_STATUS_NONEXISTENT:
				logContent = Constants.PROCESS_LOG_NONEXISTENT;
				break;
			default:
		}
		
		BoApproveCallbackLog processlog = new BoApproveCallbackLog();
		processlog.setId(UUIDUtils.create());
		processlog.setApproveId(wfId);
		processlog.setApproveStatus(Integer.valueOf(wfStatus));
		processlog.setCallFrom(channel);
		processlog.setRemark(logContent);
		processlog.setCreateTime(LocalDateTime.now());
		boApproveCallbackLogService.save(processlog);
	}
	
}
