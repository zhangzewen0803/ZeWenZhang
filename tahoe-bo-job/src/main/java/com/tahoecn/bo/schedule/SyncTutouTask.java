package com.tahoecn.bo.schedule;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.model.entity.TutouThBudgetTb;
import com.tahoecn.bo.model.entity.TutouThBusinessInfoTb;
import com.tahoecn.bo.model.entity.TutouThCooperationMoodTb;
import com.tahoecn.bo.model.entity.TutouThLandareaNewTb;
import com.tahoecn.bo.model.entity.TutouThLandareaTb;
import com.tahoecn.bo.model.entity.TutouThLandinformationTb;
import com.tahoecn.bo.model.entity.TutouThLanduseTb;
import com.tahoecn.bo.model.entity.TutouThProductPositionTb;
import com.tahoecn.bo.model.entity.TutouThStateTb;
import com.tahoecn.bo.service.SyncTutouTaskService;
import com.tahoecn.bo.service.TutouThBudgetTbService;
import com.tahoecn.bo.service.TutouThBusinessInfoTbService;
import com.tahoecn.bo.service.TutouThCooperationMoodTbService;
import com.tahoecn.bo.service.TutouThLandareaNewTbService;
import com.tahoecn.bo.service.TutouThLandareaTbService;
import com.tahoecn.bo.service.TutouThLandinformationTbService;
import com.tahoecn.bo.service.TutouThLanduseTbService;
import com.tahoecn.bo.service.TutouThProductPositionTbService;
import com.tahoecn.bo.service.TutouThStateTbService;

@Component
public class SyncTutouTask {

	@Autowired 
	private SyncTutouTaskService syncTutouTaskService;
	
	@Autowired 
	private TutouThLandinformationTbService tutouThLandinformationTbService;
	
	@Autowired
	private TutouThLandareaTbService tutouThLandareaTbService;
	
	@Autowired
	private TutouThLandareaNewTbService tutouThLandareaNewTbService;
	
	@Autowired 
	private TutouThLanduseTbService tutouThLanduseTbService;
	
	@Autowired
	private TutouThBudgetTbService tutouThBudgetTbService;
	
	@Autowired
	private TutouThBusinessInfoTbService tutouThBusinessInfoTbService;
	
	@Autowired 
	private TutouThCooperationMoodTbService tutouThCooperationMoodTbService;
	
	@Autowired
	private TutouThProductPositionTbService tutouThProductPositionTbService;
	
	@Autowired
	private TutouThStateTbService tutouThStateTbService;
	
	/**
	 * @Title: getLandInfoList 
	 * @Description: 同步土投地块信息
	 * @return JSONResult<List<TutouThLandinformationTb>>
	 * @author liyongxu
	 * @date 2019年6月5日 下午8:30:11 
	*/
//	@Scheduled(cron = "0 0 1 * * ?")
	public void getLandInfoListDS() {
		List<TutouThLandinformationTb> landinfoList = syncTutouTaskService.findLandinfoListDS();
		if(DataUtils.isNotEmpty(landinfoList)) {
			tutouThLandinformationTbService.batchSaveLandinfoList(landinfoList);
		}
	}
	
	/**
	 * @Title: getLandareaListDS 
	 * @Description: 同步土投地块面积信息列表
	 * @return JSONResult<List<TutouThLandareaTb>>
	 * @author liyongxu
	 * @date 2019年6月6日 上午10:59:05 
	*/
//	@Scheduled(cron = "0 0 1 * * ?")
	public void getLandareaListDS() {
		List<TutouThLandareaTb> landareaList = syncTutouTaskService.findLandareaListDS();
		if(DataUtils.isNotEmpty(landareaList)) {
			tutouThLandareaTbService.batchSaveLandareaList(landareaList);
		}
	}
	
	/**
	 * @Title: getLandareaNewListDS 
	 * @Description: 同步土投地块面积New信息列表
	 * @author liyongxu
	 * @date 2019年6月6日 下午2:40:27 
	*/
//    @Scheduled(cron = "0 0 1 * * ?")
	public void getLandareaNewListDS() {
		List<TutouThLandareaNewTb> landareaNewList = syncTutouTaskService.findLandareaNewListDS();
		if(DataUtils.isNotEmpty(landareaNewList)) {
			tutouThLandareaNewTbService.batchSaveLandareaNewList(landareaNewList);
		}
	}
	
	/**
	 * @Title: getLanduseListDS 
	 * @Description: 同步土地性质表
	 * @author liyongxu
	 * @date 2019年6月6日 下午2:40:23 
	*/
//	@Scheduled(cron = "0 0 1 * * ?")
	public void getLanduseListDS() {
		List<TutouThLanduseTb> landuseList = syncTutouTaskService.findLanduseListDS();
		if(DataUtils.isNotEmpty(landuseList)) {
			tutouThLanduseTbService.batchSaveLandauseList(landuseList);
		}
	}
	
	/**
	 * @Title: getStateListDS 
	 * @Description: 同步投委会状态码表
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:08:48 
	*/
//    @Scheduled(cron = "0 0 1 * * ?")
	public void getStateListDS() {
		List<TutouThStateTb> stateList = syncTutouTaskService.findStateListDS();
		if(DataUtils.isNotEmpty(stateList)) {
			tutouThStateTbService.batchSaveStateList(stateList);
		}
	}

	/**
	 * @Title: getProductPositListDS 
	 * @Description: 同步产品定位信息表
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:40:56 
	*/
//    @Scheduled(cron = "0 0 1 * * ?")
	public void getProductPositListDS() {
		List<TutouThProductPositionTb> productPositList = syncTutouTaskService.findProductPositListDS();
		if(DataUtils.isNotEmpty(productPositList)) {
			tutouThProductPositionTbService.batchSaveProductPositList(productPositList);
		}
	}
	
	/**
	 * @Title: getCooperationMoodListDS 
	 * @Description: 同步合作方式表
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:54:42 
	*/
//    @Scheduled(cron = "0 0 1 * * ?")
	public void getCooperationMoodListDS() {
		List<TutouThCooperationMoodTb> cooperationMoodList = syncTutouTaskService.findCooperationMoodListDS();
		if(DataUtils.isNotEmpty(cooperationMoodList)) {
			tutouThCooperationMoodTbService.batchSaveCooperationMoodList(cooperationMoodList);
		}
	}
	
	/**
	 * @Title: getBusinessInfoListDS 
	 * @Description: 同步商务信息表
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:14:51 
	*/
//    @Scheduled(cron = "0 0 1 * * ?")
	public void getBusinessInfoListDS() {
		List<TutouThBusinessInfoTb> businessInfoList = syncTutouTaskService.findBusinessInfoListDS();
		if(DataUtils.isNotEmpty(businessInfoList)) {
			tutouThBusinessInfoTbService.batchSaveBusinessInfoList(businessInfoList);
		}
	}
	
	/**
	 * @Title: getBudgetListDS 
	 * @Description: 同步测算对比表
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:33:59 
	*/
//    @Scheduled(cron = "0 0 1 * * ?")
	public void getBudgetListDS() {
		List<TutouThBudgetTb> budgetList = syncTutouTaskService.findBudgetListDS();
		if(DataUtils.isNotEmpty(budgetList)) {
			tutouThBudgetTbService.batchSaveBudgetList(budgetList);
		}
	}
	
}
