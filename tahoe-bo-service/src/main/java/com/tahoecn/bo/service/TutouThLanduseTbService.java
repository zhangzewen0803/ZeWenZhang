package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.TutouThLanduseTb;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * TUTOU-土地性质表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLanduseTbService extends IService<TutouThLanduseTb> {

	/**
	 * @Title: batchSaveLandauseList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param landuseList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午2:40:51 
	*/
	void batchSaveLandauseList(List<TutouThLanduseTb> landuseList);

}
