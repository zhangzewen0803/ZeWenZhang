package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.TutouThStateTb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * TUTOU-投委会状态码表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThStateTbMapper extends BaseMapper<TutouThStateTb> {

	/**
	 * @Title: selectStateListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThStateTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:01:56 
	*/
	List<TutouThStateTb> selectStateListDS();

	/**
	 * @Title: batchInsertStateList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param stateList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:04:17 
	*/
	void batchInsertStateList(List<TutouThStateTb> stateList);

	/**
	 * @Title: selectBySourceId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param sourceId
	 * @return TutouThStateTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:27:53 
	*/
	TutouThStateTb selectBySourceId(@Param("sourceId")Integer sourceId);

	/**
	 * @Title: batchUpdateStateList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param stateUpdateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:27:58 
	*/
	void batchUpdateStateList(List<TutouThStateTb> stateUpdateList);

}
