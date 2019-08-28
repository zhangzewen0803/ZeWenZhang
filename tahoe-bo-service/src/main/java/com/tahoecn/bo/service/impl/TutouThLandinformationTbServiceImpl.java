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
import com.tahoecn.bo.mapper.TutouThLandinformationTbMapper;
import com.tahoecn.bo.model.entity.TutouThLandinformationTb;
import com.tahoecn.bo.service.TutouThLandinformationTbService;

/**
 * <p>
 * TUTOU-土地信息表-主表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThLandinformationTbServiceImpl extends ServiceImpl<TutouThLandinformationTbMapper, TutouThLandinformationTb> implements TutouThLandinformationTbService {

	@Autowired
	private TutouThLandinformationTbMapper tutouThLandinformationTbMapper;
	
	@Override
	public List<TutouThLandinformationTb> findLandinfoList() {
		return tutouThLandinformationTbMapper.selectLandinfoList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveLandinfoList(List<TutouThLandinformationTb> tTLandinfoList) {
		List<TutouThLandinformationTb> tTLandinfoInsertList = new ArrayList<TutouThLandinformationTb>();
		List<TutouThLandinformationTb> tTLandinfoUpdateList = new ArrayList<TutouThLandinformationTb>();
		for (TutouThLandinformationTb tutouThLandinformationTb : tTLandinfoList) {
			TutouThLandinformationTb tutouThLandinformationTbOld = tutouThLandinformationTbMapper.selectInfoByLandId(tutouThLandinformationTb.getLandId());
			if(tutouThLandinformationTbOld != null) {
				tutouThLandinformationTb.setId(tutouThLandinformationTbOld.getId());
				tutouThLandinformationTb.setUpdateTime(LocalDateTime.now());
				tTLandinfoUpdateList.add(tutouThLandinformationTb);
			}else {
				tutouThLandinformationTb.setId(UUIDUtils.create());
				tutouThLandinformationTb.setCreateTime(LocalDateTime.now());
				tTLandinfoInsertList.add(tutouThLandinformationTb);
			}
		}
		if (DataUtils.isNotEmpty(tTLandinfoUpdateList)) {
			tutouThLandinformationTbMapper.batchUpateLandinfoList(tTLandinfoUpdateList);
		}
		if (DataUtils.isNotEmpty(tTLandinfoInsertList)) {
			tutouThLandinformationTbMapper.batchInsertLandinfoList(tTLandinfoInsertList);
		}
	}
	
}
