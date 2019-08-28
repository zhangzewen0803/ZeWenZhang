package com.tahoecn.bo.service;

import com.tahoecn.bo.model.entity.TutouThProductPositionTb;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * TUTOU-产品定位信息表 服务类
 * </p>
 *
 * @author panglx
 * @since 2019-05-29
 */
public interface TutouThProductPositionTbService extends IService<TutouThProductPositionTb> {

	/**
	 * @Title: batchSaveProductPositList 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param productPositList void
	 * @author liyongxu
	 * @date 2019年6月6日 下午3:33:58 
	*/
	void batchSaveProductPositList(List<TutouThProductPositionTb> productPositList);

}
