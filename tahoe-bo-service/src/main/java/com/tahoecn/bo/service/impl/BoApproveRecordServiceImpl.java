package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.enums.VersionStatusEnum;
import com.tahoecn.bo.common.utils.Constants;
import com.tahoecn.bo.mapper.BoApproveRecordMapper;
import com.tahoecn.bo.mapper.BoProjectExtendMapper;
import com.tahoecn.bo.mapper.BoProjectPriceExtendMapper;
import com.tahoecn.bo.mapper.BoProjectQuotaExtendMapper;
import com.tahoecn.bo.model.entity.BoApproveRecord;
import com.tahoecn.bo.model.entity.BoProjectExtend;
import com.tahoecn.bo.model.entity.BoProjectPriceExtend;
import com.tahoecn.bo.model.entity.BoProjectQuotaExtend;
import com.tahoecn.bo.service.BoApproveRecordService;

/**
 * <p>
 * 审批记录表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-03
 */
@Service
public class BoApproveRecordServiceImpl extends ServiceImpl<BoApproveRecordMapper, BoApproveRecord> implements BoApproveRecordService {

	@Autowired
	private BoApproveRecordMapper boApproveRecordMapper;
	
	@Autowired
	private BoProjectExtendMapper boProjectExtendMapper;
	
	@Autowired
	private BoProjectQuotaExtendMapper boProjectQuotaExtendMapper;
	
	@Autowired
	private BoProjectPriceExtendMapper boProjectPriceExtendMapper;
	
	@Override
	public BoApproveRecord getProcessInfo(String approveId) {
		return boApproveRecordMapper.selectByApproveId(approveId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateApproveRecordByApproveId(String orderType,String processId, String wfStatus, String backtype) {
		BoApproveRecord approveRecord = boApproveRecordMapper.selectByApproveId(processId);
		approveRecord.setId(approveRecord.getId());
		switch (orderType) {
			case Constants.PROCESS_STATUS_PROJECT:
				this.savaApproveUpdateProjectExtendInfo(processId,approveRecord.getRefId(),wfStatus);
				break;
			case Constants.PROCESS_STATUS_SUB_PROJECT:
				this.savaApproveUpdateProjectExtendInfo(processId,approveRecord.getRefId(),wfStatus);
				break;
			case Constants.PROCESS_STATUS_AREA:
				this.savaApproveUpdateProjectQuotaExtendInfo(processId,approveRecord.getRefId(),wfStatus);
				break;
			case Constants.PROCESS_STATUS_PRICE:
				this.savaApproveUpdateProjectPriceExtendInfo(processId,approveRecord.getRefId(),wfStatus);
				break;
			default:
		}
		if(approveRecord.getFormStatus().equals("30") || approveRecord.getFormStatus().equals("31") ) {
			approveRecord.setFormStatus(approveRecord.getFormStatus());
		}else {
			approveRecord.setFormStatus(wfStatus);
		}
		approveRecord.setBackType(backtype);
		approveRecord.setUpdateTime(LocalDateTime.now());
		boApproveRecordMapper.updateById(approveRecord);
	}

	/**
	 * @Title: savaApproveUpdateProjectExtendInfo 
	 * @Description: 更新项目/分期信息数据
	 * @param processId
	 * @param refId
	 * @param wfStatus	10	草稿，用户撒回流程也是10
						11	流程被打回状态
						20	待审。审批状态
						21	被打回后，重新发起审批后的待审，审批状态
						30	流程正常结束
						31	流程重新发起审批后结束，与11，21类似
						00	废弃状态
						99	流程不存在（被删除）
	 * @param currentUserVO void
	 * @author liyongxu
	 * @date 2019年6月19日 下午9:51:30 
	*/
	public void savaApproveUpdateProjectExtendInfo(String processId,String refId, String wfStatus) {
		BoProjectExtend boProjectExtend = boProjectExtendMapper.selectById(refId);
		boProjectExtend.setId(refId);
		if(wfStatus.equals("10")) {
			boProjectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
		}else if (wfStatus.equals("20") || wfStatus.equals("21")) {
			boProjectExtend.setVersionStatus(VersionStatusEnum.CHECKING.getKey());
		}else if (wfStatus.equals("30") || wfStatus.equals("31")) {
			boProjectExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
			boProjectExtend.setApproveEndTime(LocalDateTime.now());
		}else if (wfStatus.equals("11")) {
			boProjectExtend.setVersionStatus(VersionStatusEnum.REJECTED.getKey());
		}else if (wfStatus.equals("00") || wfStatus.equals("99")) {
			boProjectExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
			boProjectExtend.setUpdateTime(LocalDateTime.now());
			boProjectExtendMapper.updateForClearApproveData(boProjectExtend);
			boProjectExtend = null;
			if(wfStatus.equals("99")) {
				deleteApproveRecorByProcessId(processId);
			}
		}
		if(boProjectExtend != null) {
			boProjectExtendMapper.updateById(boProjectExtend);
		}
	}
	
	/**
	 * @Title: savaApproveUpdateProjectQuotaExtendInfo 
	 * @Description: 更新面积数据
	 * @param dataStr
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:34:26 
	*/
	public void savaApproveUpdateProjectQuotaExtendInfo(String processId,String refId, String wfStatus) {
		BoProjectQuotaExtend boProjectQuotaExtend = boProjectQuotaExtendMapper.selectById(refId);
		boProjectQuotaExtend.setId(refId);
		if(wfStatus.equals("10")) {
			boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
		}else if (wfStatus.equals("20") || wfStatus.equals("21")) {
			boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.CHECKING.getKey());
		}else if (wfStatus.equals("30") || wfStatus.equals("31")) {
			boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
			boProjectQuotaExtend.setApproveEndTime(LocalDateTime.now());
		}else if (wfStatus.equals("11")) {
			boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.REJECTED.getKey());
		}else if (wfStatus.equals("00") || wfStatus.equals("99")) {
			boProjectQuotaExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
			boProjectQuotaExtend.setUpdateTime(LocalDateTime.now());
			boProjectQuotaExtendMapper.updateForClearApproveData(boProjectQuotaExtend);
			boProjectQuotaExtend = null;
			if(wfStatus.equals("99")) {
				deleteApproveRecorByProcessId(processId);
			}
		}
		if(boProjectQuotaExtend != null) {
			boProjectQuotaExtendMapper.updateById(boProjectQuotaExtend);
		}
	}
	
	/**
	 * @Title: savaApproveUpdateProjectPriceExtendInfo 
	 * @Description: 更新价格数据
	 * @param dataStr
	 * @param refId void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @date 2019年6月10日 下午2:37:13 
	*/
	public void savaApproveUpdateProjectPriceExtendInfo(String processId,String refId, String wfStatus) {
		BoProjectPriceExtend boProjectPriceExtend = boProjectPriceExtendMapper.selectById(refId);
		boProjectPriceExtend.setId(refId);
		if(wfStatus.equals("10")) {
			boProjectPriceExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
		}else if (wfStatus.equals("20") || wfStatus.equals("21")) {
			boProjectPriceExtend.setVersionStatus(VersionStatusEnum.CHECKING.getKey());
		}else if (wfStatus.equals("30") || wfStatus.equals("31")) {
			boProjectPriceExtend.setVersionStatus(VersionStatusEnum.PASSED.getKey());
			boProjectPriceExtend.setApproveEndTime(LocalDateTime.now());
		}else if (wfStatus.equals("11")) {
			boProjectPriceExtend.setVersionStatus(VersionStatusEnum.REJECTED.getKey());
		}else if (wfStatus.equals("00") || wfStatus.equals("99")) {
			boProjectPriceExtend.setVersionStatus(VersionStatusEnum.CREATING.getKey());
			boProjectPriceExtend.setUpdateTime(LocalDateTime.now());
			boProjectPriceExtendMapper.updateForClearApproveData(boProjectPriceExtend);
			boProjectPriceExtend = null;
			if(wfStatus.equals("99")) {
				deleteApproveRecorByProcessId(processId);
			}
		}
		if(boProjectPriceExtend != null) {
			boProjectPriceExtendMapper.updateById(boProjectPriceExtend);
		}
	}
	
	@Override
	public void deleteApproveRecorById(String approveRecorId) {
		BoApproveRecord boApproveRecord = boApproveRecordMapper.selectById(approveRecorId);
		boApproveRecord.setIsDelete(1);
		boApproveRecordMapper.updateById(boApproveRecord);
	}
	
	/**
	 * @Title: deleteApproveRecorByProcessId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param processId void
	 * @author liyongxu
	 * @date 2019年7月23日 上午11:32:20 
	*/
	public void deleteApproveRecorByProcessId(String processId) {
		BoApproveRecord boApproveRecord = boApproveRecordMapper.selectByApproveId(processId);
		boApproveRecord.setIsDelete(1);
		boApproveRecordMapper.updateById(boApproveRecord);
	}

}
