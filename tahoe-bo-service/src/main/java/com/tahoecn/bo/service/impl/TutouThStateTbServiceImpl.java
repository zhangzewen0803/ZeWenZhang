package com.tahoecn.bo.service.impl;

import com.tahoecn.bo.model.entity.TutouThStateTb;
import com.tahoecn.bo.common.utils.DataUtils;
import com.tahoecn.bo.common.utils.UUIDUtils;
import com.tahoecn.bo.mapper.TutouThStateTbMapper;
import com.tahoecn.bo.service.TutouThStateTbService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * TUTOU-投委会状态码表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThStateTbServiceImpl extends ServiceImpl<TutouThStateTbMapper, TutouThStateTb> implements TutouThStateTbService {

	@Autowired
	private TutouThStateTbMapper tutouThStateTbMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveStateList(List<TutouThStateTb> stateList) {
		List<TutouThStateTb> stateInsertList = new ArrayList<TutouThStateTb>();
		List<TutouThStateTb> stateUpdateList = new ArrayList<TutouThStateTb>();
		for (TutouThStateTb tutouThStateTb : stateList) {
			TutouThStateTb tutouThStateTbOld = tutouThStateTbMapper.selectBySourceId(tutouThStateTb.getSourceId());
			if(tutouThStateTbOld != null) {
				tutouThStateTb.setId(tutouThStateTbOld.getId());
				tutouThStateTb.setUpdateTime(LocalDateTime.now());
				stateUpdateList.add(tutouThStateTb);
			}else {
				tutouThStateTb.setId(UUIDUtils.create());
				tutouThStateTb.setCreateTime(LocalDateTime.now());
				stateInsertList.add(tutouThStateTb);
			}
		}
		if(DataUtils.isNotEmpty(stateUpdateList)) {
			tutouThStateTbMapper.batchUpdateStateList(stateUpdateList);
		}
		if(DataUtils.isNotEmpty(stateInsertList)) {
			tutouThStateTbMapper.batchInsertStateList(stateInsertList);
		}
	}

}
