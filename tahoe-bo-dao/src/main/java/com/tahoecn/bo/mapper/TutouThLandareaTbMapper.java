package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.TutouThLandareaTb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * TUTOU-土地面积表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLandareaTbMapper extends BaseMapper<TutouThLandareaTb> {

	/**
	 * @Title: selectLandareaList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThLandareaTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:43:18 
	*/
	List<TutouThLandareaTb> selectLandareaList();
	
	List<TutouThLandareaTb> selectLandareaListDS();

	/**
	 * @Title: batchInsertLandareaList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landareaList void
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:43:27 
	*/
	void batchInsertLandareaList(List<TutouThLandareaTb> landareaList);

	/**
	 * @Title: selectBySourceId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param sourceId
	 * @return TutouThLandareaTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:03:49 
	*/
	TutouThLandareaTb selectBySourceId(@Param("sourceId")Integer sourceId);

	/**
	 * @Title: batchUpdateLandareaList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landareaUpdateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:03:54 
	*/
	void batchUpdateLandareaList(List<TutouThLandareaTb> landareaUpdateList);

}
