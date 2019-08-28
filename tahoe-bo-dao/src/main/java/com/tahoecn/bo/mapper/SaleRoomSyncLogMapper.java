package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.SaleRoomSyncLog;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 房间同步日志表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-12
 */
public interface SaleRoomSyncLogMapper extends BaseMapper<SaleRoomSyncLog> {

	/**
	 * @Title: selectSyncTimeStamp 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param type
	 * @return SaleRoomSyncLog
	 * @author liyongxu
	 * @date 2019年6月28日 下午10:05:05 
	*/
	SaleRoomSyncLog selectSyncTimeStamp(@Param(value = "type")String type);

}
