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
import com.tahoecn.bo.mapper.TutouThLandareaNewTbMapper;
import com.tahoecn.bo.model.entity.TutouThLandareaNewTb;
import com.tahoecn.bo.service.TutouThLandareaNewTbService;

/**
 * <p>
 * TUTOU-地块信息-新 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThLandareaNewTbServiceImpl extends ServiceImpl<TutouThLandareaNewTbMapper, TutouThLandareaNewTb> implements TutouThLandareaNewTbService {

	@Autowired
	private TutouThLandareaNewTbMapper tutouThLandareaNewTbMapper;
	
	@Override
	public List<TutouThLandareaNewTb> findLandareaNewList() {
		return tutouThLandareaNewTbMapper.selectLandareaNewList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveLandareaNewList(List<TutouThLandareaNewTb> landareaNewList) {
		List<TutouThLandareaNewTb> landareaNewInsertList = new ArrayList<TutouThLandareaNewTb>();
		List<TutouThLandareaNewTb> landareaNewUpateList = new ArrayList<TutouThLandareaNewTb>();
		for (TutouThLandareaNewTb tutouThLandareaNewTb : landareaNewList) {
			TutouThLandareaNewTb tutouThLandareaNewTbOld = tutouThLandareaNewTbMapper.selectBySourceId(tutouThLandareaNewTb.getSourceId());
			if(tutouThLandareaNewTbOld != null) {
				tutouThLandareaNewTb.setId(tutouThLandareaNewTbOld.getId());
				tutouThLandareaNewTb.setUpdateTime(LocalDateTime.now());
				landareaNewUpateList.add(tutouThLandareaNewTb);
			}else {
				tutouThLandareaNewTb.setId(UUIDUtils.create());
				tutouThLandareaNewTb.setCreateTime(LocalDateTime.now());
				landareaNewInsertList.add(tutouThLandareaNewTb);
			}
		}
		if(DataUtils.isNotEmpty(landareaNewUpateList)) {
			tutouThLandareaNewTbMapper.batchUpdateLandareaNewList(landareaNewUpateList);
		}
		if(DataUtils.isNotEmpty(landareaNewInsertList)) {
			tutouThLandareaNewTbMapper.batchInsertLandareaNewList(landareaNewInsertList);
		}
	}

}
