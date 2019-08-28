package com.tahoecn.bo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.tahoecn.bo.mapper.TutouThBudgetTbMapper;
import com.tahoecn.bo.mapper.TutouThBusinessInfoTbMapper;
import com.tahoecn.bo.mapper.TutouThCooperationMoodTbMapper;
import com.tahoecn.bo.mapper.TutouThLandareaNewTbMapper;
import com.tahoecn.bo.mapper.TutouThLandareaTbMapper;
import com.tahoecn.bo.mapper.TutouThLandinformationTbMapper;
import com.tahoecn.bo.mapper.TutouThLanduseTbMapper;
import com.tahoecn.bo.mapper.TutouThProductPositionTbMapper;
import com.tahoecn.bo.mapper.TutouThStateTbMapper;
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

/**
 * <p>
 * TUTOU-土投 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
@DS("slave_02")
public class SyncTutouTaskServiceImpl implements SyncTutouTaskService {

	@Autowired
	private TutouThLandinformationTbMapper tutouThLandinformationTbMapper;
	
	@Autowired
	private TutouThLandareaTbMapper tutouThLandareaTbMapper;
	
	@Autowired
	private TutouThLandareaNewTbMapper tutouThLandareaNewTbMapper;
	
	@Autowired 
	private TutouThLanduseTbMapper tutouThLanduseTbMapper;
	
	@Autowired
	private TutouThBudgetTbMapper tutouThBudgetTbMapper;
	
	@Autowired
	private TutouThBusinessInfoTbMapper tutouThBusinessInfoTbMapper;
	
	@Autowired 
	private TutouThCooperationMoodTbMapper tutouThCooperationMoodTbMapper;
	
	@Autowired
	private TutouThProductPositionTbMapper tutouThProductPositionTbMapper;
	
	@Autowired
	private TutouThStateTbMapper tutouThStateTbMapper;
	
	@Override
	public List<TutouThLandinformationTb> findLandinfoListDS() {
		return tutouThLandinformationTbMapper.selectLandinfoListDS();
	}

	@Override
	public List<TutouThLandareaTb> findLandareaListDS() {
		return tutouThLandareaTbMapper.selectLandareaListDS();
	}
	
	@Override
	public List<TutouThLandareaNewTb> findLandareaNewListDS() {
		return tutouThLandareaNewTbMapper.selectLandareaNewListDS();
	}

	@Override
	public List<TutouThLanduseTb> findLanduseListDS() {
		return tutouThLanduseTbMapper.selectLanduseListDS();
	}

	@Override
	public List<TutouThStateTb> findStateListDS() {
		return tutouThStateTbMapper.selectStateListDS();
	}

	@Override
	public List<TutouThProductPositionTb> findProductPositListDS() {
		return tutouThProductPositionTbMapper.selectProductPositListDS();
	}

	@Override
	public List<TutouThCooperationMoodTb> findCooperationMoodListDS() {
		return tutouThCooperationMoodTbMapper.selectCooperationMoodListDS();
	}

	@Override
	public List<TutouThBusinessInfoTb> findBusinessInfoListDS() {
		return tutouThBusinessInfoTbMapper.selectBusinessInfoListDS();
	}

	@Override
	public List<TutouThBudgetTb> findBudgetListDS() {
		return tutouThBudgetTbMapper.selectBudgetListDS();
	}
}
