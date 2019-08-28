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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.CodeEnum;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsDisableEnum;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.DateUtils;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.controller.TahoeBaseController;
import com.tahoecn.bo.model.bo.BpmBusinessInfoBo;
import com.tahoecn.bo.model.bo.BpmReviewBo;
import com.tahoecn.bo.model.dto.BpmReviewDto;
import com.tahoecn.bo.model.entity.BoApproveRecord;
import com.tahoecn.bo.model.entity.BoProjectExtend;
import com.tahoecn.bo.model.entity.BoProjectPriceExtend;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.model.entity.UcUser;
import com.tahoecn.bo.model.vo.BpmProcessInfoVo;
import com.tahoecn.bo.model.vo.BpmRelationVO;
import com.tahoecn.bo.model.vo.BpmShowButtonVo;
import com.tahoecn.bo.model.vo.BusineBaseInfoVO;
import com.tahoecn.bo.service.BoApproveRecordService;
import com.tahoecn.bo.service.BoProjectExtendService;
import com.tahoecn.bo.service.BoProjectPriceExtendService;
import com.tahoecn.bo.service.BoProjectQuotaExtendService;
import com.tahoecn.bo.service.BpmReviewService;
import com.tahoecn.bo.service.ProjectApprovalService;
import com.tahoecn.bo.service.UcUserService;
import com.tahoecn.bpm.vo.ClientButton;
import com.tahoecn.core.json.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @ClassName：BpmReceiveApi
 * @Description：Bpm公共流程API
 * @author liyongxu 
 * @date 2019年6月1日 下午5:29:30 
 * @version 1.0.0 
 */
@Api(tags = "Bpm公共流程API", value = "Bpm公共流程API")
@RestController
@RequestMapping(value = "/api/bpm")
public class BpmCommonApi extends TahoeBaseController{
	
	@Autowired
	private BpmReviewService bpmReviewService;
	
	@Autowired
	private BoApproveRecordService boApproveRecordService;
	
	@Autowired
	private ProjectApprovalService projectApprovalService;
	
	@Autowired
	private BoProjectQuotaExtendService boProjectQuotaExtendService;
	
	@Autowired
	private BoProjectPriceExtendService boProjectPriceExtendService;
	
	@Autowired
	private BoProjectExtendService boProjectExtendService;
	
	@Autowired
	private UcUserService ucUserService;
	
	private static final Logger logger = LoggerFactory.getLogger(BpmCommonApi.class);
	    
	
	/**
	 * @Title: processInfo 
	 * @Description: 流程信息
	 * @param bpmReviewDto
	 * @return JSONResult<BpmProcessInfoVo>
	 * @author liyongxu
	 * @date 2019年6月19日 下午4:43:09 
	*/
	@ApiOperation(value = "流程基础信息", notes = "流程基础信息")
    @RequestMapping(value = "/processInfo", method = RequestMethod.POST)
	public JSONResult<BpmProcessInfoVo> processInfo(@ApiParam(name = "processId", value = "流程Id", required = false) @RequestParam(value = "processId", defaultValue = "") String processId,
			@ApiParam(name = "type", value = "类型(项目：PROJECT,分期：SUBPROJECT,面积：AREA,价格：PRICE)", required = false) @RequestParam(value = "type", defaultValue = "") String type,
			@ApiParam(name = "versionId", value = "版本Id", required = false) @RequestParam(value = "versionId", defaultValue = "") String versionId) {
		JSONResult<BpmProcessInfoVo> jsonResult = new JSONResult<BpmProcessInfoVo>();
		BpmProcessInfoVo processInfoVo = new BpmProcessInfoVo();
		if(StrUtils.isNotEmpty(processId)) {
			BoApproveRecord approveRecord = boApproveRecordService.getProcessInfo(processId);
			processInfoVo.setDocSubject(approveRecord.getDocSubject());
			processInfoVo.setDocContent(approveRecord.getDocContent() == null ? "" :approveRecord.getDocContent());
			processInfoVo.setVersionDesc(approveRecord.getVersionDesc());
			QueryWrapper<UcUser> ucUserWrapper = new QueryWrapper<>();
	        ucUserWrapper.eq("fd_sid", approveRecord.getCreaterId())
	        	.eq("fd_isdelete", -1);
	        UcUser ucUser = ucUserService.getOne(ucUserWrapper);
			processInfoVo.setDocCreatorName(ucUser.getFdName());
			processInfoVo.setCreateTime(approveRecord.getCreateTime().format(DateTimeFormatter.ofPattern(DateUtils.DATE_FULL_STR)));
			processInfoVo.setProcessStatus(approveRecord.getFormStatus());
			processInfoVo.setBackType(approveRecord.getBackType());
			processInfoVo.setShowButton(this.getShowButtonData(processId, approveRecord.getFormStatus()));
		}else {
			switch (type) {
				case Constants.PROCESS_STATUS_PROJECT:
					BpmBusinessInfoBo bpmProInfoBo = projectApprovalService.projectCreatTitle(versionId);
					processInfoVo.setDocSubject(bpmProInfoBo.getDocSubject());
					processInfoVo.setVersionDesc(bpmProInfoBo.getVersionDesc());
					processInfoVo.setDocCreatorName(getCurrentUser().getName());
					processInfoVo.setCreateTime(DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
					processInfoVo.setDocContent("");
					processInfoVo.setProcessStatus("0");
					processInfoVo.setShowButton(this.getShowButtonData(null, "10"));
					break;
				case Constants.PROCESS_STATUS_SUB_PROJECT:
					BpmBusinessInfoBo bpmProSubInfoBo = projectApprovalService.subProjectCreatTitle(versionId);
					processInfoVo.setDocSubject(bpmProSubInfoBo.getDocSubject());
					processInfoVo.setVersionDesc(bpmProSubInfoBo.getVersionDesc());
					processInfoVo.setDocCreatorName(getCurrentUser().getName());
					processInfoVo.setCreateTime(DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
					processInfoVo.setDocContent("");
					processInfoVo.setProcessStatus("0");
					processInfoVo.setShowButton(this.getShowButtonData(null, "10"));
					break;
				case Constants.PROCESS_STATUS_AREA:
					BpmBusinessInfoBo bpmAreaInfoBo = boProjectQuotaExtendService.getPreApproveInfo(versionId);
					processInfoVo.setDocSubject(bpmAreaInfoBo.getDocSubject());
					processInfoVo.setVersionDesc(bpmAreaInfoBo.getVersionDesc());
					processInfoVo.setDocCreatorName(getCurrentUser().getName());
					processInfoVo.setCreateTime(DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
					processInfoVo.setDocContent("");
					processInfoVo.setProcessStatus("0");
					processInfoVo.setShowButton(this.getShowButtonData(null, "10"));
					break;
				case Constants.PROCESS_STATUS_PRICE:
					BpmBusinessInfoBo bpmPriceInfoBo = boProjectPriceExtendService.getPreApproveInfo(versionId);
					processInfoVo.setDocSubject(bpmPriceInfoBo.getDocSubject());
					processInfoVo.setVersionDesc(bpmPriceInfoBo.getVersionDesc());
					processInfoVo.setDocCreatorName(getCurrentUser().getName());
					processInfoVo.setCreateTime(DateUtils.toStrDateFromUtilDateByFormat(new Date(), DateUtils.DATE_FULL_STR));
					processInfoVo.setDocContent("");
					processInfoVo.setProcessStatus("0");
					processInfoVo.setShowButton(this.getShowButtonData(null, "10"));
					break;
				default:
			}
		}
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		jsonResult.setData(processInfoVo);
		return jsonResult;
	}
	
	/**
	 * @Title: getShowButtonData 
	 * @Description: showButton
	 * @param processId
	 * @param 版本状态          0-编制中；1-审批中；2：审批通过；3：已驳回；
	 * 		formStatus	10	草稿，用户撒回流程也是10
						11	流程被打回状态 -驳回
						20	待审。审批状态
						21	被打回后，重新发起审批后的待审，审批状态
						30	流程正常结束
						31	流程重新发起审批后结束，与11，21类似
						00	废弃状态
						99	流程不存在（被删除）
	 * @return List<BpmShowButtonVo>
	 * @author liyongxu
	 * @date 2019年6月19日 下午5:36:08 
	*/
	public List<BpmShowButtonVo> getShowButtonData(String processId, String formStatus) {
		List<BpmShowButtonVo> showButtonList = new ArrayList<>();
		if(StrUtils.isEmpty(processId)) {
			showButtonList = getButtonVoList("new", null);
		}else {
			JSONResult<String> jsonResult = bpmReviewService.getGotoAuditProcessUrl(processId);
			String viewURL = "";
			if(jsonResult.getCode() == 200) {
				viewURL = jsonResult.getData();
			}
			if(formStatus.equals("10") || formStatus.equals("11")) {
				showButtonList = getButtonVoList("update|drop|view", viewURL);
			}else if (formStatus.equals("20") || formStatus.equals("21")) {
				showButtonList = getButtonVoList("back|view", viewURL);
			}else if (formStatus.equals("30") || formStatus.equals("31")) {
				showButtonList = getButtonVoList("view", viewURL);
			}else if (formStatus.equals("00") || formStatus.equals("99")) {
				showButtonList = getButtonVoList("new", null);
			}
		}
		return showButtonList;
	}

	/**
	 * @Title: getButtonVoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param showButtonMap
	 * @param btnCodes
	 * @return List<BpmShowButtonVo>
	 * @author liyongxu
	 * @date 2019年6月19日 下午6:10:28 
	*/
	private List<BpmShowButtonVo> getButtonVoList(String btnCodes, String viewURL) {
		Map<String, BpmShowButtonVo> showButtonMap = new HashMap<String, BpmShowButtonVo>();
		showButtonMap.put("new", new BpmShowButtonVo("NewProcess","发起流程"));
		showButtonMap.put("update", new BpmShowButtonVo("UpdateProcess","更新流程"));
		showButtonMap.put("drop", new BpmShowButtonVo("DropProcess","废弃流程"));
		showButtonMap.put("back", new BpmShowButtonVo("BackProcess","撤回流程"));
		showButtonMap.put("view", new BpmShowButtonVo("ViewProcess","查看流程",viewURL));
		
		List<BpmShowButtonVo> showButtonList = new ArrayList<BpmShowButtonVo>();
		String [] btnCodeArray = btnCodes.split("\\|");
		for(String code : btnCodeArray) {
			showButtonList.add(showButtonMap.get(code));
		}
		return showButtonList;
	}
	
	/**
	 * @Title: getStageInfoByVersionId 
	 * @Description: 根据版本id查询业务基础信息
	 * @param versionId
	 * @return JSONResult<StageInfoVO>
	 * @author liyongxu
	 * @date 2019年7月16日 下午6:48:42 
	*/
	@ApiOperation(value = "根据版本id查询业务基础信息", notes = "根据版本id查询业务基础信息")
    @RequestMapping(value = "/getStageInfoByVersionId", method = RequestMethod.POST)
	public JSONResult<BusineBaseInfoVO> getStageInfoByVersionId(@ApiParam(name = "versionId", value = "版本Id", required = true) @RequestParam(value = "versionId", defaultValue = "") String versionId,
			@ApiParam(name = "type", value = "类型(项目：PROJECT,分期：SUBPROJECT,面积：AREA,价格：PRICE)", required = false) @RequestParam(value = "type", defaultValue = "") String type) {
		JSONResult<BusineBaseInfoVO> jsonResult = new JSONResult<BusineBaseInfoVO>();
		BusineBaseInfoVO busineBaseInfoVO = new BusineBaseInfoVO();
		if(type.equals(Constants.PROCESS_STATUS_AREA)) {
			QueryWrapper<BoProjectQuotaExtend> projectQuotaExtendWrapper = new QueryWrapper<>();
	        projectQuotaExtendWrapper.eq("id", versionId)
	        	.eq("is_delete", IsDeleteEnum.NO.getKey())
	        	.eq("is_disable", IsDisableEnum.NO.getKey());
	        BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendService.getOne(projectQuotaExtendWrapper);
			if(boProjectQuotaExtend != null) {
				busineBaseInfoVO.setProjectId(boProjectQuotaExtend.getProjectId());
				busineBaseInfoVO.setStageCode(boProjectQuotaExtend.getStageCode());
				jsonResult.setData(busineBaseInfoVO);
			}
		}else if (type.equals(Constants.PROCESS_STATUS_PRICE)) {
			QueryWrapper<BoProjectPriceExtend> wrapper = new QueryWrapper<>();
	        wrapper.eq("id", versionId)
	        	.eq("is_delete", IsDeleteEnum.NO.getKey())
	        	.eq("is_disable", IsDisableEnum.NO.getKey());
	        BoProjectPriceExtend boProjectPriceExtend = boProjectPriceExtendService.getOne(wrapper);
			if(boProjectPriceExtend != null) {
				busineBaseInfoVO.setProjectId(boProjectPriceExtend.getProjectId());
				busineBaseInfoVO.setStageCode(boProjectPriceExtend.getStageCode());
				jsonResult.setData(busineBaseInfoVO);
			}
		}else if (type.equals(Constants.PROCESS_STATUS_PROJECT) || type.equals(Constants.PROCESS_STATUS_SUB_PROJECT)) {
			QueryWrapper<BoProjectExtend> wrapper = new QueryWrapper<>();
	        wrapper.eq("id", versionId)
	        	.eq("is_delete", IsDeleteEnum.NO.getKey())
	        	.eq("is_disable", IsDisableEnum.NO.getKey());
	        BoProjectExtend boProjectExtend = boProjectExtendService.getOne(wrapper);
			if(boProjectExtend != null) {
				busineBaseInfoVO.setProjectId(boProjectExtend.getProjectId());
				jsonResult.setData(busineBaseInfoVO);
			}
		}
		jsonResult.setCode(CodeEnum.SUCCESS.getKey());
		jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
		return jsonResult;
	}
	
	/**
	 * @Title: getBpmRelationId 
	 * @Description: 根据流程id查询业务id
	 * @param processId
	 * @return JSONResult<BpmRelationVO>
	 * @author liyongxu
	 * @date 2019年7月10日 下午3:32:58 
	*/
	@ApiOperation(value = "获取业务Id", notes = "获取业务Id")
    @RequestMapping(value = "/getBpmRelationId", method = RequestMethod.POST)
	public JSONResult<BpmRelationVO> getBpmRelationId(@ApiParam(name = "processId", value = "流程Id", required = true) @RequestParam(value = "processId", defaultValue = "") String processId) {
		JSONResult<BpmRelationVO> jsonResult = new JSONResult<BpmRelationVO>();
		BoApproveRecord boApproveRecord = boApproveRecordService.getProcessInfo(processId);
		if(boApproveRecord != null) {
			BpmRelationVO bpmRelation = new BpmRelationVO();
			bpmRelation.setProcessId(processId);
			bpmRelation.setVersionId(boApproveRecord.getRefId());
			bpmRelation.setType(boApproveRecord.getRefTable());
			jsonResult.setCode(CodeEnum.SUCCESS.getKey());
			jsonResult.setMsg(CodeEnum.SUCCESS.getValue());
			jsonResult.setData(bpmRelation);
		}
		return jsonResult;
	}
	
	/**
	 * @Title: newProcess 
	 * @Description: 发起流程
	 * @param bpmReviewDto
	 * @return JSONResult<BpmReviewBo>
	 * @author liyongxu
	 * @date 2019年7月24日 上午11:16:10 
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "发起流程", notes = "发起流程")
    @RequestMapping(value = "/newProcess", method = RequestMethod.POST)
	public JSONResult<BpmReviewBo> newProcess(BpmReviewDto bpmReviewDto) {
		JSONResult<BpmReviewBo> bpmReviewJson = new JSONResult<BpmReviewBo>();
		JSONResult validateApprovalJson = new JSONResult();
		QueryWrapper<BoApproveRecord> approveRecordWrapper = new QueryWrapper<>();
        approveRecordWrapper.eq("ref_id", bpmReviewDto.getVersionId())
        	.notIn("form_status", 00,99)
        	.eq("is_delete", IsDeleteEnum.NO.getKey())
        	.eq("is_disable", IsDisableEnum.NO.getKey());
        BoApproveRecord boApproveRecord = boApproveRecordService.getOne(approveRecordWrapper);
        if(boApproveRecord != null) {
        	bpmReviewJson.setCode(CodeEnum.ERROR.getKey());
        	bpmReviewJson.setMsg("发起流程失败，该版本已经有审批发起！");
        }else {
        	JSONResult<BpmProcessInfoVo> bpmProcessInfo = this.processInfo(null, bpmReviewDto.getType(), bpmReviewDto.getVersionId());
        	if(bpmProcessInfo.getCode() == 0) {
        		bpmReviewDto.setDocSubject(bpmProcessInfo.getData().getDocSubject());
            	bpmReviewDto.setVersionDesc(bpmProcessInfo.getData().getVersionDesc());
            	validateApprovalJson = this.validateApproval(bpmReviewDto.getVersionId(), bpmReviewDto.getType());
            	if(validateApprovalJson.getCode() == 0) {
            		bpmReviewJson = bpmReviewService.newProcess(bpmReviewDto,getCurrentUser());
                	if(bpmReviewJson.getCode() == 200) {
                		bpmReviewJson.setCode(CodeEnum.SUCCESS.getKey());
                		bpmReviewJson.setMsg(CodeEnum.SUCCESS.getValue());
                	}else {
                		bpmReviewJson.setCode(CodeEnum.ERROR.getKey());
                		bpmReviewJson.setMsg(bpmReviewJson.getMsg());
        			}
            	}else {
            		bpmReviewJson = validateApprovalJson;
    			}
        	}
		}
		return bpmReviewJson;
	}
	
	/**
	 * @Title: updateFormData 
	 * @Description:修改表单内容
	 * @param bpmReviewDto
	 * @param processId
	 * @return JSONResult<BpmReviewBo>
	 * @author liyongxu
	 * @date 2019年7月24日 上午11:15:54 
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "修改表单内容", notes = "修改表单内容")
    @RequestMapping(value = "/updateFormData", method = RequestMethod.POST)
	public JSONResult<BpmReviewBo> updateFormData(BpmReviewDto bpmReviewDto,
			@ApiParam(name = "processId", value = "流程Id", required = true) @RequestParam(value = "processId", defaultValue = "") String processId) {
		JSONResult<BpmReviewBo> bpmReviewJson = new JSONResult<BpmReviewBo>();
		JSONResult validateApprovalJson = new JSONResult();
		QueryWrapper<BoApproveRecord> approveRecordWrapper = new QueryWrapper<>();
        approveRecordWrapper.eq("approve_id", processId)
        	.eq("is_delete", IsDeleteEnum.NO.getKey())
        	.eq("is_disable", IsDisableEnum.NO.getKey());
        BoApproveRecord boApproveRecord = boApproveRecordService.getOne(approveRecordWrapper);
        QueryWrapper<UcUser> ucUserWrapper = new QueryWrapper<>();
        ucUserWrapper.eq("fd_sid", boApproveRecord.getCreaterId())
        	.eq("fd_isdelete", -1);
        UcUser ucUser = ucUserService.getOne(ucUserWrapper);
        String userName = ucUser.getFdUsername();
    	bpmReviewDto.setDocSubject(boApproveRecord.getDocSubject());
    	bpmReviewDto.setVersionDesc(boApproveRecord.getVersionDesc());
    	logger.info("versionId:" + bpmReviewDto.getVersionId() + "type:" + bpmReviewDto.getType() + "processId:" + processId);
    	validateApprovalJson = this.validateApproval(bpmReviewDto.getVersionId(), bpmReviewDto.getType());
    	logger.info("validateApprovalJson:" + validateApprovalJson);
    	if(validateApprovalJson.getCode() == 0) {
        	bpmReviewJson = bpmReviewService.updateFormData(processId,bpmReviewDto,userName,getCurrentUser());
			if(bpmReviewJson.getCode() == 200) {
				bpmReviewJson.setCode(CodeEnum.SUCCESS.getKey());
				bpmReviewJson.setMsg(CodeEnum.SUCCESS.getValue());
	    	}else {
	    		bpmReviewJson.setCode(CodeEnum.ERROR.getKey());
	    		bpmReviewJson.setMsg(bpmReviewJson.getMsg());
			}
		}else {
			bpmReviewJson = validateApprovalJson;
		}
		return bpmReviewJson;
	}
	
	/**
	 * @Title: validateApproval 
	 * @Description: 校验是否可以发起流程
	 * @param versionId
	 * @param type
	 * @return JSONResult
	 * @author liyongxu
	 * @date 2019年7月24日 上午11:12:12 
	*/
	@SuppressWarnings("rawtypes")
	public JSONResult validateApproval(String versionId,String type) {
		JSONResult validateApprovalJson = new JSONResult();
		try {
        	switch (type) {
				case Constants.PROCESS_STATUS_PROJECT:
					validateApprovalJson = projectApprovalService.projectValidateApproval(versionId);
					break;
				case Constants.PROCESS_STATUS_SUB_PROJECT:
					validateApprovalJson =  projectApprovalService.subProjectValidateApproval(versionId);
					break;
				case Constants.PROCESS_STATUS_AREA:
					validateApprovalJson =  boProjectQuotaExtendService.areaValidateApprove(versionId);
					break;
				case Constants.PROCESS_STATUS_PRICE:
					validateApprovalJson =  boProjectPriceExtendService.priceValidateApprove(versionId);
					break;
				default:
        	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return validateApprovalJson;
	}
	
	/**
	 * @Title: backProcess 
	 * @Description: 流程撤回
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年7月24日 上午11:16:34 
	*/
	@ApiOperation(value = "流程撤回", notes = "流程撤回")
    @RequestMapping(value = "/backProcess", method = RequestMethod.POST)
	public JSONResult<String> backProcess(
			@ApiParam(name = "processId", value = "流程Id", required = true) @RequestParam(value = "processId", defaultValue = "") String processId) {
		JSONResult<String> processResult = bpmReviewService.backProcess(processId,getCurrentUser());
		if(processResult.getCode() == 200) {
			processResult.setCode(CodeEnum.SUCCESS.getKey());
			processResult.setMsg(CodeEnum.SUCCESS.getValue());
    	}else {
    		processResult.setCode(CodeEnum.ERROR.getKey());
    		processResult.setMsg(processResult.getMsg());
		}
		return processResult;
	}
	
	/**
	 * @Title: dropProcess 
	 * @Description: 废弃流程
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年7月24日 上午11:16:44 
	*/
	@ApiOperation(value = "废弃流程", notes = "废弃流程")
    @RequestMapping(value = "/dropProcess", method = RequestMethod.POST)
	public JSONResult<String> dropProcess(
			@ApiParam(name = "processId", value = "流程Id", required = true) @RequestParam(value = "processId", required = false) String processId) {
		JSONResult<String> processResult = bpmReviewService.dropProcess(processId,getCurrentUser());
		if(processResult.getCode() == 200) {
			processResult.setCode(CodeEnum.SUCCESS.getKey());
			processResult.setMsg(CodeEnum.SUCCESS.getValue());
    	}else {
    		processResult.setCode(CodeEnum.ERROR.getKey());
    		processResult.setMsg(processResult.getMsg());
		}
		return processResult;
	}
	
	/**
	 * @Title: showButtons 
	 * @Description: 业务系统获取表单按钮状态
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年6月13日 下午5:08:35 
	*/
//	@ApiOperation(value = "业务系统获取表单按钮状态", notes = "业务系统获取表单按钮状态")
//	@RequestMapping(value = "/showButtons", method = RequestMethod.POST)
	public JSONResult<List<ClientButton>> showButtons(
			@ApiParam(name = "processId", value = "流程Id", required = true) @RequestParam(value = "processId", defaultValue = "") String processId) {
		List<ClientButton> clientButtons = new ArrayList<ClientButton>();
		JSONResult<List<ClientButton>> processResult = bpmReviewService.showButtons(processId, clientButtons);
		return processResult;
	}
    
	/**
	 * @Title: getGotoAuditProcessUrl 
	 * @Description: 获取跳转审批过程URL
	 * @param processId
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @date 2019年6月13日 下午5:13:19 
	*/
//	@ApiOperation(value = "获取跳转审批过程URL", notes = "获取跳转审批过程URL")
//    @RequestMapping(value = "/getGotoAuditProcessUrl", method = RequestMethod.POST)
	public JSONResult<String> getGotoAuditProcessUrl(
			@ApiParam(name = "processId", value = "流程Id", required = true) @RequestParam(value = "processId", defaultValue = "") String processId) {
		JSONResult<String> processResult = bpmReviewService.getGotoAuditProcessUrl(processId);
		return processResult;
	}
}
