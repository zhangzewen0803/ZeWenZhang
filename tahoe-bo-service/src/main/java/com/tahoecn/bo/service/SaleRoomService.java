package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.SaleRoom;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 房间信息表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-06-18
 */
public interface SaleRoomService extends IService<SaleRoom> {

	/**
	 * @Title: batchSaveRoomInfoData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomStreamList void
	 * @author liyongxu
	 * @param flag 
	 * @date 2019年6月18日 下午3:56:46 
	*/
	void batchSaveRoomInfoData(List<SaleRoom> saleRoomInfoList, Integer flag);


	/**
	 * 查询已取证的房源
	 * @param buildingOriginIds 楼栋ID列表
	 * @param endTime 截止时间
	 * @return
	 */
	List<SaleRoom> getTakeCardSaleRoomListByBuildingOriginIds(Collection<String> buildingOriginIds, LocalDateTime endTime);

	/**
	 * 查已取证的房间信息
	 * @param roomGuids 房间ID集合
	 * @param endTime 截止时间
	 * @return
	 */
	List<SaleRoom> getTakeCardSaleRoomListByRoomGuids(Collection<String> roomGuids, LocalDateTime endTime);





}
