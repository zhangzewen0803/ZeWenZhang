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
import com.tahoecn.bo.mapper.TutouThBudgetTbMapper;
import com.tahoecn.bo.model.entity.TutouThBudgetTb;
import com.tahoecn.bo.service.TutouThBudgetTbService;

/**
 * <p>
 * TUTOU-测算对比表 服务实现类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
@Service
public class TutouThBudgetTbServiceImpl extends ServiceImpl<TutouThBudgetTbMapper, TutouThBudgetTb> implements TutouThBudgetTbService {

	@Autowired
	private TutouThBudgetTbMapper tutouThBudgetTbMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSaveBudgetList(List<TutouThBudgetTb> budgetList) {
		List<TutouThBudgetTb> budgetNewList = new ArrayList<TutouThBudgetTb>();
		List<TutouThBudgetTb> budgetUpdateList = new ArrayList<TutouThBudgetTb>();
		for (TutouThBudgetTb tutouThBudgetTb : budgetList) {
			TutouThBudgetTb tutouThBudgetTbOld = tutouThBudgetTbMapper.selectByBudgetId(tutouThBudgetTb.getBudgetId());
			if(tutouThBudgetTbOld != null) {
				tutouThBudgetTb.setId(tutouThBudgetTbOld.getId());
				tutouThBudgetTb.setUpdateTime(LocalDateTime.now());
				budgetUpdateList.add(tutouThBudgetTb);
			}else {
				tutouThBudgetTb.setId(UUIDUtils.create());
				tutouThBudgetTb.setCreateTime(LocalDateTime.now());
				budgetNewList.add(tutouThBudgetTb);
			}
		}
		if(DataUtils.isNotEmpty(budgetUpdateList)) {
			tutouThBudgetTbMapper.batchUpdateBudgetList(budgetUpdateList);
		}
		if (DataUtils.isNotEmpty(budgetNewList)) {
			tutouThBudgetTbMapper.batchInsertBudgetList(budgetNewList);
		}
	}
	
}
