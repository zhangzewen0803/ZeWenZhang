package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.common.utils.StrUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.BoApproveRecordMapper;
import com.tahoecn.bo.mapper.BoBpmTemplateConfigMapper;
import com.tahoecn.bo.mapper.BoProjectExtendMapper;
import com.tahoecn.bo.mapper.BoProjectPriceExtendMapper;
import com.tahoecn.bo.mapper.BoProjectQuotaExtendMapper;
import com.tahoecn.bo.model.bo.BpmReviewBo;
import com.tahoecn.bo.model.dto.BpmReviewDto;
import com.tahoecn.bo.model.entity.BoApproveRecord;
import com.tahoecn.bo.model.entity.BoBpmTemplateConfig;
import com.tahoecn.bo.model.entity.BoProjectExtend;
import com.tahoecn.bo.model.entity.BoProjectPriceExtend;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.model.vo.CurrentUserVO;
import com.tahoecn.bo.service.BpmReviewService;
import com.tahoecn.bpm.BpmClient;
import com.tahoecn.bpm.vo.BpmReviewParam;
import com.tahoecn.bpm.vo.ClientButton;
import com.tahoecn.core.json.JSONResult;
import com.tahoecn.http.HttpStatus;
import com.tahoecn.log.Log;
import com.tahoecn.log.LogFactory;

/**
 * @ClassName：BpmServiceImpl
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @author liyongxu 
 * @date 2019年5月31日 上午10:01:02 
 * @version 1.0.0 
 */
@Service
public class BpmReviewServiceImpl implements BpmReviewService {

	@Autowired
	private BoApproveRecordMapper boApproveRecordMapper;
	
	@Autowired
	private BoProjectExtendMapper boProjectExtendMapper;
	
	@Autowired
	private BoProjectQuotaExtendMapper boProjectQuotaExtendMapper;
	
	@Autowired
	private BoProjectPriceExtendMapper boProjectPriceExtendMapper;
	
	@Autowired
	private BoBpmTemplateConfigMapper boBpmTemplateConfigMapper;
	
	@Value("${tahoe.bpm.previewUrl}")
	private String bpmPreviewUrl;//Bpm跳转地址
	
	public static final Log logger = LogFactory.get(BpmReviewServiceImpl.class);
	
	@Override
	public JSONResult<BpmReviewBo> newProcess(BpmReviewDto bpmReviewDto, CurrentUserVO currentUserVO) {
		JSONResult<BpmReviewBo> bpmReviewJson = new JSONResult<BpmReviewBo>();
		try {
			BpmReviewParam bpmReviewParamterForm = new BpmReviewParam();
			// 测试模板id与流程配置组沟通
			String templateId = this.getTemplateId(bpmReviewDto.getType());
			bpmReviewParamterForm.setFdTemplateId(templateId);
			bpmReviewParamterForm.setDocSubject(bpmReviewDto.getDocSubject());
			bpmReviewParamterForm.setDocCreator("{'LoginName':'" + currentUserVO.getUsername() + "'}");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectId", bpmReviewDto.getProjectId());
			bpmReviewParamterForm.setFormValues(map);
			JSONResult<String> jsonResult = BpmClient.newProcess(bpmReviewParamterForm);// 发起审批
			logger.info("------------------------------------------发起审批:" + jsonResult);
			String processId = jsonResult.getData();
			if(StrUtils.isNotEmpty(processId)) {
				BpmReviewBo bpmReviewBo = new BpmReviewBo();
				this.savaApproveRecord(processId,templateId, bpmReviewDto, currentUserVO);
				bpmReviewBo.setProcessId(processId);
				bpmReviewBo.setReviewUrl(bpmPreviewUrl + processId);
				bpmReviewJson.setData(bpmReviewBo);
			}
			bpmReviewJson.setCode(jsonResult.getCode());
			bpmReviewJson.setMsg(jsonResult.getMsg());
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return bpmReviewJson;
	}

	/**
	 * @Title: templateId 
	 * @Description: 返回模板id
	 * @param templateType
	 * @return String
	 * @author liyongxu
	 * @date 2019年6月15日 下午1:24:55 
	*/
	public String getTemplateId(String templateType) {
        BoBpmTemplateConfig boBpmTemplateConfig = boBpmTemplateConfigMapper.selectOneTemplateIdByType(templateType);
		return boBpmTemplateConfig.getTemplateId();
	}
	
	/**
	 * @Title: savaApproveRecord 
	 * @Description: 保存流程数据
	 * @param jsonResult
	 * @return JSONResult<String>
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月1日 上午11:35:50 
	*/
	@Transactional(rollbackFor = Exception.class)
	public void savaApproveRecord(String processId,String templateId,BpmReviewDto bpmReviewDto, CurrentUserVO currentUserVO) {
		BoApproveRecord approveRecord = new BoApproveRecord();
		approveRecord.setId(UUIDUtils.create());
		approveRecord.setApproveId(processId);
		approveRecord.setRefId(bpmReviewDto.getVersionId());
		approveRecord.setRefTable(bpmReviewDto.getType());
		switch (bpmReviewDto.getType()) {
			case Constants.PROCESS_STATUS_PROJECT:
				this.savaApproveUpdateProjectExtendInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_SUB_PROJECT:
				this.savaApproveUpdateProjectExtendInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_AREA:
				this.savaApproveUpdateProjectQuotaExtendInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_PRICE:
				this.savaApproveUpdateProjectPriceExtendInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			default:
		}
		approveRecord.setFormStatus("10");
		approveRecord.setVersionDesc(bpmReviewDto.getVersionDesc());
		approveRecord.setDocSubject(bpmReviewDto.getDocSubject());
		approveRecord.setTemplateId(templateId);
		approveRecord.setDocContent(bpmReviewDto.getDocContent());
		approveRecord.setIsDelete(Constants.NODEL);
		approveRecord.setIsDisable(Constants.NODEL);
		approveRecord.setCreaterId(currentUserVO.getId());
		approveRecord.setCreaterName(currentUserVO.getName());
		approveRecord.setCreateTime(LocalDateTime.now());
		boApproveRecordMapper.insert(approveRecord);
	}
	
	/**
	 * @Title: savaApproveUpdateProjectInfo 
	 * @Description: 更新项目信息数据
	 * @param processId
	 * @param refId
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:29:12 
	*/
	public void savaApproveUpdateProjectExtendInfo(String processId,String refId, CurrentUserVO currentUserVO) {
		BoProjectExtend boProjectExtend = boProjectExtendMapper.selectById(refId);
		boProjectExtend.setId(refId);
		boProjectExtend.setApproveId(processId);
		boProjectExtend.setApproveStartTime(LocalDateTime.now());
		boProjectExtend.setUpdaterId(currentUserVO.getId());
		boProjectExtend.setUpdaterName(currentUserVO.getName());
		boProjectExtend.setUpdateTime(LocalDateTime.now());
		boProjectExtendMapper.updateById(boProjectExtend);
	}
	
	/**
	 * @Title: savaApproveUpdateProjectQuotaExtendInfo 
	 * @Description: 更新面积数据
	 * @param processId
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:34:26 
	*/
	public void savaApproveUpdateProjectQuotaExtendInfo(String processId,String refId, CurrentUserVO currentUserVO) {
		BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendMapper.selectById(refId);
		boProjectQuotaExtend.setId(refId);
		boProjectQuotaExtend.setApproveId(processId);
		boProjectQuotaExtend.setApproveStartTime(LocalDateTime.now());
		boProjectQuotaExtend.setUpdaterId(currentUserVO.getId());
		boProjectQuotaExtend.setUpdaterName(currentUserVO.getName());
		boProjectQuotaExtend.setUpdateTime(LocalDateTime.now());
		boProjectQuotaExtendMapper.updateById(boProjectQuotaExtend);
	}
	
	/**
	 * @Title: savaApproveUpdateProjectPriceExtendInfo 
	 * @Description: 更新价格数据
	 * @param processId
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:37:13 
	*/
	public void savaApproveUpdateProjectPriceExtendInfo(String processId,String refId, CurrentUserVO currentUserVO) {
		BoProjectPriceExtend boProjectPriceExtend = boProjectPriceExtendMapper.selectById(refId);
		boProjectPriceExtend.setId(refId);
		boProjectPriceExtend.setApproveId(processId);
		boProjectPriceExtend.setApproveStartTime(LocalDateTime.now());
		boProjectPriceExtend.setUpdaterId(currentUserVO.getId());
		boProjectPriceExtend.setUpdaterName(currentUserVO.getName());
		boProjectPriceExtend.setUpdateTime(LocalDateTime.now());
		boProjectPriceExtendMapper.updateById(boProjectPriceExtend);
	}
	
	@Override
	public JSONResult<BpmReviewBo> updateFormData(String processId,BpmReviewDto bpmReviewDto, String userName,CurrentUserVO currentUserVO) {
		JSONResult<BpmReviewBo> bpmReviewJson = new JSONResult<BpmReviewBo>();
		try {
			BpmReviewParam bpmReviewParamterForm = new BpmReviewParam();
			// 被修改的流程id，必填
			bpmReviewParamterForm.setFdId(processId);
			// 测试模板id与流程配置组沟通
			String templateId = this.getTemplateId(bpmReviewDto.getType());
			bpmReviewParamterForm.setFdTemplateId(templateId);
			bpmReviewParamterForm.setDocSubject(bpmReviewDto.getDocSubject());
			bpmReviewParamterForm.setDocCreator("{'LoginName':'" + userName + "'}");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectId", bpmReviewDto.getProjectId());
			bpmReviewParamterForm.setFormValues(map);
			JSONResult<String> jsonResult = BpmClient.updateFormData(bpmReviewParamterForm);
			logger.info("----------------------------------------------更新审批:" + jsonResult);
			String processID = jsonResult.getData();
			if(processId.equals(processID)) {
				BpmReviewBo bpmReviewBo = new BpmReviewBo();
				this.updateApproveRecord(processId,templateId,bpmReviewDto,currentUserVO);
				bpmReviewBo.setProcessId(processId);
				bpmReviewBo.setReviewUrl(bpmPreviewUrl + processId);
				bpmReviewJson.setData(bpmReviewBo);
			}
			bpmReviewJson.setCode(jsonResult.getCode());
			bpmReviewJson.setMsg(jsonResult.getMsg());
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return bpmReviewJson;
	}

	/**
	 * @Title: updateApproveRecord 
	 * @Description: 更新流程数据
	 * @param processId
	 * @param bpmReviewDto void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月5日 上午10:52:22 
	*/
	@Transactional(rollbackFor = Exception.class)
	public void updateApproveRecord(String processId,String templateId,BpmReviewDto bpmReviewDto, CurrentUserVO currentUserVO) {
		BoApproveRecord approveRecord = boApproveRecordMapper.selectByApproveId(processId);
		switch (bpmReviewDto.getType()) {
			case Constants.PROCESS_STATUS_PROJECT:
				this.updatePrejectInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_SUB_PROJECT:
				this.updatePrejectInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_AREA:
				this.updatePrejectQuotaInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_PRICE:
				this.updatePrejectPriceInfo(processId,bpmReviewDto.getVersionId(),currentUserVO);
				break;
			default:
		}
		approveRecord.setDocContent(bpmReviewDto.getDocContent());
		approveRecord.setUpdaterId(currentUserVO.getId());
		approveRecord.setUpdaterName(currentUserVO.getName());
		approveRecord.setUpdateTime(LocalDateTime.now());
		boApproveRecordMapper.updateById(approveRecord);
	}
	
	/**
	 * @Title: updatePrejectonfo 
	 * @Description: 更新流程时修改项目数据
	 * @param processID
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:47:17 
	*/
	public void updatePrejectInfo(String processId,String refId, CurrentUserVO currentUserVO) {
		BoProjectExtend boProjectExtend = boProjectExtendMapper.selectById(refId);
		boProjectExtend.setId(refId);
		boProjectExtend.setUpdaterId(currentUserVO.getId());
		boProjectExtend.setUpdaterName(currentUserVO.getName());
		boProjectExtend.setUpdateTime(LocalDateTime.now());
		boProjectExtendMapper.updateById(boProjectExtend);
	}
	
	/**
	 * @Title: updatePrejectQuotaInfo 
	 * @Description:  更新流程时修改面积数据
	 * @param processId
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:48:48 
	*/
	public void updatePrejectQuotaInfo(String processId,String refId, CurrentUserVO currentUserVO) {
		BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendMapper.selectById(refId);
		boProjectQuotaExtend.setId(refId);
		boProjectQuotaExtend.setUpdaterId(currentUserVO.getId());
		boProjectQuotaExtend.setUpdaterName(currentUserVO.getName());
		boProjectQuotaExtend.setUpdateTime(LocalDateTime.now());
		boProjectQuotaExtendMapper.updateById(boProjectQuotaExtend);
	}
	
	/**
	 * @Title: updatePrejectPriceInfo 
	 * @Description: 更新流程时修改价格数据
	 * @param processId
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:49:40 
	*/
	public void updatePrejectPriceInfo(String processId,String refId, CurrentUserVO currentUserVO) {
		BoProjectPriceExtend boProjectPriceExtend = boProjectPriceExtendMapper.selectById(refId);
		boProjectPriceExtend.setId(refId);
		boProjectPriceExtend.setUpdaterId(currentUserVO.getId());
		boProjectPriceExtend.setUpdaterName(currentUserVO.getName());
		boProjectPriceExtend.setUpdateTime(LocalDateTime.now());
		boProjectPriceExtendMapper.updateById(boProjectPriceExtend);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public JSONResult<String> backProcess(String processId,CurrentUserVO currentUserVO) {
		JSONResult<String> jsonResult = new JSONResult<String>();
		try {
			jsonResult = BpmClient.backProcess(processId);
			if("200".equals(jsonResult.getCode())) {
				this.updateBusinessInfo(processId, currentUserVO);
			}
			return jsonResult;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			jsonResult.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
			jsonResult.setMsg(e.getMessage());
		}
		return jsonResult;
	}
	
	/**
	 * @Title: updateBusinessInfo 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param processId
	 * @param currentUserVO void
	 * @author liyongxu
	 * @date 2019年6月19日 下午9:37:48 
	*/
	@Transactional(rollbackFor = Exception.class)
	public void updateBusinessInfo(String processId,CurrentUserVO currentUserVO) {
		BoApproveRecord approveRecord = boApproveRecordMapper.selectByApproveId(processId);
		switch (approveRecord.getRefTable()) {
			case Constants.PROCESS_STATUS_PROJECT:
				this.updatePrejectInfo(processId,approveRecord.getRefId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_SUB_PROJECT:
				this.updatePrejectInfo(processId,approveRecord.getRefId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_AREA:
				this.updatePrejectQuotaInfo(processId,approveRecord.getRefId(),currentUserVO);
				break;
			case Constants.PROCESS_STATUS_PRICE:
				this.updatePrejectPriceInfo(processId,approveRecord.getRefId(),currentUserVO);
				break;
			default:
		}
	}
	
	@Override
	public JSONResult<String> dropProcess(String processId,CurrentUserVO currentUserVO) {
		JSONResult<String> jsonResult = new JSONResult<>();
		try {
			jsonResult = BpmClient.dropProcess(processId);
			if(jsonResult.getCode() == 200) {
				this.updateBusinessInfo(processId, currentUserVO);
			}
			return jsonResult;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			jsonResult.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
			jsonResult.setMsg(e.getMessage());
		}
		return jsonResult;
	}

	@Override
	public JSONResult<List<ClientButton>> showButtons(String processId,List<ClientButton> clientButtons) {
		JSONResult<List<ClientButton>> jsonResult = new JSONResult<List<ClientButton>>();
		try {
			jsonResult = BpmClient.showButtons(processId,clientButtons);
			return jsonResult;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			jsonResult.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
			jsonResult.setMsg(e.getMessage());
		}
		return jsonResult;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONResult<String> getGotoAuditProcessUrl(String processId) {
		JSONResult<String> jsonResult = new JSONResult<String>();
		try {
			jsonResult = BpmClient.getGotoAuditProcessUrl(processId);
			return jsonResult;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			jsonResult.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
			jsonResult.setMsg(e.getMessage());
		}
		return jsonResult;
	}
	
}
