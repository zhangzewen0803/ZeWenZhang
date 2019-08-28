package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.TutouThBusinessInfoTb;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThBusinessInfoTbService extends IService<TutouThBusinessInfoTb> {

	/**
	 * @Title: batchSaveBusinessInfoList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param businessInfoList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:03:33 
	*/
	void batchSaveBusinessInfoList(List<TutouThBusinessInfoTb> businessInfoList);

}
