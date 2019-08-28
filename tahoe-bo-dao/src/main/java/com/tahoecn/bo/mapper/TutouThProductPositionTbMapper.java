package com.tahoecn.bo.mapper;

import com.tahoecn.bo.model.entity.TutouThProductPositionTb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * TUTOU-产品定位信息表 Mapper 接口
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThProductPositionTbMapper extends BaseMapper<TutouThProductPositionTb> {

	/**
	 * @Title: selectProductPositListDS 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return List<TutouThProductPositionTb>
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:33:51 
	*/
	List<TutouThProductPositionTb> selectProductPositListDS();

	/**
	 * @Title: batchInsertProductPositList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productPositList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:35:16 
	*/
	void batchInsertProductPositList(List<TutouThProductPositionTb> productPositList);

	/**
	 * @Title: selectByProductId 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productId
	 * @return TutouThProductPositionTb
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:21:50 
	*/
	TutouThProductPositionTb selectByProductId(@Param("productId")Integer productId);

	/**
	 * @Title: batchUpdateProductPositList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productPositUpdateList void
	 * @author liyongxu
	 * @date 2019年6月12日 上午11:21:54 
	*/
	void batchUpdateProductPositList(List<TutouThProductPositionTb> productPositUpdateList);

}
