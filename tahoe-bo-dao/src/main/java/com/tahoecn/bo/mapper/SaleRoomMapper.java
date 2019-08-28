package com.tahoecn.bo.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.SaleRoom;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * <p>
 * 房间信息表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-18
 */
public interface SaleRoomMapper extends BaseMapper<SaleRoom> {

	/**
	 * @Title: batchInsertRoomInfoData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomStreamList void
	 * @author liyongxu
	 * @date 2019年6月18日 下午3:59:18 
	*/
	void batchInsertRoomInfoData(List<SaleRoom> saleRoomStreamList);

	/**
	 * @Title: batchUpdateRoomInfoData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomStreamUpdateList void
	 * @author liyongxu
	 * @date 2019年6月18日 下午4:14:47 
	*/
	void batchUpdateRoomInfoData(List<SaleRoom> saleRoomStreamUpdateList);

	/**
	 * @Title: selectByRoomGuid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param roomGuid
	 * @return SaleRoom
	 * @author liyongxu
	 * @date 2019年6月18日 下午9:57:36 
	*/
	SaleRoom selectByRoomGuid(@Param(value = "roomGuid")String roomGuid);

}
