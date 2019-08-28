package com.tahoecn.bo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tahoecn.bo.model.entity.TutouThLandareaNewTb;

/**
 * <p>
 * TUTOU-地块信息-新 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLandareaNewTbMapper extends BaseMapper<TutouThLandareaNewTb> {

	/**
	 * @Title: selectLandareaList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThLandareaTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:43:18 
	*/
	List<TutouThLandareaNewTb> selectLandareaNewList();
	
	List<TutouThLandareaNewTb> selectLandareaNewListDS();

	/**
	 * @Title: batchInsertLandareaList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landareaList void
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:43:27 
	*/
	void batchInsertLandareaNewList(List<TutouThLandareaNewTb> landareaNewList);

	/**
	 * @Title: selectByLandNatureId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landNatureId
	 * @return TutouThLandareaNewTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午10:48:06 
	*/
	TutouThLandareaNewTb selectBySourceId(@Param("sourceId")Integer sourceId);

	/**
	 * @Title: batchUpdateLandareaNewList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landareaUpateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午10:48:10 
	*/
	void batchUpdateLandareaNewList(List<TutouThLandareaNewTb> landareaUpateList);

}
