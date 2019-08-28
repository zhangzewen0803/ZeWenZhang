package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tahoecn.bo.common.enums.IsVirtualRoomEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.ListUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.SaleRoomStreamMapper;
import com.tahoecn.bo.model.entity.SaleRoomStream;
import com.tahoecn.bo.service.SaleRoomStreamService;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-06-12
 */
@Service
public class SaleRoomStreamServiceImpl extends ServiceImpl<SaleRoomStreamMapper, SaleRoomStream> implements SaleRoomStreamService {

	@Autowired
	private SaleRoomStreamMapper saleRoomStreamMapper;

	//拆分入库数量
	private final Integer SALE_ROOM_STREAM_SIZE = 3000;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveRoomStreamData(List<SaleRoomStream> saleRoomStreamList, Integer isAll) {
		List<SaleRoomStream> saleRoomStreamSaveList = new ArrayList<SaleRoomStream>();
		List<SaleRoomStream> saleRoomStreamUpdateList = new ArrayList<SaleRoomStream>();
		for (SaleRoomStream saleRoomStream : saleRoomStreamList) {
			if(isAll == 1){
				saleRoomStream.setId(UUIDUtils.create());
				saleRoomStream.setCreateTime(LocalDateTime.now());
				saleRoomStreamSaveList.add(saleRoomStream);
			}else if(isAll == 0) {
				SaleRoomStream saleRoomStreamOld = saleRoomStreamMapper.selectByRoomStreamGuid(saleRoomStream.getStreamGuid());
				if(saleRoomStreamOld == null) {
					saleRoomStream.setId(UUIDUtils.create());
					saleRoomStream.setCreateTime(LocalDateTime.now());
					saleRoomStreamSaveList.add(saleRoomStream);
				}else {
					saleRoomStream.setId(saleRoomStreamOld.getId());
					saleRoomStream.setUpdateTime(LocalDateTime.now());
					saleRoomStreamUpdateList.add(saleRoomStream);
				}
			}
		}

		//将用户数据集合拆分成3000长的的集合
		if(DataUtils.isNotEmpty(saleRoomStreamSaveList)) {
			List<List> subList = ListUtils.getSubList(saleRoomStreamSaveList, SALE_ROOM_STREAM_SIZE);
			for (List list : subList) {
				List<SaleRoomStream> streamSaveList =(List<SaleRoomStream>)list;
				saleRoomStreamMapper.batchInsertRoomStreamData(streamSaveList);
			}
		}

		if(DataUtils.isNotEmpty(saleRoomStreamUpdateList)) {
			List<List> subList = ListUtils.getSubList(saleRoomStreamUpdateList, SALE_ROOM_STREAM_SIZE);
			for (List list : subList) {
				List<SaleRoomStream> streamUpdateList =(List<SaleRoomStream>)list;
				saleRoomStreamMapper.batchUpdateRoomStreamData(streamUpdateList);
			}
		}
	}

	@Override
	public List<SaleRoomStream> getLastSaleRoomStreamListByBuildingOriginIds(Collection<String> buildingOriginIds, LocalDateTime endTime) {
		QueryWrapper<SaleRoomStream> queryWrapper = new QueryWrapper<>();
		queryWrapper
				.eq("is_virtual_room", IsVirtualRoomEnum.NO.getKey())
				.le("gx_time",endTime)
				.isNotNull("price")
				.in("main_data_bld_id",buildingOriginIds)
				.last("order by room_guid,gx_time desc");
		List<SaleRoomStream> list = list(queryWrapper);
		Map<String,SaleRoomStream> lastSaleStreamMap = new ConcurrentHashMap<>();
		if (CollectionUtils.isNotEmpty(list)){
			list.parallelStream().forEach(saleRoomStream -> {
				if (saleRoomStream.getGxTime() == null){
					return;
				}
				SaleRoomStream old = lastSaleStreamMap.get(saleRoomStream.getRoomGuid());
				if (old == null){
					SaleRoomStream last = lastSaleStreamMap.putIfAbsent(saleRoomStream.getRoomGuid(), saleRoomStream);
					if (last != null){
						old = last;
					}else {
						return;
					}
				}
				while (saleRoomStream.getGxTime().isAfter(old.getGxTime())){
					SaleRoomStream last = lastSaleStreamMap.put(saleRoomStream.getRoomGuid(), saleRoomStream);
					if (last == old){
						break;
					}
					old = last;
				}
			});
		}
		return lastSaleStreamMap.values().parallelStream().collect(Collectors.toList());
	}

}
