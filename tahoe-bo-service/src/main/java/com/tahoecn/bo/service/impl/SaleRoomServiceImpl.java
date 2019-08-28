package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsDeleteEnum;
import com.tahoecn.bo.common.enums.IsVirtualRoomEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.ListUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.SaleRoomMapper;
import com.tahoecn.bo.model.entity.SaleRoom;
import com.tahoecn.bo.service.SaleRoomService;

/**
 * <p>
 * 房间信息表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-18
 */
@Service
public class SaleRoomServiceImpl extends ServiceImpl<SaleRoomMapper, SaleRoom> implements SaleRoomService {

	@Autowired
	private SaleRoomMapper saleRoomMapper;
	
	//拆分入库数量
	private final Integer SALE_ROOM_SIZE = 3000;
	private final Integer SALE_ROOM_UPDATE_SIZE = 2000;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveRoomInfoData(List<SaleRoom> saleRoomInfoList, Integer isAll) {
		List<SaleRoom> saleRoomInfoSaveList = new ArrayList<SaleRoom>();
		List<SaleRoom> saleRoomInfoUpdateList = new ArrayList<SaleRoom>();
		for (SaleRoom saleRoom : saleRoomInfoList) {
			if(isAll == 1){
				saleRoom.setId(UUIDUtils.create());
				saleRoom.setCreateTime(LocalDateTime.now());
				saleRoomInfoSaveList.add(saleRoom);
			}else if(isAll == 0) {
				SaleRoom saleRoomOld = saleRoomMapper.selectByRoomGuid(saleRoom.getRoomGuid());
				if(saleRoomOld != null) {
					saleRoom.setId(saleRoomOld.getId());
					saleRoom.setUpdateTime(LocalDateTime.now());
					saleRoomInfoUpdateList.add(saleRoom);
				}else {
					saleRoom.setId(UUIDUtils.create());
					saleRoom.setCreateTime(LocalDateTime.now());
					saleRoomInfoSaveList.add(saleRoom);
				}
			}
		}
		
		//将用户数据集合拆分成3000长的的集合
		if(DataUtils.isNotEmpty(saleRoomInfoSaveList)) {
			List<List> subList = ListUtils.getSubList(saleRoomInfoSaveList, SALE_ROOM_SIZE);
		    for (List list : subList) {
		    	List<SaleRoom> roomInfoSaveList =(List<SaleRoom>)list;
		    	saleRoomMapper.batchInsertRoomInfoData(roomInfoSaveList);
			}	
		}
		
		if(DataUtils.isNotEmpty(saleRoomInfoUpdateList)) {
			List<List> subList = ListUtils.getSubList(saleRoomInfoUpdateList, SALE_ROOM_UPDATE_SIZE);
			for (List list : subList) {
		    	List<SaleRoom> roomInfoUpdateList =(List<SaleRoom>)list;
		    	saleRoomMapper.batchUpdateRoomInfoData(roomInfoUpdateList);
			}
		}
	}

	@Override
	public List<SaleRoom> getTakeCardSaleRoomListByBuildingOriginIds(Collection<String> buildingOriginIds, LocalDateTime endTime) {
		QueryWrapper<SaleRoom> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("is_virtual_room", IsVirtualRoomEnum.NO.getKey())
				.le("real_take_card_time",endTime)
				.eq("is_delete", IsDeleteEnum.NO.getKey())
				.in("main_data_bld_id",buildingOriginIds);
		return list(queryWrapper);
	}

	@Override
	public List<SaleRoom> getTakeCardSaleRoomListByRoomGuids(Collection<String> roomGuids, LocalDateTime endTime) {
		QueryWrapper<SaleRoom> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("is_virtual_room", IsVirtualRoomEnum.NO.getKey())
				.le("real_take_card_time",endTime)
				.eq("is_delete", IsDeleteEnum.NO.getKey())
				.in("room_guid",roomGuids);
		return list(queryWrapper);
	}

}
