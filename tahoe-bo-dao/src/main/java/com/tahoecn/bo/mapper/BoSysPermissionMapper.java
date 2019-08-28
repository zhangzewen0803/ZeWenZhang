package com.tahoecn.bo.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.BoSysPermission;

/**
 * <p>
 * 系统权限表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-27
 */
public interface BoSysPermissionMapper extends BaseMapper<BoSysPermission> {

	/**
	 * @Title: selectByFdCode 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param fdCode
	 * @return List<BoSysPermission>
	 * @author liyongxu
	 * @date 2019年5月28日 下午9:04:18 
	*/
	BoSysPermission selectByFdCode(@Param(value = "fdCode")String fdCode);

}
