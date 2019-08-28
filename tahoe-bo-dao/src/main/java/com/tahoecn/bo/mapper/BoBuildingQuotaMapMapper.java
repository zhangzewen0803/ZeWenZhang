package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.BoBuildingQuotaMap;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 楼栋指标数据表/映射表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingQuotaMapMapper extends BaseMapper<BoBuildingQuotaMap> {

	/**
	 * @Title: deleteBuildingQuotaByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId void
	 * @author liyongxu
	 * @param string 
	 * @date 2019年7月15日 下午2:29:48 
	*/
	void deleteBuildingQuotaByBulId(@Param(value = "buildingId")String buildingId,@Param(value = "userId") String userId,@Param(value = "userName") String userName);

}
