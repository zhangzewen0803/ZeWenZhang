package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.SaleRoomStream;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-12
 */
public interface SaleRoomStreamService extends IService<SaleRoomStream> {

	/**
	 * @Title: batchSaveRoomStreamData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomStream void
	 * @author liyongxu
	 * @param flag 
	 * @date 2019年6月17日 上午11:23:05 
	*/
	void batchSaveRoomStreamData(List<SaleRoomStream> saleRoomStreamList, Integer flag);


	/**
	 * 查最新的房间流水信息
	 * @param buildingOriginIds 楼栋ID集合
	 * @param endTime 截止时间
	 * @return
	 */
	List<SaleRoomStream> getLastSaleRoomStreamListByBuildingOriginIds(Collection<String> buildingOriginIds, LocalDateTime endTime);

}
