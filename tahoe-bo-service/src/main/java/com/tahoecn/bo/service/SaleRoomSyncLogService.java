package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.SaleRoomSyncLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 房间同步日志表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-12
 */
public interface SaleRoomSyncLogService extends IService<SaleRoomSyncLog> {

	/**
	 * @Title: saveSyncLogInfo 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomSyncLog void
	 * @author liyongxu
	 * @date 2019年6月26日 下午5:10:52 
	*/
	void saveSyncLogInfo(SaleRoomSyncLog saleRoomSyncLog);

	/**
	 * @Title: getSyncTimeStamp 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return SaleRoomSyncLog
	 * @author liyongxu
	 * @param type 
	 * @date 2019年6月28日 下午10:02:13 
	*/
	SaleRoomSyncLog getSyncTimeStamp(String type);

}
