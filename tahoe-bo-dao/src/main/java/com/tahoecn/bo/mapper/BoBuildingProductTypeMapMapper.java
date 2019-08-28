package com.tahoecn.bo.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.BoBuildingProductTypeMap;

/**
 * <p>
 * 楼栋业态关系表/映射表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoBuildingProductTypeMapMapper extends BaseMapper<BoBuildingProductTypeMap> {

	/**
	 * @Title: deleteBuildingProductTypeByBulId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param buildingId
	 * @return void
	 * @author liyongxu
	 * @return 
	 * @date 2019年7月12日 下午5:25:50 
	*/
	void deleteBuildingProductTypeByBulId(@Param(value = "buildingId")String buildingId,@Param(value = "userId") String userId,@Param(value = "userName") String userName);

	/**
	 * @Title: deleteBuildingProductTypeByProductId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productId
	 * @param userId
	 * @param userName void
	 * @author liyongxu
	 * @date 2019年7月19日 上午10:26:07 
	*/
	void deleteBuildingProductTypeByProductId(@Param(value = "productId")String productId,@Param(value = "userId") String userId,@Param(value = "userName") String userName);

}
