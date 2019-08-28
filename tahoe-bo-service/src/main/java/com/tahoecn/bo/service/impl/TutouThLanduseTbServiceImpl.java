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
import com.tahoecn.bo.mapper.TutouThLanduseTbMapper;
import com.tahoecn.bo.model.entity.TutouThLanduseTb;
import com.tahoecn.bo.service.TutouThLanduseTbService;

/**
 * <p>
 * TUTOU-土地性质表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThLanduseTbServiceImpl extends ServiceImpl<TutouThLanduseTbMapper, TutouThLanduseTb> implements TutouThLanduseTbService {

	@Autowired
	private TutouThLanduseTbMapper tutouThLanduseTbMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveLandauseList(List<TutouThLanduseTb> landuseList) {
		List<TutouThLanduseTb> landuseInsertList = new ArrayList<TutouThLanduseTb>();
		List<TutouThLanduseTb> landuseUpdateList = new ArrayList<TutouThLanduseTb>();
		for (TutouThLanduseTb tutouThLanduseTb : landuseList) {
			TutouThLanduseTb tutouThLanduseTbOld = tutouThLanduseTbMapper.selectByPurposeId(tutouThLanduseTb.getPurposeId());
			if(tutouThLanduseTbOld != null) {
				tutouThLanduseTb.setId(tutouThLanduseTbOld.getId());
				tutouThLanduseTb.setUpdateTime(LocalDateTime.now());
				landuseUpdateList.add(tutouThLanduseTb);
			}else {
				tutouThLanduseTb.setId(UUIDUtils.create());
				tutouThLanduseTb.setCreateTime(LocalDateTime.now());
				landuseInsertList.add(tutouThLanduseTb);
			}
		}
		if (DataUtils.isNotEmpty(landuseUpdateList)) {
			tutouThLanduseTbMapper.batchUpdateLanduseList(landuseUpdateList);
		}
		if (DataUtils.isNotEmpty(landuseInsertList)) {
			tutouThLanduseTbMapper.batchInsertLanduseList(landuseInsertList);
		}
	}

}
