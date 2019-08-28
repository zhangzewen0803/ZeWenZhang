package com.tahoecn.bo.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tahoecn.bo.model.entity.TutouThLandinformationTb;

/**
 * <p>
 * TUTOU-土地信息表-主表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThLandinformationTbService extends IService<TutouThLandinformationTb> {

	/**
	 * @Title: findLandinfoList 
	 * @Description: 查询BO地块数据
	 * @return List<TutouThLandinformationTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:35:14 
	*/
	List<TutouThLandinformationTb> findLandinfoList();
	
	/**
	 * @Title: batchLandinfoList 
	 * @Description: 保存地块信息
	 * @param findLandinfoList void
	 * @author liyongxu
	 * @date 2019年6月5日 下午8:42:56 
	*/
	void batchSaveLandinfoList(List<TutouThLandinformationTb> landinfoList);
	
}
