package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.BoProjectSupplyPlanVersion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目分期供货计划版本表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-08-08
 */
public interface BoProjectSupplyPlanVersionService extends IService<BoProjectSupplyPlanVersion> {

	/**
	 * @Title: createVersion 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param projectId
	 * @param stageCode
	 * @param planType
	 * @return BoProjectSupplyPlanVersion
	 * @author liyongxu
	 * @date 2019年8月9日 上午10:51:05 
	*/
	BoProjectSupplyPlanVersion createVersion(String projectId, String stageCode, String planType);

	/**
	 * @Title: hasCanEditData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param projectId
	 * @param stageCode
	 * @return boolean
	 * @author liyongxu
	 * @date 2019年8月9日 上午10:51:45 
	*/
	boolean hasCanEditData(String projectId, String stageCode);

}
