package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.TutouThBusinessInfoTb;
import com.tahoecn.bo.model.entity.TutouThCooperationMoodTb;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * TUTOU-合作方式表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThCooperationMoodTbService extends IService<TutouThCooperationMoodTb> {

	/**
	 * @Title: batchSaveCooperationMoodList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param cooperationMoodList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:47:26 
	*/
	void batchSaveCooperationMoodList(List<TutouThCooperationMoodTb> cooperationMoodList);

}
