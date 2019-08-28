package com.tahoecn.bo.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.BoProjectPriceExtend;

/**
 * <p>
 * 项目分期价格扩展信息表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoProjectPriceExtendMapper extends BaseMapper<BoProjectPriceExtend> {

	/**
	 * @Title: selectByApproveId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param wfId
	 * @return BoProjectPriceExtend
	 * @author liyongxu
	 * @date 2019年6月5日 下午3:12:07 
	*/
	BoProjectPriceExtend selectByApproveId(@Param("approveId") String approveId);

	/**
	 * 清理审批数据，并将审批状态改为编制中
	 * @param boProjectPriceExtend
	 * @return
	 */
	int updateForClearApproveData(BoProjectPriceExtend boProjectPriceExtend);

}
