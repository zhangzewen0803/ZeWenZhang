package com.tahoecn.bo.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.TutouThLandareaNewTb;

/**
 * <p>
 * TUTOU-地块信息-新 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLandareaNewTbService extends IService<TutouThLandareaNewTb> {

	/**
	 * @Title: findLandareaList 
	 * @Description: 查询BO地块面积数据
	 * @return List<TutouThLandareaNewTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:35:14 
	*/
	List<TutouThLandareaNewTb> findLandareaNewList();
	
	/**
	 * @Title: batchSaveLandareaList 
	 * @Description: 保存地块面积信息
	 * @param landareaList void
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:38:00 
	*/
	void batchSaveLandareaNewList(List<TutouThLandareaNewTb> landareaNewList);
}
