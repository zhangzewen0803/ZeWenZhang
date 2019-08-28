package com.tahoecn.bo.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.TutouThLandareaTb;

/**
 * <p>
 * TUTOU-土地面积表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLandareaTbService extends IService<TutouThLandareaTb> {

	/**
	 * @Title: findLandareaList 
	 * @Description: 查询BO地块面积数据
	 * @return List<TutouThLandareaTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:35:14 
	*/
	List<TutouThLandareaTb> findLandareaList();
	
	/**
	 * @Title: batchSaveLandareaList 
	 * @Description: 保存地块面积信息
	 * @param landareaList void
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:38:00 
	*/
	void batchSaveLandareaList(List<TutouThLandareaTb> landareaList);
}
