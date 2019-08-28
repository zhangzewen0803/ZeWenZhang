package com.tahoecn.bo.service;

import java.util.List;

import com.tahoecn.bo.model.entity.TutouThBudgetTb;
import com.tahoecn.bo.model.entity.TutouThBusinessInfoTb;
import com.tahoecn.bo.model.entity.TutouThCooperationMoodTb;
import com.tahoecn.bo.model.entity.TutouThLandareaNewTb;
import com.tahoecn.bo.model.entity.TutouThLandareaTb;
import com.tahoecn.bo.model.entity.TutouThLandinformationTb;
import com.tahoecn.bo.model.entity.TutouThLanduseTb;
import com.tahoecn.bo.model.entity.TutouThProductPositionTb;
import com.tahoecn.bo.model.entity.TutouThStateTb;

/**
 * <p>
 * TUTOU-土投 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface SyncTutouTaskService{

	/**
	 * @Title: findLandinfoListDS 
	 * @Description: 查询土投地块信息
	 * @return List<TutouThLandinformationTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:35:16 
	*/
	List<TutouThLandinformationTb> findLandinfoListDS();
	
	/**
	 * @Title: findLandareaListDS 
	 * @Description: 查询土投地块面积信息
	 * @return List<TutouThLandareaTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:35:16 
	*/
	List<TutouThLandareaTb> findLandareaListDS();
	
	/**
	 * @Title: findLandareaListDS 
	 * @Description: 查询土投地块面积信息
	 * @return List<TutouThLandareaTb>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:35:16 
	*/
	List<TutouThLandareaNewTb> findLandareaNewListDS();

	/**
	 * @Title: findLanduseListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThLanduseTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午2:39:58 
	*/
	List<TutouThLanduseTb> findLanduseListDS();

	/**
	 * @Title: findStateListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThStateTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:00:58 
	*/
	List<TutouThStateTb> findStateListDS();

	/**
	 * @Title: findProductPositListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThProductPositionTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:33:15 
	*/
	List<TutouThProductPositionTb> findProductPositListDS();

	/**
	 * @Title: findCooperationMoodListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThCooperationMoodTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:47:20 
	*/
	List<TutouThCooperationMoodTb> findCooperationMoodListDS();

	/**
	 * @Title: findBusinessInfoListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThBusinessInfoTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:02:00 
	*/
	List<TutouThBusinessInfoTb> findBusinessInfoListDS();

	/**
	 * @Title: findBudgetListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThBudgetTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:34:34 
	*/
	List<TutouThBudgetTb> findBudgetListDS();
}
