package com.tahoecn.bo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.BoApproveRecord;

/**
 * <p>
 * 审批记录表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-03
 */
public interface BoApproveRecordService extends IService<BoApproveRecord> {

	/**
	 * @Title: getOrderType 
	 * @Description: 获取流程类型
	 * @param wfId
	 * @return BoApproveRecord
	 * @author liyongxu
	 * @date 2019年6月3日 上午10:53:10 
	*/
	BoApproveRecord getProcessInfo(String wfId);

	/**
	 * @Title: updateApproveRecordByApproveId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param wfId
	 * @param wfStatus
	 * @param backtype void
	 * @author liyongxu
	 * @param currentUserVO 
	 * @param currentUserVO 
	 * @param backtype2 
	 * @date 2019年6月3日 下午4:26:09 
	*/
	void updateApproveRecordByApproveId(String orderType, String wfId, String wfStatus, String backtype);

	/**
	 * @Title: deleteById 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id void
	 * @author liyongxu
	 * @date 2019年6月3日 下午4:29:43 
	*/
	void deleteApproveRecorById(String approveRecorId);

}
