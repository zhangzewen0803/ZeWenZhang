package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.TutouThLanduseTb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * TUTOU-土地性质表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLanduseTbMapper extends BaseMapper<TutouThLanduseTb> {

	/**
	 * @Title: selectLanduseListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThLanduseTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午2:42:39 
	*/
	List<TutouThLanduseTb> selectLanduseListDS();

	/**
	 * @Title: batchInsertLanduseList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landuseList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午2:47:07 
	*/
	void batchInsertLanduseList(List<TutouThLanduseTb> landuseList);

	/**
	 * @Title: selectByPurposeId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param purposeId
	 * @return TutouThLanduseTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:13:07 
	*/
	TutouThLanduseTb selectByPurposeId(@Param("purposeId")Integer purposeId);

	/**
	 * @Title: batchUpdateLanduseList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landuseUpdateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:13:12 
	*/
	void batchUpdateLanduseList(List<TutouThLanduseTb> landuseUpdateList);

}
