package com.tahoecn.bo.service.impl;

import com.tahoecn.bo.model.entity.SaleRoomSyncLog;
import com.tahoecn.bo.mapper.SaleRoomSyncLogMapper;
import com.tahoecn.bo.service.SaleRoomSyncLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 房间同步日志表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-12
 */
@Service
public class SaleRoomSyncLogServiceImpl extends ServiceImpl<SaleRoomSyncLogMapper, SaleRoomSyncLog> implements SaleRoomSyncLogService {

	@Autowired
	private SaleRoomSyncLogMapper saleRoomSyncLogMapper;
	
	@Override
	public void saveSyncLogInfo(SaleRoomSyncLog saleRoomSyncLog) {
		saleRoomSyncLogMapper.insert(saleRoomSyncLog);
	}

	@Override
	public SaleRoomSyncLog getSyncTimeStamp(String type) {
		return saleRoomSyncLogMapper.selectSyncTimeStamp(type);
	}

}
