package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.TutouThStateTb;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * TUTOU-投委会状态码表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThStateTbService extends IService<TutouThStateTb> {

	/**
	 * @Title: batchSaveStateList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param stateList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:01:04 
	*/
	void batchSaveStateList(List<TutouThStateTb> stateList);

}
