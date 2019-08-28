package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.BoApproveRecord;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 审批记录表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-03
 */
public interface BoApproveRecordMapper extends BaseMapper<BoApproveRecord> {

	/**
	 * @Title: selectByApproveId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param approveId
	 * @return BoApproveRecord
	 * @author liyongxu
	 * @date 2019年6月3日 上午10:56:31 
	*/
	BoApproveRecord selectByApproveId(@Param(value = "approveId")String approveId);

}
