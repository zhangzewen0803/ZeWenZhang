package com.tahoecn.bo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.TutouThBusinessInfoTb;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThBusinessInfoTbMapper extends BaseMapper<TutouThBusinessInfoTb> {

	/**
	 * @Title: selectBusinessInfoListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThBusinessInfoTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:02:46 
	*/
	List<TutouThBusinessInfoTb> selectBusinessInfoListDS();

	/**
	 * @Title: batchInsertBusinessInfoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param businessInfoList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:04:35 
	*/
	void batchInsertBusinessInfoList(List<TutouThBusinessInfoTb> businessInfoList);

	/**
	 * @Title: selectByBusinessInfoId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param businessInfoId
	 * @return TutouThBusinessInfoTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午10:11:34 
	*/
	TutouThBusinessInfoTb selectByBusinessInfoId(@Param("businessInfoId")Integer businessInfoId);

	/**
	 * @Title: batchUpdateBusinessInfoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param businessInfoList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午10:11:41 
	*/
	void batchUpdateBusinessInfoList(List<TutouThBusinessInfoTb> businessInfoList);

}
