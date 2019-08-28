package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.TutouThCooperationMoodTb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * TUTOU-合作方式表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThCooperationMoodTbMapper extends BaseMapper<TutouThCooperationMoodTb> {

	/**
	 * @Title: selectCooperationMoodListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThCooperationMoodTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:48:21 
	*/
	List<TutouThCooperationMoodTb> selectCooperationMoodListDS();

	/**
	 * @Title: batchInsertCooperationMoodList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param cooperationMoodList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:50:04 
	*/
	void batchInsertCooperationMoodList(List<TutouThCooperationMoodTb> cooperationMoodList);

	/**
	 * @Title: selectBySourceId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param sourceId
	 * @return TutouThCooperationMoodTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午10:33:57 
	*/
	TutouThCooperationMoodTb selectBySourceId(@Param("sourceId")Integer sourceId);

	/**
	 * @Title: batchUpdateCooperationMoodList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param cooperationMoodUpdateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午10:34:01 
	*/
	void batchUpdateCooperationMoodList(List<TutouThCooperationMoodTb> cooperationMoodUpdateList);

}
