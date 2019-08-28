package com.tahoecn.bo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.TutouThLandareaTbMapper;
import com.tahoecn.bo.model.entity.TutouThLandareaTb;
import com.tahoecn.bo.service.TutouThLandareaTbService;

/**
 * <p>
 * TUTOU-土地面积表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThLandareaTbServiceImpl extends ServiceImpl<TutouThLandareaTbMapper, TutouThLandareaTb> implements TutouThLandareaTbService {

	@Autowired
	private TutouThLandareaTbMapper tutouThLandareaTbMapper;
	
	@Override
	public List<TutouThLandareaTb> findLandareaList() {
		return tutouThLandareaTbMapper.selectLandareaList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveLandareaList(List<TutouThLandareaTb> landareaList) {
		List<TutouThLandareaTb> landareaInsertList = new ArrayList<TutouThLandareaTb>();
		List<TutouThLandareaTb> landareaUpdateList = new ArrayList<TutouThLandareaTb>();
		for (TutouThLandareaTb tutouThLandareaTb : landareaList) {
			TutouThLandareaTb tutouThLandareaTbOld = tutouThLandareaTbMapper.selectBySourceId(tutouThLandareaTb.getSourceId());
			if(tutouThLandareaTbOld != null) {
				tutouThLandareaTb.setId(tutouThLandareaTbOld.getId());
				landareaUpdateList.add(tutouThLandareaTb);
			}else {
				tutouThLandareaTb.setId(UUIDUtils.create());
				landareaInsertList.add(tutouThLandareaTb);
			}
		}
		if(DataUtils.isNotEmpty(landareaUpdateList)) {
			tutouThLandareaTbMapper.batchUpdateLandareaList(landareaUpdateList);
		}
		if(DataUtils.isNotEmpty(landareaInsertList)) {
			tutouThLandareaTbMapper.batchInsertLandareaList(landareaInsertList);
		}
	}

}
