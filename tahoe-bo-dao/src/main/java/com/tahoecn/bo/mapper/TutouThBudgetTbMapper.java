package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.TutouThBudgetTb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * TUTOU-测算对比表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThBudgetTbMapper extends BaseMapper<TutouThBudgetTb> {

	/**
	 * @Title: selectBudgetListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThBudgetTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:35:12 
	*/
	List<TutouThBudgetTb> selectBudgetListDS();

	/**
	 * @Title: batchInsertBudgetList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param budgetList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午4:53:59 
	*/
	void batchInsertBudgetList(List<TutouThBudgetTb> budgetList);

	/**
	 * @Title: selectByBudgetId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param budgetId void
	 * @author liyongxu
	 * @date 2019年6月12日 上午9:55:12 
	*/
	TutouThBudgetTb selectByBudgetId(@Param("budgetId") Integer budgetId);

	/**
	 * @Title: batchUpdateBudgetList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param budgetUpdateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午9:59:24 
	*/
	void batchUpdateBudgetList(List<TutouThBudgetTb> budgetUpdateList);

}
