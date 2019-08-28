package com.tahoecn.bo.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.SaleRoomStream;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * <p>
 * 房间表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-06-12
 */
public interface SaleRoomStreamMapper extends BaseMapper<SaleRoomStream> {

	/**
	 * @Title: batchInsertRoomStreamData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomStream void
	 * @author liyongxu
	 * @date 2019年6月17日 上午11:24:48 
	*/
	void batchInsertRoomStreamData(List<SaleRoomStream> saleRoomStream);
	
	/**
	 * @Title: batchUpdateRoomStreamData 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saleRoomStream void
	 * @author liyongxu
	 * @date 2019年7月10日 下午4:15:24 
	*/
	void batchUpdateRoomStreamData(List<SaleRoomStream> saleRoomStream);

	/**
	 * @Title: selectByRoomStreamGuid 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param roomGuid
	 * @return SaleRoomStream
	 * @author liyongxu
	 * @date 2019年7月5日 上午10:37:06 
	*/
	SaleRoomStream selectByRoomStreamGuid(@Param(value = "streamGuid")String streamGuid);

}
