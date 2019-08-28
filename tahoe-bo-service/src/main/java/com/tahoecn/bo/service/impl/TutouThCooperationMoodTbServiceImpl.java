package com.tahoecn.bo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.TutouThCooperationMoodTbMapper;
import com.tahoecn.bo.model.entity.TutouThCooperationMoodTb;
import com.tahoecn.bo.service.TutouThCooperationMoodTbService;

/**
 * <p>
 * TUTOU-合作方式表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThCooperationMoodTbServiceImpl extends ServiceImpl<TutouThCooperationMoodTbMapper, TutouThCooperationMoodTb> implements TutouThCooperationMoodTbService {

	@Autowired 
	private TutouThCooperationMoodTbMapper tutouThCooperationMoodTbMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveCooperationMoodList(List<TutouThCooperationMoodTb> cooperationMoodList) {
		List<TutouThCooperationMoodTb> cooperationMoodInsertList = new ArrayList<TutouThCooperationMoodTb>();
		List<TutouThCooperationMoodTb> cooperationMoodUpdateList = new ArrayList<TutouThCooperationMoodTb>();
		for (TutouThCooperationMoodTb tutouThCooperationMoodTb : cooperationMoodList) {
			TutouThCooperationMoodTb tutouThCooperationMoodTbOld = tutouThCooperationMoodTbMapper.selectBySourceId(tutouThCooperationMoodTb.getSourceId());
			if(tutouThCooperationMoodTbOld != null) {
				tutouThCooperationMoodTb.setId(tutouThCooperationMoodTbOld.getId());
				tutouThCooperationMoodTb.setUpdateTime(LocalDateTime.now());
				cooperationMoodUpdateList.add(tutouThCooperationMoodTb);
			}else {
				tutouThCooperationMoodTb.setId(UUIDUtils.create());
				tutouThCooperationMoodTb.setCreateTime(LocalDateTime.now());
				cooperationMoodInsertList.add(tutouThCooperationMoodTb);
			}
		}
		if (DataUtils.isNotEmpty(cooperationMoodUpdateList)) {
			tutouThCooperationMoodTbMapper.batchUpdateCooperationMoodList(cooperationMoodUpdateList);
		}
		if (DataUtils.isNotEmpty(cooperationMoodInsertList)) {
			tutouThCooperationMoodTbMapper.batchInsertCooperationMoodList(cooperationMoodInsertList);
		}
	}
	
}
