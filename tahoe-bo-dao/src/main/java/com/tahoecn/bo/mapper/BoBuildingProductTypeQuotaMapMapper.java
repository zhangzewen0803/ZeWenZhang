package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.BoBuildingProductTypeQuotaMap;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 楼栋业态指标数据表/映射表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingProductTypeQuotaMapMapper extends BaseMapper<BoBuildingProductTypeQuotaMap> {

	/**
	 * @Title: deletedBuildingProductTypeQuotaByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @date 2019年7月15日 下午2:30:41 
	*/
	void deletedBuildingProductTypeQuotaByBulId(@Param(value = "buildingId")String buildingId,@Param(value = "userId") String userId,@Param(value = "userName") String userName);

	/**
	 * @Title: deletedBuildingProductTypeQuotaByProductTypeId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productTypeId void
	 * @author liyongxu
	 * @date 2019年7月15日 下午2:32:16 
	*/
	void deletedBuildingProductTypeQuotaByProductTypeId(@Param(value = "productTypeId")String productTypeId,@Param(value = "userId") String userId,@Param(value = "userName") String userName);

}
