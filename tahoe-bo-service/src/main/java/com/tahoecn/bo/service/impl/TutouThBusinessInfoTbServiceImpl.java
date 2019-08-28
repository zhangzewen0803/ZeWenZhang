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
import com.tahoecn.bo.mapper.TutouThBusinessInfoTbMapper;
import com.tahoecn.bo.model.entity.TutouThBusinessInfoTb;
import com.tahoecn.bo.service.TutouThBusinessInfoTbService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThBusinessInfoTbServiceImpl extends ServiceImpl<TutouThBusinessInfoTbMapper, TutouThBusinessInfoTb> implements TutouThBusinessInfoTbService {

	@Autowired
	private TutouThBusinessInfoTbMapper tutouThBusinessInfoTbMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveBusinessInfoList(List<TutouThBusinessInfoTb> businessInfoList) {
		List<TutouThBusinessInfoTb> businessInsertList = new ArrayList<TutouThBusinessInfoTb>();
		List<TutouThBusinessInfoTb> businessUpdateList = new ArrayList<TutouThBusinessInfoTb>();
		for (TutouThBusinessInfoTb tutouThBusinessInfoTb : businessInfoList) {
			TutouThBusinessInfoTb tutouThBusinessInfoTbOld = tutouThBusinessInfoTbMapper.selectByBusinessInfoId(tutouThBusinessInfoTb.getBusinessInfoId());
			if(tutouThBusinessInfoTbOld != null) {
				tutouThBusinessInfoTb.setId(tutouThBusinessInfoTbOld.getId());
				tutouThBusinessInfoTb.setUpdateTime(LocalDateTime.now());
				businessUpdateList.add(tutouThBusinessInfoTb);
			}else {
				tutouThBusinessInfoTb.setId(UUIDUtils.create());
				tutouThBusinessInfoTb.setCreateTime(LocalDateTime.now());
				businessInsertList.add(tutouThBusinessInfoTb);
			}
		}
		if(DataUtils.isNotEmpty(businessUpdateList)) {
			tutouThBusinessInfoTbMapper.batchUpdateBusinessInfoList(businessUpdateList);
		}
		if (DataUtils.isNotEmpty(businessInsertList)) {
			tutouThBusinessInfoTbMapper.batchInsertBusinessInfoList(businessInsertList);
		}
	}
	
}
